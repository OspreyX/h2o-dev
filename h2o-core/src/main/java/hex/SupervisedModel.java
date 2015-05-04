package hex;

import water.Key;
import water.fvec.Chunk;
import water.util.*;

/** Supervised Model
 *  There is a response column used in training.
 */
public abstract class SupervisedModel<M extends SupervisedModel<M,P,O>, P extends SupervisedModel.SupervisedParameters, O extends SupervisedModel.SupervisedOutput> extends Model<M,P,O> {

  public SupervisedModel( Key selfKey, P parms, O output ) { super(selfKey,parms,output);  }

  /** Supervised Model Parameters includes a response column, and whether or
   *  not rebalancing classes is desirable.  Also includes a bunch of cheap
   *  cached convenience fields.  */
  public abstract static class SupervisedParameters extends Model.Parameters {
    /** Supervised models have an expected response they get to train with! */
    public String _response_column; // response column name

    /** Should all classes be over/under-sampled to balance the class
     *  distribution? */
    public boolean _balance_classes = false;

    /** When classes are being balanced, limit the resulting dataset size to
     *  the specified multiple of the original dataset size.  Maximum relative
     *  size of the training data after balancing class counts (can be less
     *  than 1.0) */
    public float _max_after_balance_size = 5.0f;

    /**
     * Desired over/under-sampling ratios per class (lexicographic order).
     * Only when balance_classes is enabled.
     * If not specified, they will be automatically computed to obtain class balance during training.
     */
    public float[] _class_sampling_factors;

    /** The maximum number (top K) of predictions to use for hit ratio
     *  computation (for multi-class only, 0 to disable) */
    public int _max_hit_ratio_k = 10;

    /** For classification models, the maximum size (in terms of classes) of
     *  the confusion matrix for it to be printed. This option is meant to
     *  avoid printing extremely large confusion matrices.  */
    public int _max_confusion_matrix_size = 20;
  }

  /** Output from all Supervised Models, includes class distribution
   */
  public abstract static class SupervisedOutput extends Model.Output {
    // Includes the class distribution for all supervised models
    public long [/*nclass*/] _distribution;  // Count of rows-per-class
    public double[/*nclass*/] _priorClassDist;// Fraction of classes out of 1.0
    public double[/*nclass*/] _modelClassDist;// Distribution, after balancing classes

    public SupervisedOutput() { this(null); }

    /** Any final prep-work just before model-building starts, but after the
     *  user has clicked "go".  E.g., converting a response column to an enum
     *  touches the entire column (can be expensive), makes a parallel vec
     *  (Key/Data leak management issues), and might throw IAE if there are too
     *  many classes. */
    public SupervisedOutput( SupervisedModelBuilder b ) {
      super(b);
      if( b==null ) return;     // This Output will be filled by the GUI not a Builder

      // flip the response to an ENUM here

      // Capture the data "shape" the model is valid on, after the response is moved to the end
      _names  = b._train.names  ();
      _domains= b._train.domains();

      // Compute class distribution, handy for most builders
      if( b.isClassifier() && b.isSupervised()) {
        MRUtils.ClassDist cdmt = new MRUtils.ClassDist(b._nclass).doAll(b._response);
        _distribution   = cdmt.dist();
        _priorClassDist = cdmt.rel_dist();
      } else {                    // Regression; only 1 "class"
        _distribution   = new long[] { b._train.numRows() };
        _priorClassDist = new double[] { 1.0f };
      }
      _modelClassDist = _priorClassDist;
    }

    /** @return Returns number of input features */
    @Override public int nfeatures() { return _names.length - 1; }

    @Override public boolean isSupervised() { return true; }

    /** @return number of classes; illegal to call before setting distribution */
    public int nclasses() { return _distribution == null ? 1 : _distribution.length; }
    public boolean isClassifier() { return nclasses()>1; }
    @Override public ModelCategory getModelCategory() {
      return nclasses()==1 
        ? Model.ModelCategory.Regression
        : (nclasses()==2 ? Model.ModelCategory.Binomial : Model.ModelCategory.Multinomial);
    }
  }

  /** Bulk scoring API for one row.  Chunks are all compatible with the model,
   *  and expect the last Chunks are for the final distribution and prediction.
   *  Default method is to just load the data into the tmp array, then call
   *  subclass scoring logic. */
  @Override public double[] score0( Chunk chks[], int row_in_chunk, double[] tmp, double[] preds ) {
    assert chks.length>=_output._names.length; // Last chunk is for the response
    for( int i=0; i<tmp.length; i++ )
      tmp[i] = chks[i].atd(row_in_chunk);
    double[] scored = score0(tmp,preds);
    // Correct probabilities obtained from training on oversampled data back to original distribution
    // C.f. http://gking.harvard.edu/files/0s.pdf Eq.(27)
    if( _output.isClassifier() && _output._priorClassDist !=_output._modelClassDist ) {
      ModelUtils.correctProbabilities(scored,_output._priorClassDist, _output._modelClassDist);
      //set label based on corrected probabilities (max value wins, with deterministic tie-breaking)
      scored[0] = hex.genmodel.GenModel.getPrediction(scored, tmp);
    }
    return scored;
  }

  @Override protected SB toJavaPROB( SB sb) {
    JCodeGen.toStaticVar(sb, "PRIOR_CLASS_DISTRIB", _output._priorClassDist, "Prior class distribution");
    JCodeGen.toStaticVar(sb, "MODEL_CLASS_DISTRIB", _output._modelClassDist, "Class distribution used for model building");
    return sb;
  }
}

