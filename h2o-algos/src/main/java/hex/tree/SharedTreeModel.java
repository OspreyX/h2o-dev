package hex.tree;

import hex.*;
import water.*;
import water.util.*;

import java.util.Arrays;

public abstract class SharedTreeModel<M extends SharedTreeModel<M,P,O>, P extends SharedTreeModel.SharedTreeParameters, O extends SharedTreeModel.SharedTreeOutput> extends SupervisedModel<M,P,O> {

  public abstract static class SharedTreeParameters extends SupervisedModel.SupervisedParameters {
    /** Maximal number of supported levels in response. */
    static final int MAX_SUPPORTED_LEVELS = 1000;

    public int _ntrees=50; // Number of trees in the final model. Grid Search, comma sep values:50,100,150,200

    public int _max_depth = 5; // Maximum tree depth. Grid Search, comma sep values:5,7

    public int _min_rows = 10; // Fewest allowed observations in a leaf (in R called 'nodesize'). Grid Search, comma sep values

    public int _nbins = 20; // Build a histogram of this many bins, then split at the best point

    public double _r2_stopping = 0.999999; // Stop when the r^2 metric equals or exceeds this value

    public long _seed;          // Seed for pseudo-random redistribution

    // TRUE: Continue extending an existing checkpointed model
    // FALSE: Overwrite any prior model
    public boolean _checkpoint;
  }

  @Override public ModelMetrics.MetricBuilder makeMetricBuilder(String[] domain) {
    switch(_output.getModelCategory()) {
      case Binomial:    return new ModelMetricsBinomial.MetricBuilderBinomial(domain);
      case Multinomial: return new ModelMetricsMultinomial.MetricBuilderMultinomial(_output.nclasses(),domain);
      case Regression:  return new ModelMetricsRegression.MetricBuilderRegression();
      default: throw H2O.unimpl();
    }
  }

  public abstract static class SharedTreeOutput extends SupervisedModel.SupervisedOutput {
    /** InitF value (for zero trees)
     *  f0 = mean(yi) for gaussian
     *  f0 = log(yi/1-yi) for bernoulli
     *
     *  For GBM bernoulli, the initial prediction for 0 trees is
     *  p = 1/(1+exp(-f0))
     *
     *  From this, the mse for 0 trees can be computed as follows:
     *  mean((yi-p)^2)
     *  This is what is stored in _mse_train[0]
     * */
    public double _init_f;

    /** Number of trees actually in the model (as opposed to requested) */
    public int _ntrees;

    /** More indepth tree stats */
    final public TreeStats _treeStats;

    /** Trees get big, so store each one seperately in the DKV. */
    public Key<CompressedTree>[/*_ntrees*/][/*_nclass*/] _treeKeys;

    /** Train and validation errors per-tree (scored).  Zero index is the no-tree
     *  error, guessing only the class distribution.  Not all trees are
     *  scored, NaN represents trees not scored. */
    public double _mse_train[/*_ntrees+1*/];
    public double _mse_valid[/*_ntrees+1*/];

    /** Training time */
    public long _training_time_ms[/*ntrees+1*/] = new long[]{System.currentTimeMillis()};

    /**
     * Variable importances computed during training
     */
    public TwoDimTable _variable_importances;

    public SharedTreeOutput( SharedTree b, double mse_train, double mse_valid ) {
      super(b);
      _ntrees = 0;              // No trees yet
      _treeKeys = new Key[_ntrees][]; // No tree keys yet
      _treeStats = new TreeStats();
      _mse_train = new double[]{mse_train};
      _mse_valid = new double[]{mse_valid};
    }

    // Append next set of K trees
    public void addKTrees( DTree[] trees) {
      // DEBUG: Print the generated K trees
      //SharedTree.printGenerateTrees(trees);
      assert nclasses()==trees.length;
      // Compress trees and record tree-keys
      _treeKeys = Arrays.copyOf(_treeKeys ,_ntrees+1);
      Key[] keys = _treeKeys[_ntrees] = new Key[trees.length];
      Futures fs = new Futures();
      for( int i=0; i<nclasses(); i++ ) if( trees[i] != null ) {
        CompressedTree ct = trees[i].compress(_ntrees,i);
        DKV.put(keys[i]=ct._key,ct,fs);
        _treeStats.updateBy(trees[i]); // Update tree shape stats
      }
      _ntrees++;
      // 1-based for errors; _mse_train[0] is for zero trees, not 1 tree
      _mse_train = ArrayUtils.copyAndFillOf(_mse_train, _ntrees+1, Double.NaN);
      _mse_valid = _validation_metrics != null ? ArrayUtils.copyAndFillOf(_mse_valid, _ntrees+1, Double.NaN) : null;
      _training_time_ms = ArrayUtils.copyAndFillOf(_training_time_ms, _ntrees+1, System.currentTimeMillis());
      fs.blockForPending();
    }

