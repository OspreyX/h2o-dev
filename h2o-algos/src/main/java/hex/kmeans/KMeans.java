package hex.kmeans;

import hex.ClusteringModelBuilder;
import hex.Model;
import hex.ModelMetricsClustering;
import hex.schemas.KMeansV3;
import hex.schemas.ModelBuilderSchema;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import water.*;
import water.H2O.H2OCountedCompleter;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scalable K-Means++ (KMeans||)<br>
 * http://theory.stanford.edu/~sergei/papers/vldb12-kmpar.pdf<br>
 * http://www.youtube.com/watch?v=cigXAxV3XcY
 */
public class KMeans extends ClusteringModelBuilder<KMeansModel,KMeansModel.KMeansParameters,KMeansModel.KMeansOutput> {
  @Override public Model.ModelCategory[] can_build() {
    return new Model.ModelCategory[]{ Model.ModelCategory.Clustering };
  }

  @Override public BuilderVisibility builderVisibility() { return BuilderVisibility.Stable; };

  public enum Initialization {
    Random, PlusPlus, Furthest, User
  }

  // Convergence tolerance
  final private double TOLERANCE = 1e-6;

  // Called from an http request
  public KMeans(Key dest, String desc, KMeansModel.KMeansParameters parms) { super(dest, desc, parms); init(false); }
  public KMeans( KMeansModel.KMeansParameters parms ) { super("K-means",parms); init(false); }

  public ModelBuilderSchema schema() { return new KMeansV3(); }

  @Override
  protected void checkMemoryFootPrint() {
    long mem_usage = 8 /*doubles*/ * _parms._k * _train.numCols() * (_parms._standardize ? 2 : 1);
    long max_mem = H2O.SELF.get_max_mem();
    if (mem_usage > max_mem) {
      String msg = "Centroids won't fit in the driver node's memory ("
              + PrettyPrint.bytes(mem_usage) + " > " + PrettyPrint.bytes(max_mem)
              + ") - try reducing the number of columns and/or the number of categorical factors.";
      error("_train", msg);
      cancel(msg);
    }
  }
  /** Start the KMeans training Job on an F/J thread. */
  @Override public Job<KMeansModel> trainModel() {
    return start(new KMeansDriver(), _parms._max_iterations);
  }

  /** Initialize the ModelBuilder, validating all arguments and preparing the
   *  training frame.  This call is expected to be overridden in the subclasses
   *  and each subclass will start with "super.init();".
   *
   *  Validate K, max_iterations and the number of rows. */
  @Override public void init(boolean expensive) {
    super.init(expensive);
    if( _parms._max_iterations < 0 || _parms._max_iterations > 1e6) error("_max_iterations", " max_iterations must be between 0 and 1e6");
    if( _train == null ) return;
    if( null != _parms._user_points ){ // Check dimensions of user-specified centers
      if( _parms._user_points.get().numCols() != _train.numCols() ) {
        error("_user_points","The user-specified points must have the same number of columns (" + _train.numCols() + ") as the training observations");
      }
    }
    if (expensive && error_count() == 0) checkMemoryFootPrint();
  }

  // ----------------------
  private class KMeansDriver extends H2OCountedCompleter<KMeansDriver> {
    private String[][] _isCats;  // Categorical columns

