package hex;

import hex.ClusteringModel.ClusteringOutput;
import hex.ClusteringModel.ClusteringParameters;
import water.exceptions.H2OIllegalArgumentException;
import water.fvec.Frame;
import water.util.ArrayUtils;
import water.util.Log;
import water.util.TwoDimTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelMetricsClustering extends ModelMetricsUnsupervised {
  public long[/*k*/] _size;
  public double[/*k*/] _within_mse;
  public double _avg_ss;
  public double _avg_within_ss;
  public double _avg_between_ss;
//  public TwoDimTable _centroid_stats;

  public ModelMetricsClustering(Model model, Frame frame) {
    super(model, frame, Double.NaN);
    _size = null;
    _within_mse = null;
    _avg_ss = _avg_within_ss = _avg_between_ss = Double.NaN;
  }
  /**
   * Populate TwoDimTable from members _size and _within_mse
   * @return TwoDimTable
   */
  public TwoDimTable createCentroidStatsTable() {
    if (_size == null || _within_mse == null)
      return null;
    List<String> colHeaders = new ArrayList<>();
    List<String> colTypes = new ArrayList<>();
    List<String> colFormat = new ArrayList<>();

    colHeaders.add("Centroid"); colTypes.add("long"); colFormat.add("%d");
    colHeaders.add("Size"); colTypes.add("double"); colFormat.add("%.5f");
    colHeaders.add("Within Sum of Squares"); colTypes.add("double"); colFormat.add("%.5f");

    final int K = _size.length;
    assert(_within_mse.length == K);

    TwoDimTable table = new TwoDimTable(
            "Centroid Statistics", null,
            new String[K],
            colHeaders.toArray(new String[0]),
            colTypes.toArray(new String[0]),
            colFormat.toArray(new String[0]),
            "");
    for (int k =0; k<K; ++k) {
      int col = 0;
      table.set(k, col++, k+1);
      table.set(k, col++, _size[k]);
      table.set(k, col++, _within_mse[k]);
    }
    return table;
  }

  public static class MetricBuilderClustering extends MetricBuilderUnsupervised {
    public long[] _size;        // Number of elements in cluster
    public double[] _within_sumsqe;   // Within-cluster sum of squared error
    private double[/*features*/] _colSum;  // Sum of each column
    private double[/*features*/] _colSumSq;  // Sum of squared values of each column

    public MetricBuilderClustering(int ncol, int nclust) {
      _work = new double[ncol];
      _size = new long[nclust];
      _within_sumsqe = new double[nclust];
      Arrays.fill(_size, 0);
      Arrays.fill(_within_sumsqe, 0);

      _colSum = new double[ncol];
      _colSumSq = new double[ncol];
      Arrays.fill(_colSum, 0);
      Arrays.fill(_colSumSq, 0);
    }

    // Compare row (dataRow) against centroid it was assigned to (preds[0])
    @Override
    public double[] perRow(double[] preds, float[] dataRow, float row_weight, Model m) {
      assert m instanceof ClusteringModel;
      assert !Double.isNaN(preds[0]);

      ClusteringModel clm = (ClusteringModel) m;
      boolean standardize = ((((ClusteringOutput) clm._output)._centers_std_raw) != null);
      double[][] centers = standardize ? ((ClusteringOutput) clm._output)._centers_std_raw: ((ClusteringOutput) clm._output)._centers_raw;
      double[] sub = standardize ? ((ClusteringOutput) clm._output)._normSub : null;
      double[] mul = standardize ? ((ClusteringOutput) clm._output)._normMul : null;

      int clus = (int)preds[0];
      double [] colSum = new double[_colSum.length];
      double [] colSumSq = new double[_colSumSq.length];
      double sqr = hex.genmodel.GenModel.KMeans_distance(centers[clus], dataRow, clm._output._domains, sub, mul, colSum, colSumSq);
      ArrayUtils.add(_colSum, colSum);
      ArrayUtils.add(_colSumSq, colSumSq);
      _count++;
      _size[clus]++;
      _sumsqe += sqr;
      _within_sumsqe[clus] += sqr;

      if (Double.isNaN(_sumsqe))
        throw new H2OIllegalArgumentException("Sum of Squares is invalid (Double.NaN) - Check for missing values in the dataset.");
      return preds;                // Flow coding
    }

    @Override
    public void reduce(MetricBuilder mb) {
      MetricBuilderClustering mm = (MetricBuilderClustering) mb;
      super.reduce(mm);
      ArrayUtils.add(_size, mm._size);
      ArrayUtils.add(_within_sumsqe, mm._within_sumsqe);
      ArrayUtils.add(_colSum, mm._colSum);
      ArrayUtils.add(_colSumSq, mm._colSumSq);
    }

    @Override
    public ModelMetrics makeModelMetrics(Model m, Frame f, double sigma) {
      assert m instanceof ClusteringModel;
      ClusteringModel clm = (ClusteringModel) m;
      ModelMetricsClustering mm = new ModelMetricsClustering(m, f);

      mm._size = _size;
      mm._avg_within_ss = _sumsqe / _count;
      mm._within_mse = new double[_size.length];
      for (int i = 0; i < mm._within_mse.length; i++)
        mm._within_mse[i] = _within_sumsqe[i] / _size[i];

      // Sum-of-square distance from grand mean
      if ( ((ClusteringParameters) clm._parms)._k == 1 )
        mm._avg_ss = mm._avg_within_ss;
      else {
        mm._avg_ss = 0;
        for (int i = 0; i < _colSum.length; i++)
          mm._avg_ss += _colSumSq[i] - (_colSum[i] * _colSum[i]) / f.numRows();
        mm._avg_ss /= f.numRows();
      }
      mm._avg_between_ss = mm._avg_ss - mm._avg_within_ss;
      return m.addMetrics(mm);
    }
  }
}