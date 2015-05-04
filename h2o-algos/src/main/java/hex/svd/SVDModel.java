package hex.svd;

import hex.DataInfo;
import hex.Model;
import hex.ModelMetrics;
import hex.ModelMetricsUnsupervised;
import water.DKV;
import water.H2O;
import water.Key;
import water.MRTask;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.TwoDimTable;

public class SVDModel extends Model<SVDModel,SVDModel.SVDParameters,SVDModel.SVDOutput> {
  public static class SVDParameters extends Model.Parameters {
    public DataInfo.TransformType _transform = DataInfo.TransformType.NONE; // Data transformation (demean to compare with PCA)
    public int _nv = 1;    // Number of right singular vectors to calculate
    public int _max_iterations = 1000;    // Maximum number of iterations
    public long _seed = System.nanoTime();        // RNG seed
    public boolean _keep_u = true;    // Should left singular vectors be saved in memory? (Only applies if _only_v = false)
    public Key<Frame> _u_key;         // Frame key for left singular vectors (U)
    public boolean _only_v = false;   // Compute only right singular vectors? (Faster if true)
  }

  public static class SVDOutput extends Model.Output {
    // Right singular vectors (V)
    public double[][] _v;

    // Singular values (diagonal of D)
    public double[] _d;

    // Frame key for left singular vectors (U)
    public Key<Frame> _u_key;

    // If standardized, mean of each numeric data column
    public double[] _normSub;

    // If standardized, one over standard deviation of each numeric data column
    public double[] _normMul;

    public SVDOutput(SVD b) { super(b); }

    @Override public ModelCategory getModelCategory() { return Model.ModelCategory.DimReduction; }
  }

  public SVDModel(Key selfKey, SVDParameters parms, SVDOutput output) { super(selfKey,parms,output); }

  @Override public ModelMetrics.MetricBuilder makeMetricBuilder(String[] domain) {
    return new ModelMetricsSVD.SVDModelMetrics(_parms._nv);
  }

  public static class ModelMetricsSVD extends ModelMetricsUnsupervised {
    public ModelMetricsSVD(Model model, Frame frame) {
      super(model, frame, Double.NaN);
    }

    // SVD currently does not have any model metrics to compute during scoring
    public static class SVDModelMetrics extends MetricBuilderUnsupervised {
      public SVDModelMetrics(int dims) {
        _work = new double[dims];
      }

      @Override public double[] perRow(double[] dataRow, float[] preds, float row_weight, Model m) { return dataRow; }

      @Override public ModelMetrics makeModelMetrics(Model m, Frame f, double sigma) {
        return m._output.addModelMetrics(new ModelMetricsSVD(m, f));
      }
    }
  }

  @Override protected Frame scoreImpl(Frame orig, Frame adaptedFr, Vec row_weight, String destination_key) {
    Frame adaptFrm = new Frame(adaptedFr);
    for(int i = 0; i < _parms._nv; i++)
      adaptFrm.add("PC"+String.valueOf(i+1),adaptFrm.anyVec().makeZero());

    new MRTask() {
      @Override public void map( Chunk chks[] ) {
        double tmp [] = new double[_output._names.length];
        double preds[] = new double[_parms._nv];
        for( int row = 0; row < chks[0]._len; row++) {
          double p[] = score0(chks, row, tmp, preds);
          for( int c=0; c<preds.length; c++ )
            chks[_output._names.length+c].set(row, p[c]);
        }
      }
    }.doAll(adaptFrm);

    // Return the projection into right singular vector (V) space
    int x = _output._names.length, y = adaptFrm.numCols();
    Frame f = adaptFrm.extractFrame(x, y); // this will call vec_impl() and we cannot call the delete() below just yet

    f = new Frame((null == destination_key ? Key.make() : Key.make(destination_key)), f.names(), f.vecs());
    DKV.put(f);
    makeMetricBuilder(null).makeModelMetrics(this, orig, Double.NaN);
    return f;
  }

  @Override protected double[] score0(double data[/*ncols*/], double preds[/*nclasses+1*/]) {
    assert data.length == _output._v.length;
    for(int i = 0; i < _parms._nv; i++) {
      preds[i] = 0;
      for (int j = 0; j < data.length; j++)
        preds[i] += (data[j] - _output._normSub[j]) * _output._normMul[j] * _output._v[j][i];
    }
    return preds;
  }

  @Override public Frame score(Frame fr, Vec row_weights, String destination_key) {
    Frame adaptFr = new Frame(fr);
    adaptTestForTrain(adaptFr, true);   // Adapt
    Frame output = scoreImpl(fr, adaptFr, row_weights, destination_key); // Score

    Vec[] vecs = adaptFr.vecs();
    for (int i = 0; i < vecs.length; i++)
      if (fr.find(vecs[i]) != -1)   // Exists in the original frame?
        vecs[i] = null;            // Do not delete it
    adaptFr.delete();
    return output;
  }
}