    // Initialize cluster centers
    double[][] initial_centers( KMeansModel model, final Vec[] vecs, final double[] means, final double[] mults ) {

      // Categoricals use a different distance metric than numeric columns.
      model._output._categorical_column_count=0;
      _isCats = new String[vecs.length][];
      for( int v=0; v<vecs.length; v++ ) {
        _isCats[v] = vecs[v].isEnum() ? new String[0] : null;
        if (_isCats[v] != null) model._output._categorical_column_count++;
      }
      
      Random rand = water.util.RandomUtils.getRNG(_parms._seed - 1);
      double centers[][];    // Cluster centers
      if( null != _parms._user_points ) { // User-specified starting points
        int numCenters = _parms._k;
        int numCols = _parms._user_points.get().numCols();
        centers = new double[numCenters][numCols];
        Vec[] centersVecs = _parms._user_points.get().vecs();
        // Get the centers and standardize them if requested
        for (int r=0; r<numCenters; r++) {
          for (int c=0; c<numCols; c++){
            centers[r][c] = centersVecs[c].at(r);
            centers[r][c] = data(centers[r][c], c, means, mults, vecs[c].cardinality());
          }
        }
      }
      else { // Random, Furthest, or PlusPlus initialization
        if (_parms._init == Initialization.Random) {
          // Initialize all cluster centers to random rows
          centers = new double[_parms._k][_train.numCols()];
          for (double[] center : centers)
            randomRow(vecs, rand, center, means, mults);
        } else {
          centers = new double[1][vecs.length];
          // Initialize first cluster center to random row
          randomRow(vecs, rand, centers[0], means, mults);

          while (model._output._iterations < 5) {
            // Sum squares distances to cluster center
            SumSqr sqr = new SumSqr(centers, means, mults, _isCats).doAll(vecs);

            // Sample with probability inverse to square distance
            Sampler sampler = new Sampler(centers, means, mults, _isCats, sqr._sqr, _parms._k * 3, _parms._seed).doAll(vecs);
            centers = ArrayUtils.append(centers, sampler._sampled);

            // Fill in sample centers into the model
            if (!isRunning()) return null; // Stopped/cancelled
            model._output._centers_raw = destandardize(centers, _isCats, means, mults);
            model._output._tot_withinss = sqr._sqr / _train.numRows();

            model._output._iterations++;     // One iteration done

            model.update(_key); // Make early version of model visible, but don't update progress using update(1)
          }
          // Recluster down to k cluster centers
          centers = recluster(centers, rand, _parms._k, _parms._init, _isCats);
          model._output._iterations = -1; // Reset iteration count
        }
      }
      return centers;
    }

    // Number of reinitialization attempts for preventing empty clusters
    transient private int _reinit_attempts;
    // Handle the case where some centers go dry.  Rescue only 1 cluster
    // per iteration ('cause we only tracked the 1 worst row)
    boolean cleanupBadClusters( Lloyds task, final Vec[] vecs, final double[][] centers, final double[] means, final double[] mults ) {
      // Find any bad clusters
      int clu;
      for( clu=0; clu<_parms._k; clu++ )
        if( task._size[clu] == 0 ) break;
      if( clu == _parms._k ) return false; // No bad clusters

      long row = task._worst_row;
      Log.warn("KMeans: Re-initializing cluster " + clu + " to row " + row);
      data(centers[clu] = task._cMeans[clu], vecs, row, means, mults);
      task._size[clu] = 1; //FIXME: PUBDEV-871 Some other cluster had their membership count reduced by one! (which one?)

      // Find any MORE bad clusters; we only fixed the first one
      for( clu=0; clu<_parms._k; clu++ )
        if( task._size[clu] == 0 ) break;
      if( clu == _parms._k ) return false; // No MORE bad clusters

      // If we see 2 or more bad rows, just re-run Lloyds to get the
      // next-worst row.  We don't count this as an iteration, because
      // we're not really adjusting the centers, we're trying to get
      // some centers *at-all*.
      Log.warn("KMeans: Re-running Lloyds to re-init another cluster");
      if (_reinit_attempts++ < _parms._k) {
        return true;  // Rerun Lloyds, and assign points to centroids
      } else {
        _reinit_attempts = 0;
        return false;
      }
    }