    public CompressedTree ctree( int tnum, int knum ) { return _treeKeys[tnum][knum].get(); }
    public String toStringTree ( int tnum, int knum ) { return ctree(tnum,knum).toString(this); }
  }

  public SharedTreeModel(Key selfKey, P parms, O output) { super(selfKey,parms,output); }

  @Override protected double[] score0(double data[/*ncols*/], double preds[/*nclasses+1*/]) {
    // Prefetch trees into the local cache if it is necessary
    // Invoke scoring
    Arrays.fill(preds,0);
    for( int tidx=0; tidx<_output._treeKeys.length; tidx++ )
      score0(data, preds, tidx);
    return preds;
  }
  // Score per line per tree
  public void score0(double data[], double preds[], int treeIdx) {
    Key[] keys = _output._treeKeys[treeIdx];
    for( int c=0; c<keys.length; c++ )
      if( keys[c] != null ) {
        double pred = DKV.get(keys[c]).<CompressedTree>get().score(data);
        assert(!Double.isInfinite(pred));
        preds[keys.length == 1 ? 0 : c + 1] += pred;
      }
  }

  @Override protected Futures remove_impl( Futures fs ) {
    for( Key ks[] : _output._treeKeys)
      for( Key k : ks )
        if( k != null ) k.remove(fs);
    return super.remove_impl(fs);
  }

  // Override in subclasses to provide some top-level model-specific goodness
  @Override protected boolean toJavaCheckTooBig() {
    // If the number of leaves in a forest is more than N, don't try to render it in the browser as POJO code.
    return _output==null || _output._treeStats._num_trees * _output._treeStats._mean_leaves > 5000;
  }
  protected boolean binomialOpt() { return false; }
  @Override protected SB toJavaInit(SB sb, SB fileContext) {
    sb.nl();
    sb.ip("public boolean isSupervised() { return true; }").nl();
    sb.ip("public int nfeatures() { return "+_output.nfeatures()+"; }").nl();
    sb.ip("public int nclasses() { return "+_output.nclasses()+"; }").nl();
    sb.ip("public ModelCategory getModelCategory() { return ModelCategory."+_output.getModelCategory()+"; }").nl();
    return sb;
  }
  @Override protected void toJavaPredictBody(SB body, SB classCtx, SB file) {
    final int nclass = _output.nclasses();
    body.ip("java.util.Arrays.fill(preds,0);").nl();
    body.ip("double[] fdata = hex.genmodel.GenModel.SharedTree_clean(data);").nl();
    String mname = JCodeGen.toJavaId(_key.toString());

    // One forest-per-GBM-tree, with a real-tree-per-class
    for( int t=0; t < _output._treeKeys.length; t++ ) {
      toJavaForestName(body.i(),mname,t).p(".score0(fdata,preds);").nl();
      file.nl();
      toJavaForestName(file.ip("class "),mname,t).p(" {").nl().ii(1);
      file.ip("public static void score0(double[] fdata, double[] preds) {").nl().ii(1);
      for( int c=0; c<nclass; c++ )
        if( !binomialOpt() || !(c==1 && nclass==2) ) // Binomial optimization
          toJavaTreeName(file.ip("preds[").p(nclass==1?0:c+1).p("] += "),mname,t,c).p(".score0(fdata);").nl();
      file.di(1).ip("}").nl(); // end of function
      file.di(1).ip("}").nl(); // end of forest class

      // Generate the pre-tree classes afterwards
      for( int c=0; c<nclass; c++ ) {
        if( !binomialOpt() || !(c==1 && nclass==2) ) { // Binomial optimization
          toJavaTreeName(file.ip("class "),mname,t,c).p(" {").nl().ii(1);
          CompressedTree ct = _output.ctree(t,c);
          new TreeJCodeGen(this,ct, file).generate();
          file.di(1).ip("}").nl(); // close the class
        }
      }
    }
    toJavaUnifyPreds(body,file);
  }
  abstract protected void toJavaUnifyPreds( SB body, SB file );
  protected SB toJavaTreeName( final SB sb, String mname, int t, int c ) { return sb.p(mname).p("_Tree_").p(t).p("_class_").p(c); }
  protected SB toJavaForestName( final SB sb, String mname, int t ) { return sb.p(mname).p("_Forest_").p(t); }
}