    // Compute all interesting KMeans stats (errors & variances of clusters,
    // etc).  Return new centers.
    double[][] computeStatsFillModel( Lloyds task, KMeansModel model, final Vec[] vecs, final double[][] centers, final double[] means, final double[] mults ) {
      // Fill in the model based on original destandardized centers
      if (model._parms._standardize) {
        model._output._centers_std_raw = centers;
      }
      model._output._centers_raw = destandardize(centers, _isCats, means, mults);
      model._output._size = task._size;
      model._output._withinss = task._cSqr;
      double ssq = 0;       // sum squared error
      for( int i=0; i<_parms._k; i++ )
        ssq += model._output._withinss[i]; // sum squared error all clusters
      model._output._tot_withinss = ssq;

      // Sum-of-square distance from grand mean
      if(_parms._k == 1)
        model._output._totss = model._output._tot_withinss;
      else {
        // If data already standardized, grand mean is just the origin
        TotSS totss = new TotSS(means,mults, _parms.train().domains()).doAll(vecs);
        model._output._totss = totss._tss;
      }
      model._output._betweenss = model._output._totss - model._output._tot_withinss;  // MSE between-cluster
      model._output._iterations++;

      // add to scoring history
      model._output._history_withinss = ArrayUtils.copyAndFillOf(
          model._output._history_withinss,
          model._output._history_withinss.length+1, model._output._tot_withinss);

      // Two small TwoDimTables - cheap
      model._output._model_summary = createModelSummaryTable(model._output);
      model._output._scoring_history = createScoringHistoryTable(model._output);

      // Take the cluster stats from the model, and assemble them into a model metrics object
      model._output._training_metrics = makeTrainingMetrics(model);

      return task._cMeans;      // New centers
    }

    // Stopping criteria
    boolean isDone( KMeansModel model, double[][] newCenters, double[][] oldCenters ) {
      if( !isRunning() ) return true; // Stopped/cancelled
      // Stopped for running out iterations
      if( model._output._iterations > _parms._max_iterations) return true;

      // Compute average change in standardized cluster centers
      if( oldCenters==null ) return false; // No prior iteration, not stopping
      double average_change = 0;
      for( int clu=0; clu<_parms._k; clu++ )
        average_change += hex.genmodel.GenModel.KMeans_distance(oldCenters[clu],newCenters[clu],_isCats,null,null);
      average_change /= _parms._k;  // Average change per cluster
      model._output._avg_centroids_chg = ArrayUtils.copyAndFillOf(
              model._output._avg_centroids_chg,
              model._output._avg_centroids_chg.length+1, average_change);
      model._output._training_time_ms = ArrayUtils.copyAndFillOf(
              model._output._training_time_ms,
              model._output._training_time_ms.length+1, System.currentTimeMillis());
      return average_change < TOLERANCE;
    }

    // Main worker thread
    @Override protected void compute2() {

      KMeansModel model = null;
      try {
        _parms.read_lock_frames(KMeans.this); // Fetch & read-lock input frames
        init(true);
        if( error_count() > 0 ) throw new IllegalArgumentException("Found validation errors: "+validationErrors());

        // The model to be built
        model = new KMeansModel(dest(), _parms, new KMeansModel.KMeansOutput(KMeans.this));
        model.delete_and_lock(_key);

        //
        final Vec vecs[] = _train.vecs();
        // mults & means for standardization
        final double[] means = _train.means();  // means are used to impute NAs
        final double[] mults = _parms._standardize ? _train.mults() : null;
        model._output._normSub = means;
        model._output._normMul = mults;
        // Initialize cluster centers and standardize if requested
        double[][] centers = initial_centers(model,vecs,means,mults);
        if( centers==null ) return; // Stopped/cancelled during center-finding
        double[][] oldCenters = null;

        // ---
        // Run the main KMeans Clustering loop
        // Stop after enough iterations or average_change < TOLERANCE
        while( !isDone(model,centers,oldCenters) ) {
          Lloyds task = new Lloyds(centers,means,mults,_isCats, _parms._k).doAll(vecs);
          // Pick the max categorical level for cluster center
          max_cats(task._cMeans,task._cats,_isCats);

          // Handle the case where some centers go dry.  Rescue only 1 cluster
          // per iteration ('cause we only tracked the 1 worst row)
          if( cleanupBadClusters(task,vecs,centers,means,mults) ) continue;

          // Compute model stats; update standardized cluster centers
          oldCenters = centers;
          centers = computeStatsFillModel(task, model, vecs, centers, means, mults);

          model.update(_key); // Update model in K/V store
          update(1);          // One unit of work
          if (model._parms._score_each_iteration)
            Log.info(model._output._model_summary);
        }

        Log.info(model._output._model_summary);
//        Log.info(model._output._scoring_history);
//        Log.info(((ModelMetricsClustering)model._output._training_metrics).createCentroidStatsTable().toString());

        // FIXME: Remove (most of) this code - once it passes...
        // PUBDEV-871: Double-check the training metrics (gathered by computeStatsFillModel) and the scoring logic by scoring on the training set
        if (false) {
          assert((ArrayUtils.sum(model._output._size) - _parms.train().numRows()) <= 1);

//          Log.info(model._output._model_summary);
//          Log.info(model._output._scoring_history);
//          Log.info(((ModelMetricsClustering)model._output._training_metrics).createCentroidStatsTable().toString());
          model.score(_parms.train()).delete(); //this scores on the training data and appends a ModelMetrics
          ModelMetricsClustering mm = DKV.getGet(model._output._model_metrics[model._output._model_metrics.length - 1]);
          assert(Arrays.equals(mm._size, ((ModelMetricsClustering) model._output._training_metrics)._size));
          for (int i=0; i<_parms._k; ++i) {
            assert(MathUtils.compare(mm._withinss[i], ((ModelMetricsClustering) model._output._training_metrics)._withinss[i], 1e-6, 1e-6));
          }
          assert(MathUtils.compare(mm._totss, ((ModelMetricsClustering) model._output._training_metrics)._totss, 1e-6, 1e-6));
          assert(MathUtils.compare(mm._betweenss, ((ModelMetricsClustering) model._output._training_metrics)._betweenss, 1e-6, 1e-6));
          assert(MathUtils.compare(mm._tot_withinss, ((ModelMetricsClustering) model._output._training_metrics)._tot_withinss, 1e-6, 1e-6));
        }
        // At the end: validation scoring (no need to gather scoring history)
        if (_valid != null) {
          Frame pred = model.score(_parms.valid()); //this appends a ModelMetrics on the validation set
          model._output._validation_metrics = DKV.getGet(model._output._model_metrics[model._output._model_metrics.length-1]);
          pred.delete();
          model.update(_key); // Update model in K/V store
        }
        done();                 // Job done!

      } catch( Throwable t ) {
        Job thisJob = DKV.getGet(_key);
        if (thisJob._state == JobState.CANCELLED) {
          Log.info("Job cancelled by user.");
        } else {
          t.printStackTrace();
          failed(t);
          throw t;
        }
      } finally {
        if( model != null ) model.unlock(_key);
        _parms.read_unlock_frames(KMeans.this);
      }
      tryComplete();
    }

    private TwoDimTable createModelSummaryTable(KMeansModel.KMeansOutput output) {
      List<String> colHeaders = new ArrayList<>();
      List<String> colTypes = new ArrayList<>();
      List<String> colFormat = new ArrayList<>();
      colHeaders.add("Number of Clusters"); colTypes.add("long"); colFormat.add("%d");
      colHeaders.add("Number of Categorical Columns"); colTypes.add("long"); colFormat.add("%d");
      colHeaders.add("Number of Iterations"); colTypes.add("long"); colFormat.add("%d");
      colHeaders.add("Within Cluster Sum of Squares"); colTypes.add("double"); colFormat.add("%.5f");
      colHeaders.add("Total Sum of Squares"); colTypes.add("double"); colFormat.add("%.5f");
      colHeaders.add("Between Cluster Sum of Squares"); colTypes.add("double"); colFormat.add("%.5f");

      final int rows = 1;
      TwoDimTable table = new TwoDimTable(
              "Model Summary", null,
              new String[rows],
              colHeaders.toArray(new String[0]),
              colTypes.toArray(new String[0]),
              colFormat.toArray(new String[0]),
              "");
      int row = 0;
      int col = 0;
      table.set(row, col++, output._centers_raw.length);
      table.set(row, col++, output._categorical_column_count);
      table.set(row, col++, output._iterations);
      table.set(row, col++, output._tot_withinss);
      table.set(row, col++, output._totss);
      table.set(row, col++, output._betweenss);
      return table;
    }

    private TwoDimTable createScoringHistoryTable(KMeansModel.KMeansOutput output) {
      List<String> colHeaders = new ArrayList<>();
      List<String> colTypes = new ArrayList<>();
      List<String> colFormat = new ArrayList<>();
      colHeaders.add("Timestamp"); colTypes.add("string"); colFormat.add("%s");
      colHeaders.add("Duration"); colTypes.add("string"); colFormat.add("%s");
      colHeaders.add("Iteration"); colTypes.add("long"); colFormat.add("%d");
      colHeaders.add("Avg. Change of Std. Centroids"); colTypes.add("double"); colFormat.add("%.5f");
      colHeaders.add("Within Cluster Sum Of Squares"); colTypes.add("double"); colFormat.add("%.5f");

      final int rows = output._avg_centroids_chg.length;
      TwoDimTable table = new TwoDimTable(
              "Scoring History", null,
              new String[rows],
              colHeaders.toArray(new String[0]),
              colTypes.toArray(new String[0]),
              colFormat.toArray(new String[0]),
              "");
      int row = 0;
      for( int i = 0; i<rows; i++ ) {
        int col = 0;
        assert(row < table.getRowDim());
        assert(col < table.getColDim());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        table.set(row, col++, fmt.print(output._training_time_ms[i]));
        table.set(row, col++, PrettyPrint.msecs(output._training_time_ms[i]-_start_time, true));
        table.set(row, col++, i);
        table.set(row, col++, output._avg_centroids_chg[i]);
        table.set(row, col++, output._history_withinss[i]);
        row++;
      }
      return table;
    }
  }

  static public TwoDimTable createCenterTable(KMeansModel.KMeansOutput output, boolean standardized) {
    String[] rowHeaders = new String[output._size.length];
    for(int i = 0; i < rowHeaders.length; i++)
      rowHeaders[i] = String.valueOf(i+1);
    String[] colTypes = new String[output._names.length];
    String[] colFormats = new String[output._names.length];
    for (int i=0; i<output._domains.length; ++i) {
      colTypes[i] = output._domains[i] == null ? "double" : "String";
      colFormats[i] = output._domains[i] == null ? "%f" : "%s";
    }
    String name = standardized ? "Standardized Cluster Means" : "Cluster Means";
    TwoDimTable table = new TwoDimTable(name, null, rowHeaders, output._names, colTypes, colFormats, "Centroid");

    for (int j=0; j<output._domains.length; ++j) {
      boolean string = output._domains[j] != null;
      if (string) {
        for (int i=0; i<output._centers_raw.length; ++i) {
          table.set(i, j, output._domains[j][(int)output._centers_raw[i][j]]);
        }
      } else {
        for (int i=0; i<output._centers_raw.length; ++i) {
          table.set(i, j, standardized ? output._centers_std_raw[i][j] : output._centers_raw[i][j]);
        }
      }
    }
    return table;
  }

  // -------------------------------------------------------------------------
  // Initial sum-of-square-distance to nearest cluster center
  private static class TotSS extends MRTask<TotSS> {
    // IN
    final double[] _means, _mults;
    final String[][] _isCats;

    // OUT
    double _tss;

    TotSS(double[] means, double[] mults, String[][] isCats) {
      _means = means;
      _mults = mults;
      _tss = 0;
      _isCats = isCats;
    }

    @Override public void map(Chunk[] cs) {
      // de-standardize the cluster means
      double[] means = Arrays.copyOf(_means, _means.length);

      if (_mults!=null)
        for (int i=0; i<means.length; ++i)
          means[i] = (means[i] - _means[i])/_mults[i];

      for( int row = 0; row < cs[0]._len; row++ ) {
        double[] values = new double[cs.length];
        // fetch the data - using consistent NA and categorical data handling (same as for training)
        data(values, cs, row, _means, _mults);
        // compute the distance from the (standardized) cluster centroids
        _tss += hex.genmodel.GenModel.KMeans_distance(means, values, _isCats, null, null);
      }
    }

    @Override public void reduce(TotSS other) { _tss += other._tss; }
  }

  // -------------------------------------------------------------------------
  // Initial sum-of-square-distance to nearest cluster center
  private static class SumSqr extends MRTask<SumSqr> {
    // IN
    double[][] _centers;
    double[] _means, _mults; // Standardization
    final String[][] _isCats;

    // OUT
    double _sqr;

    SumSqr( double[][] centers, double[] means, double[] mults, String[][] isCats ) {
      _centers = centers;
      _means = means;
      _mults = mults;
      _isCats = isCats;
    }

    @Override public void map(Chunk[] cs) {
      double[] values = new double[cs.length];
      ClusterDist cd = new ClusterDist();
      for( int row = 0; row < cs[0]._len; row++ ) {
        data(values, cs, row, _means, _mults);
        _sqr += minSqr(_centers, values, _isCats, cd);
      }
      _means = _mults = null;
      _centers = null;
    }

    @Override public void reduce(SumSqr other) { _sqr += other._sqr; }
  }

  // -------------------------------------------------------------------------
  // Sample rows with increasing probability the farther they are from any
  // cluster center.
  private static class Sampler extends MRTask<Sampler> {
    // IN
    double[][] _centers;
    double[] _means, _mults; // Standardization
    final String[][] _isCats;
    final double _sqr;           // Min-square-error
    final double _probability;   // Odds to select this point
    final long _seed;

    // OUT
    double[][] _sampled;   // New cluster centers

    Sampler( double[][] centers, double[] means, double[] mults, String[][] isCats, double sqr, double prob, long seed ) {
      _centers = centers;
      _means = means;
      _mults = mults;
      _isCats = isCats;
      _sqr = sqr;
      _probability = prob;
      _seed = seed;
    }

    @Override public void map(Chunk[] cs) {
      double[] values = new double[cs.length];
      ArrayList<double[]> list = new ArrayList<>();
      Random rand = RandomUtils.getRNG(_seed + cs[0].start());
      ClusterDist cd = new ClusterDist();

      for( int row = 0; row < cs[0]._len; row++ ) {
        data(values, cs, row, _means, _mults);
        double sqr = minSqr(_centers, values, _isCats, cd);
        if( _probability * sqr > rand.nextDouble() * _sqr )
          list.add(values.clone());
      }

      _sampled = new double[list.size()][];
      list.toArray(_sampled);
      _centers = null;
      _means = _mults = null;
    }

    @Override public void reduce(Sampler other) {
      _sampled = ArrayUtils.append(_sampled, other._sampled);
    }
  }

  // ---------------------------------------
  // A Lloyd's pass:
  //   Find nearest cluster center for every point
  //   Compute new mean/center & variance & rows for each cluster
  //   Compute distance between clusters
  //   Compute total sqr distance

  private static class Lloyds extends MRTask<Lloyds> {
    // IN
    double[][] _centers;
    double[] _means, _mults;      // Standardization
    final int _k;
    final String[][] _isCats;

    // OUT
    double[][] _cMeans;         // Means for each cluster
    long[/*k*/][/*features*/][/*nfactors*/] _cats; // Histogram of cat levels
    double[] _cSqr;             // Sum of squares for each cluster
    long[] _size;               // Number of rows in each cluster
    long _worst_row;            // Row with max err
    double _worst_err;          // Max-err-row's max-err

    Lloyds( double[][] centers, double[] means, double[] mults, String[][] isCats, int k ) {
      _centers = centers;
      _means = means;
      _mults = mults;
      _isCats = isCats;
      _k = k;
    }

    @Override public void map(Chunk[] cs) {
      int N = cs.length;
      assert _centers[0].length==N;
      _cMeans = new double[_k][N];
      _cSqr = new double[_k];
      _size = new long[_k];
      // Space for cat histograms
      _cats = new long[_k][N][];
      for( int clu=0; clu< _k; clu++ )
        for( int col=0; col<N; col++ )
          _cats[clu][col] = _isCats[col]==null ? null : new long[cs[col].vec().cardinality()];
      _worst_err = 0;

      // Find closest cluster center for each row
      double[] values = new double[N]; // Temp data to hold row as doubles
      ClusterDist cd = new ClusterDist();
      for( int row = 0; row < cs[0]._len; row++ ) {
        data(values, cs, row, _means, _mults); // Load row as doubles
        closest(_centers, values, _isCats, cd); // Find closest cluster center
        int clu = cd._cluster;
        assert clu != -1;       // No broken rows
        _cSqr[clu] += cd._dist;

        // Add values and increment counter for chosen cluster
        for( int col = 0; col < N; col++ )
          if( _isCats[col] != null )
            _cats[clu][col][(int)values[col]]++; // Histogram the cats
          else 
            _cMeans[clu][col] += values[col]; // Sum the column centers
        _size[clu]++;
        // Track worst row
        if( cd._dist > _worst_err) { _worst_err = cd._dist; _worst_row = cs[0].start()+row; }
      }
      // Scale back down to local mean
      for( int clu = 0; clu < _k; clu++ )
        if( _size[clu] != 0 ) ArrayUtils.div(_cMeans[clu], _size[clu]);
      _centers = null;
      _means = _mults = null;
    }

    @Override public void reduce(Lloyds mr) {
      for( int clu = 0; clu < _k; clu++ ) {
        long ra =    _size[clu];
        long rb = mr._size[clu];
        double[] ma =    _cMeans[clu];
        double[] mb = mr._cMeans[clu];
        for( int c = 0; c < ma.length; c++ ) // Recursive mean
          if( ra+rb > 0 ) ma[c] = (ma[c] * ra + mb[c] * rb) / (ra + rb);
      }
      ArrayUtils.add(_cats, mr._cats);
      ArrayUtils.add(_cSqr, mr._cSqr);
      ArrayUtils.add(_size, mr._size);
      // track global worst-row
      if( _worst_err < mr._worst_err) { _worst_err = mr._worst_err; _worst_row = mr._worst_row; }
    }
  }

  // A pair result: nearest cluster center and the square distance
  private static final class ClusterDist { int _cluster; double _dist;  }

  private static double minSqr(double[][] centers, double[] point, String[][] isCats, ClusterDist cd) {
    return closest(centers, point, isCats, cd, centers.length)._dist;
  }

  private static double minSqr(double[][] centers, double[] point, String[][] isCats, ClusterDist cd, int count) {
    return closest(centers,point,isCats,cd,count)._dist;
  }

  private static ClusterDist closest(double[][] centers, double[] point, String[][] isCats, ClusterDist cd) {
    return closest(centers, point, isCats, cd, centers.length);
  }

  /** Return both nearest of N cluster center/centroids, and the square-distance. */
  private static ClusterDist closest(double[][] centers, double[] point, String[][] isCats, ClusterDist cd, int count) {
    int min = -1;
    double minSqr = Double.MAX_VALUE;
    for( int cluster = 0; cluster < count; cluster++ ) {
      double sqr = hex.genmodel.GenModel.KMeans_distance(centers[cluster],point,isCats,null,null);
      if( sqr < minSqr ) {      // Record nearest cluster
        min = cluster;
        minSqr = sqr;
      }
    }
    cd._cluster = min;          // Record nearest cluster
    cd._dist = minSqr;          // Record square-distance
    return cd;                  // Return for flow-coding
  }

  // KMeans++ re-clustering
  private static double[][] recluster(double[][] points, Random rand, int N, Initialization init, String[][] isCats) {
    double[][] res = new double[N][];
    res[0] = points[0];
    int count = 1;
    ClusterDist cd = new ClusterDist();
    switch( init ) {
    case Random:
      break;
    case PlusPlus: { // k-means++
      while( count < res.length ) {
        double sum = 0;
        for (double[] point1 : points) sum += minSqr(res, point1, isCats, cd, count);

        for (double[] point : points) {
          if (minSqr(res, point, isCats, cd, count) >= rand.nextDouble() * sum) {
            res[count++] = point;
            break;
          }
        }
      }
      break;
    }
    case Furthest: { // Takes cluster center further from any already chosen ones
      while( count < res.length ) {
        double max = 0;
        int index = 0;
        for( int i = 0; i < points.length; i++ ) {
          double sqr = minSqr(res, points[i], isCats, cd, count);
          if( sqr > max ) {
            max = sqr;
            index = i;
          }
        }
        res[count++] = points[index];
      }
      break;
    }
    default:  throw H2O.fail();
    }
    return res;
  }

  private void randomRow(Vec[] vecs, Random rand, double[] center, double[] means, double[] mults) {
    long row = Math.max(0, (long) (rand.nextDouble() * vecs[0].length()) - 1);
    data(center, vecs, row, means, mults);
  }

  // Pick most common cat level for each cluster_centers' cat columns
  private static double[][] max_cats(double[][] centers, long[][][] cats, String[][] isCats) {
    for( int clu = 0; clu < centers.length; clu++ )
      for( int col = 0; col < centers[0].length; col++ )
        if( isCats[col] != null )
          centers[clu][col] = ArrayUtils.maxIndex(cats[clu][col]);
    return centers;
  }

  private static double[][] destandardize(double[][] centers, String[][] isCats, double[] means, double[] mults) {
    int K = centers.length;
    int N = centers[0].length;
    double[][] value = new double[K][N];
    for( int clu = 0; clu < K; clu++ ) {
      System.arraycopy(centers[clu],0,value[clu],0,N);
      if( mults!=null ) {        // Reverse standardization
        for( int col = 0; col < N; col++)
          if( isCats[col] == null )
            value[clu][col] = value[clu][col] / mults[col] + means[col];
      }
    }
    return value;
  }

  private static void data(double[] values, Vec[] vecs, long row, double[] means, double[] mults) {
    for( int i = 0; i < values.length; i++ ) {
      double d = vecs[i].at(row);
      values[i] = data(d, i, means, mults, vecs[i].cardinality());
    }
  }

  private static void data(double[] values, Chunk[] chks, int row, double[] means, double[] mults) {
    for( int i = 0; i < values.length; i++ ) {
      double d = chks[i].atd(row);
      values[i] = data(d, i, means, mults, chks[i].vec().cardinality());
    }
  }

  /**
   * Takes mean if NaN, standardize if requested.
   */
  private static double data(double d, int i, double[] means, double[] mults, int cardinality) {
    if(cardinality == -1) {
      if( Double.isNaN(d) )
        d = means[i];
      if( mults != null ) {
        d -= means[i];
        d *= mults[i];
      }
    } else {
      // TODO: If NaN, then replace with majority class?
      if(Double.isNaN(d)) {
        d = Math.min(Math.round(means[i]), cardinality-1);
        if( mults != null ) {
          d = 0;
        }
      }
    }
    return d;
  }

  /**
   * This helper creates a ModelMetricsClustering from a trained model
   * @param model, must contain valid statistics from training, such as _betweenss etc.
   */
  private ModelMetricsClustering makeTrainingMetrics(KMeansModel model) {
    ModelMetricsClustering mm = new ModelMetricsClustering(model, model._parms.train());
    mm._size = model._output._size;
    mm._withinss = model._output._withinss;
    mm._betweenss = model._output._betweenss;
    mm._totss = model._output._totss;
    mm._tot_withinss = model._output._tot_withinss;
    model.addMetrics(mm);
    return mm;
  }

}
