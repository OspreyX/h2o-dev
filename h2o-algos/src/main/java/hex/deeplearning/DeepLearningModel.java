package hex.deeplearning;

import hex.*;
import static hex.deeplearning.DeepLearning.makeDataInfo;
import hex.quantile.Quantile;
import hex.quantile.QuantileModel;
import hex.schemas.DeepLearningModelV3;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import water.*;
import water.api.ModelSchema;
import water.exceptions.H2OIllegalArgumentException;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static hex.ModelMetrics.calcVarImp;
import static java.lang.Double.isNaN;

/**
 * The Deep Learning model
 * It contains a DeepLearningModelInfo with the most up-to-date model,
 * a scoring history, as well as some helpers to indicate the progress
 */

public class DeepLearningModel extends SupervisedModel<DeepLearningModel,DeepLearningModel.DeepLearningParameters,DeepLearningModel.DeepLearningModelOutput> implements Model.DeepFeatures {

  public static class DeepLearningParameters extends SupervisedModel.SupervisedParameters {
    // public int _n_folds;
    public int getNumFolds() { return 0; }

    public boolean _keep_cross_validation_splits;

    /**
     * A model key associated with a previously trained Deep Learning
     * model. This option allows users to build a new model as a
     * continuation of a previously generated model.
     */
    public Key _checkpoint;

    /**
     * If enabled, store the best model (in terms of training error (or validation error, if validation set was provided)
     * under the destination key of this model at the end of training.
     * Only applicable if training is not cancelled.
     */
    public boolean _override_with_best_model = true;

    public boolean _autoencoder = false;

    public boolean _use_all_factor_levels = true;

  /*Neural Net Topology*/
    /**
     * The activation function (non-linearity) to be used the neurons in the hidden layers.
     * Tanh: Hyperbolic tangent function (same as scaled and shifted sigmoid).
     * Rectifier: Chooses the maximum of (0, x) where x is the input value.
     * Maxout: Choose the maximum coordinate of the input vector.
     * With Dropout: Zero out a random user-given fraction of the
     *      incoming weights to each hidden layer during training, for each
     *      training row. This effectively trains exponentially many models at
     *      once, and can improve generalization.
     */
    public Activation _activation = Activation.Rectifier;

    /**
     * The number and size of each hidden layer in the model.
     * For example, if a user specifies "100,200,100" a model with 3 hidden
     * layers will be produced, and the middle hidden layer will have 200
     * neurons.
     */
    public int[] _hidden = new int[] { 200, 200 };

    /**
     * The number of passes over the training dataset to be carried out.
     * It is recommended to start with lower values for initial experiments.
     * This value can be modified during checkpoint restarts and allows continuation
     * of selected models.
     */
    public double _epochs = 10;

    /**
     * The number of training data rows to be processed per iteration. Note that
     * independent of this parameter, each row is used immediately to update the model
     * with (online) stochastic gradient descent. This parameter controls the
     * synchronization period between nodes in a distributed environment and the
     * frequency at which scoring and model cancellation can happen. For example, if
     * it is set to 10,000 on H2O running on 4 nodes, then each node will
     * process 2,500 rows per iteration, sampling randomly from their local data.
     * Then, model averaging between the nodes takes place, and scoring can happen
     * (dependent on scoring interval and duty factor). Special values are 0 for
     * one epoch per iteration, -1 for processing the maximum amount of data
     * per iteration (if **replicate training data** is enabled, N epochs
     * will be trained per iteration on N nodes, otherwise one epoch). Special value
     * of -2 turns on automatic mode (auto-tuning).
     */
    public long _train_samples_per_iteration = -2;

    public double _target_ratio_comm_to_comp = 0.02;

    /**
     * The random seed controls sampling and initialization. Reproducible
     * results are only expected with single-threaded operation (i.e.,
     * when running on one node, turning off load balancing and providing
     * a small dataset that fits in one chunk).  In general, the
     * multi-threaded asynchronous updates to the model parameters will
     * result in (intentional) race conditions and non-reproducible
     * results. Note that deterministic sampling and initialization might
     * still lead to some weak sense of determinism in the model.
     */
    public long _seed = RandomUtils.getRNG(System.currentTimeMillis()).nextLong();

  /*Adaptive Learning Rate*/
    /**
     * The implemented adaptive learning rate algorithm (ADADELTA) automatically
     * combines the benefits of learning rate annealing and momentum
     * training to avoid slow convergence. Specification of only two
     * parameters (rho and epsilon)  simplifies hyper parameter search.
     * In some cases, manually controlled (non-adaptive) learning rate and
     * momentum specifications can lead to better results, but require the
     * specification (and hyper parameter search) of up to 7 parameters.
     * If the model is built on a topology with many local minima or
     * long plateaus, it is possible for a constant learning rate to produce
     * sub-optimal results. Learning rate annealing allows digging deeper into
     * local minima, while rate decay allows specification of different
     * learning rates per layer.  When the gradient is being estimated in
     * a long valley in the optimization landscape, a large learning rate
     * can cause the gradient to oscillate and move in the wrong
     * direction. When the gradient is computed on a relatively flat
     * surface with small learning rates, the model can converge far
     * slower than necessary.
     */
    public boolean _adaptive_rate = true;

    /**
     * The first of two hyper parameters for adaptive learning rate (ADADELTA).
     * It is similar to momentum and relates to the memory to prior weight updates.
     * Typical values are between 0.9 and 0.999.
     * This parameter is only active if adaptive learning rate is enabled.
     */
    public double _rho = 0.99;

    /**
     * The second of two hyper parameters for adaptive learning rate (ADADELTA).
     * It is similar to learning rate annealing during initial training
     * and momentum at later stages where it allows forward progress.
     * Typical values are between 1e-10 and 1e-4.
     * This parameter is only active if adaptive learning rate is enabled.
     */
    public double _epsilon = 1e-8;

  /*Learning Rate*/
    /**
     * When adaptive learning rate is disabled, the magnitude of the weight
     * updates are determined by the user specified learning rate
     * (potentially annealed), and are a function  of the difference
     * between the predicted value and the target value. That difference,
     * generally called delta, is only available at the output layer. To
     * correct the output at each hidden layer, back propagation is
     * used. Momentum modifies back propagation by allowing prior
     * iterations to influence the current update. Using the momentum
     * parameter can aid in avoiding local minima and the associated
     * instability. Too much momentum can lead to instabilities, that's
     * why the momentum is best ramped up slowly.
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public double _rate = .005;

    /**
     * Learning rate annealing reduces the learning rate to "freeze" into
     * local minima in the optimization landscape.  The annealing rate is the
     * inverse of the number of training samples it takes to cut the learning rate in half
     * (e.g., 1e-6 means that it takes 1e6 training samples to halve the learning rate).
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public double _rate_annealing = 1e-6;

    /**
     * The learning rate decay parameter controls the change of learning rate across layers.
     * For example, assume the rate parameter is set to 0.01, and the rate_decay parameter is set to 0.5.
     * Then the learning rate for the weights connecting the input and first hidden layer will be 0.01,
     * the learning rate for the weights connecting the first and the second hidden layer will be 0.005,
     * and the learning rate for the weights connecting the second and third hidden layer will be 0.0025, etc.
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public double _rate_decay = 1.0;

  /*Momentum*/
    /**
     * The momentum_start parameter controls the amount of momentum at the beginning of training.
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public double _momentum_start = 0;

    /**
     * The momentum_ramp parameter controls the amount of learning for which momentum increases
     * (assuming momentum_stable is larger than momentum_start). The ramp is measured in the number
     * of training samples.
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public double _momentum_ramp = 1e6;

    /**
     * The momentum_stable parameter controls the final momentum value reached after momentum_ramp training samples.
     * The momentum used for training will remain the same for training beyond reaching that point.
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public double _momentum_stable = 0;

    /**
     * The Nesterov accelerated gradient descent method is a modification to
     * traditional gradient descent for convex functions. The method relies on
     * gradient information at various points to build a polynomial approximation that
     * minimizes the residuals in fewer iterations of the descent.
     * This parameter is only active if adaptive learning rate is disabled.
     */
    public boolean _nesterov_accelerated_gradient = true;

  /*Regularization*/
    /**
     * A fraction of the features for each training row to be omitted from training in order
     * to improve generalization (dimension sampling).
     */
    public double _input_dropout_ratio = 0.0;

    /**
     * A fraction of the inputs for each hidden layer to be omitted from training in order
     * to improve generalization. Defaults to 0.5 for each hidden layer if omitted.
     */
    public double[] _hidden_dropout_ratios;

    /**
     * A regularization method that constrains the absolute value of the weights and
     * has the net effect of dropping some weights (setting them to zero) from a model
     * to reduce complexity and avoid overfitting.
     */
    public double _l1 = 0.0;

    /**
     *  A regularization method that constrdains the sum of the squared
     * weights. This method introduces bias into parameter estimates, but
     * frequently produces substantial gains in modeling as estimate variance is
     * reduced.
     */
    public double _l2 = 0.0;

    /**
     *  A maximum on the sum of the squared incoming weights into
     * any one neuron. This tuning parameter is especially useful for unbound
     * activation functions such as Maxout or Rectifier.
     */
    public float _max_w2 = Float.POSITIVE_INFINITY;

  /*Initialization*/
    /**
     * The distribution from which initial weights are to be drawn. The default
     * option is an optimized initialization that considers the size of the network.
     * The "uniform" option uses a uniform distribution with a mean of 0 and a given
     * interval. The "normal" option draws weights from the standard normal
     * distribution with a mean of 0 and given standard deviation.
     */
    public InitialWeightDistribution _initial_weight_distribution = InitialWeightDistribution.UniformAdaptive;

    /**
     * The scale of the distribution function for Uniform or Normal distributions.
     * For Uniform, the values are drawn uniformly from -initial_weight_scale...initial_weight_scale.
     * For Normal, the values are drawn from a Normal distribution with a standard deviation of initial_weight_scale.
     */
    public double _initial_weight_scale = 1.0;

    /**
     * The loss (error) function to be minimized by the model.
     * Cross Entropy loss is used when the model output consists of independent
     * hypotheses, and the outputs can be interpreted as the probability that each
     * hypothesis is true. Cross entropy is the recommended loss function when the
     * target values are class labels, and especially for imbalanced data.
     * It strongly penalizes error in the prediction of the actual class label.
     * Mean Square loss is used when the model output are continuous real values, but can
     * be used for classification as well (where it emphasizes the error on all
     * output classes, not just for the actual class).
     */
    public Loss _loss = Loss.Automatic;

  /*Scoring*/
    /**
     * The minimum time (in seconds) to elapse between model scoring. The actual
     * interval is determined by the number of training samples per iteration and the scoring duty cycle.
     */
    public double _score_interval = 5;

    /**
     * The number of training dataset points to be used for scoring. Will be
     * randomly sampled. Use 0 for selecting the entire training dataset.
     */
    public long _score_training_samples = 10000l;

    /**
     * The number of validation dataset points to be used for scoring. Can be
     * randomly sampled or stratified (if "balance classes" is set and "score
     * validation sampling" is set to stratify). Use 0 for selecting the entire
     * training dataset.
     */
    public long _score_validation_samples = 0l;

    /**
     * Maximum fraction of wall clock time spent on model scoring on training and validation samples,
     * and on diagnostics such as computation of feature importances (i.e., not on training).
     */
    public double _score_duty_cycle = 0.1;

    /**
     * The stopping criteria in terms of classification error (1-accuracy) on the
     * training data scoring dataset. When the error is at or below this threshold,
     * training stops.
     */
    public double _classification_stop = 0;

    /**
     * The stopping criteria in terms of regression error (MSE) on the training
     * data scoring dataset. When the error is at or below this threshold, training
     * stops.
     */
    public double _regression_stop = 1e-6;

    /**
     * Enable quiet mode for less output to standard output.
     */
    public boolean _quiet_mode = false;

    /**
     * Method used to sample the validation dataset for scoring, see Score Validation Samples above.
     */
    public ClassSamplingMethod _score_validation_sampling = ClassSamplingMethod.Uniform;

  /*Misc*/
    /**
     * Gather diagnostics for hidden layers, such as mean and RMS values of learning
     * rate, momentum, weights and biases.
     */
    public boolean _diagnostics = true;

    /**
     * Whether to compute variable importances for input features.
     * The implemented method (by Gedeon) considers the weights connecting the
     * input features to the first two hidden layers.
     */
    public boolean _variable_importances = false;

    /**
     * Enable fast mode (minor approximation in back-propagation), should not affect results significantly.
     */
    public boolean _fast_mode = true;

    /**
     * Ignore constant training columns (no information can be gained anyway).
     */
    public boolean _ignore_const_cols = true;

    /**
     * Increase training speed on small datasets by splitting it into many chunks
     * to allow utilization of all cores.
     */
    public boolean _force_load_balance = true;

    /**
     * Replicate the entire training dataset onto every node for faster training on small datasets.
     */
    public boolean _replicate_training_data = true;

    /**
     * Run on a single node for fine-tuning of model parameters. Can be useful for
     * checkpoint resumes after training on multiple nodes for fast initial
     * convergence.
     */
    public boolean _single_node_mode = false;

    /**
     * Enable shuffling of training data (on each node). This option is
     * recommended if training data is replicated on N nodes, and the number of training samples per iteration
     * is close to N times the dataset size, where all nodes train will (almost) all
     * the data. It is automatically enabled if the number of training samples per iteration is set to -1 (or to N
     * times the dataset size or larger).
     */
    public boolean _shuffle_training_data = false;

    public MissingValuesHandling _missing_values_handling = MissingValuesHandling.MeanImputation;

    public boolean _sparse = false;

    public boolean _col_major = false;

    public double _average_activation = 0;

    public double _sparsity_beta = 0;

    /**
     * Max. number of categorical features, enforced via hashing (Experimental)
     */
    public int _max_categorical_features = Integer.MAX_VALUE;

    /**
     * Force reproducibility on small data (will be slow - only uses 1 thread)
     */
    public boolean _reproducible = false;

    public boolean _export_weights_and_biases = false;

    public enum MissingValuesHandling {
      Skip, MeanImputation
    }

    public enum ClassSamplingMethod {
      Uniform, Stratified
    }

    public enum InitialWeightDistribution {
      UniformAdaptive, Uniform, Normal
    }

    /**
     * Activation functions
     */
    public enum Activation {
      Tanh, TanhWithDropout, Rectifier, RectifierWithDropout, Maxout, MaxoutWithDropout
    }

    /**
     * Loss functions
     * Absolute, MeanSquare, Huber for regression
     * Absolute, MeanSquare, Huber or CrossEntropy for classification
     */
    public enum Loss {
      Automatic, MeanSquare, CrossEntropy, Huber, Absolute
    }

    void validate( DeepLearning dl, boolean expensive ) {
      dl.hide("_score_each_iteration", "Not used by Deep Learning.");
      boolean classification = expensive || dl._nclass != 0 ? dl.isClassifier() : _loss == Loss.CrossEntropy;
      if (_hidden == null || _hidden.length == 0) dl.error("_hidden", "There must be at least one hidden layer.");

      for( int h : _hidden ) if( h<=0 ) dl.error("_hidden", "Hidden layer size must be positive.");

      if (!_autoencoder) {
        if (_valid == null)
          dl.hide("_score_validation_samples", "score_validation_samples requires a validation frame.");

        if (classification) {
          dl.hide("_regression_stop", "regression_stop is used only with regression.");
        } else {
          dl.hide("_classification_stop", "classification_stop is used only with classification.");
//          dl.hide("_max_hit_ratio_k", "max_hit_ratio_k is used only with classification.");
//          dl.hide("_balance_classes", "balance_classes is used only with classification.");
        }
//        if( !classification || !_balance_classes )
//          dl.hide("_class_sampling_factors", "class_sampling_factors requires both classification and balance_classes.");
        if (!classification && _valid != null || _valid == null)
          dl.hide("_score_validation_sampling", "score_validation_sampling requires classification and a validation frame.");
      }

      if (_activation != Activation.TanhWithDropout && _activation != Activation.MaxoutWithDropout && _activation != Activation.RectifierWithDropout)
        dl.hide("_hidden_dropout_ratios", "hidden_dropout_ratios requires a dropout activation function.");
      if (_hidden_dropout_ratios == null) {
        // ok - nothing to check
      }
      else if (_hidden_dropout_ratios.length != _hidden.length) {
        dl.error("_hidden_dropout_ratios", "Must have " + _hidden.length + " hidden layer dropout ratios.");
      }
      else if (_activation != Activation.TanhWithDropout && _activation != Activation.MaxoutWithDropout && _activation != Activation.RectifierWithDropout) {
        if (!_quiet_mode) dl.hide("_hidden_dropout_ratios", "Ignoring hidden_dropout_ratios because a non-dropout activation function was specified.");
      }
      else if (ArrayUtils.maxValue(_hidden_dropout_ratios) >= 1 || ArrayUtils.minValue(_hidden_dropout_ratios) < 0) {
        dl.error("_hidden_dropout_ratios", "Hidden dropout ratios must be >= 0 and <1.");
      }
      if (_input_dropout_ratio < 0 || _input_dropout_ratio >= 1)
        dl.error("_input_dropout_ratio", "Input dropout must be >= 0 and <1.");
      if (_score_duty_cycle < 0 || _score_duty_cycle > 1)
        dl.error("_score_duty_cycle", "Score duty cycle must be >= 0 and <=1.");
      if (_l1 < 0)
        dl.error("_l1", "L1 penalty must be >= 0.");
      if (_l2 < 0)
        dl.error("_l2", "L2 penalty must be >= 0.");
      if (H2O.CLOUD.size() == 1 && _replicate_training_data)
        dl.hide("_replicate_training_data", "replicate_training_data is only valid with cloud size greater than 1.");
      if (_single_node_mode && (H2O.CLOUD.size() == 1 || !_replicate_training_data))
        dl.hide("_single_node_mode", "single_node_mode is only used with multi-node operation with replicated training data.");
      if (_autoencoder)
        dl.hide("_use_all_factor_levels", "use_all_factor_levels is mandatory in combination with autoencoder.");
      if (getNumFolds() != 0)
        dl.hide("_override_with_best_model", "override_with_best_model is unsupported in combination with n-fold cross-validation.");
      if (_adaptive_rate) {
        dl.hide("_rate", "rate is not used with adaptive_rate.");
        dl.hide("_rate_annealing", "rate_annealing is not used with adaptive_rate.");
        dl.hide("_rate_decay", "rate_decay is not used with adaptive_rate.");
        dl.hide("_momentum_start", "momentum_start is not used with adaptive_rate.");
        dl.hide("_momentum_ramp", "momentum_ramp is not used with adaptive_rate.");
        dl.hide("_momentum_stable", "momentum_stable is not used with adaptive_rate.");
        dl.hide("_nesterov_accelerated_gradient", "nesterov_accelerated_gradient is not used with adaptive_rate.");
      } else {
        // ! adaptive_rate
        dl.hide("_rho", "rho is only used with adaptive_rate.");
        dl.hide("_epsilon", "epsilon is only used with adaptive_rate.");
      }
      if (_initial_weight_distribution == InitialWeightDistribution.UniformAdaptive) {
        dl.hide("_initial_weight_scale", "initial_weight_scale is not used if initial_weight_distribution == UniformAdaptive.");
      }
      if (getNumFolds() != 0)
        dl.error("_n_folds", "n_folds is not yet implemented.");

      if (_loss == null) {
        if (expensive || dl._nclass != 0) {
          dl.error("_loss", "Loss function must be specified. Try CrossEntropy for categorical response (classification), MeanSquare, Absolute or Huber for numerical response (regression).");
        }
        //otherwise, we might not know whether classification=true or false (from R, for example, the training data isn't known when init(false) is called).
      } else if (_loss != Loss.Automatic) {
        if (_autoencoder && _loss == Loss.CrossEntropy)
          dl.error("_loss", "Cannot use CrossEntropy loss for auto-encoder.");
        if (!classification && _loss == Loss.CrossEntropy)
          dl.error("_loss", "For CrossEntropy loss, the response must be categorical.");
      }
      if (!classification && _loss == Loss.CrossEntropy)
        dl.error("_loss", "For CrossEntropy loss, the response must be categorical. Either select MeanSquare, Absolute or Huber loss for regression, or use a categorical response.");
      if (_score_training_samples < 0)
        dl.error("_score_training_samples", "Number of training samples for scoring must be >= 0 (0 for all).");
      if (_score_validation_samples < 0)
        dl.error("_score_validation_samples", "Number of training samples for scoring must be >= 0 (0 for all).");
      if(_autoencoder && _sparsity_beta > 0) {
        if (_activation == Activation.Tanh || _activation == Activation.TanhWithDropout) {
          if (_average_activation >= 1 || _average_activation <= -1)
            dl.error("_average_activation", "Tanh average activation must be in (-1,1).");
        }
        else if (_activation == Activation.Rectifier || _activation == Activation.RectifierWithDropout) {
          if (_average_activation <= 0)
            dl.error("_average_activation", "Rectifier average activation must be positive.");
        }
      }
      if (!_autoencoder && _sparsity_beta != 0) dl.info("_sparsity_beta", "Sparsity beta can only be used for autoencoder.");

      // reason for the error message below is that validation might not have the same horizontalized features as the training data (or different order)
      if (_autoencoder && _activation == Activation.Maxout) dl.error("_activation", "Maxout activation is not supported for auto-encoder.");
      if (_max_categorical_features < 1) dl.error("_max_categorical_features", "max_categorical_features must be at least 1.");

      if (!_sparse && _col_major) {
        dl.error("_col_major", "Cannot use column major storage for non-sparse data handling.");
      }
      if (expensive) {
        if (!classification && _balance_classes) {
          dl.error("_balance_classes", "balance_classes requires classification.");
        }
        if (_class_sampling_factors != null && !_balance_classes) {
          dl.error("_class_sampling_factors", "class_sampling_factors requires balance_classes to be enabled.");
        }
      }
    }
  }

  public static class DeepLearningModelOutput extends SupervisedModel.SupervisedOutput {
    @Override public int nfeatures() {
      return _names.length - (autoencoder ? 0 : 1);
    }
    public DeepLearningModelOutput() { super(); }
    public DeepLearningModelOutput(DeepLearning b) { super(b); }
    boolean autoencoder;
    DeepLearningScoring errors;
    Key[] weights;
    Key[] biases;
    public TwoDimTable _variable_importances;

    @Override public ModelCategory getModelCategory() {
      return autoencoder ? ModelCategory.AutoEncoder : super.getModelCategory();
    }

    @Override public boolean isSupervised() { return !autoencoder; }
  }

  // Default publicly visible Schema is V2
  public ModelSchema schema() { return new DeepLearningModelV3(); }

  private volatile DeepLearningModelInfo model_info;
  void set_model_info(DeepLearningModelInfo mi) { model_info = mi; }
  final public DeepLearningModelInfo model_info() { return model_info; }
  final public VarImp varImp() { return _output.errors.variable_importances; }

  public long run_time;
  private long start_time;

  public long actual_train_samples_per_iteration;
  public double time_for_communication_us; //helper for auto-tuning: time in microseconds for collective bcast/reduce of the model

  public double epoch_counter;

  public long training_rows;

  public long validation_rows;

  private DeepLearningScoring[] errors;
  public DeepLearningScoring[] scoring_history() { return errors; }

  // Keep the best model so far, based on a single criterion (overall class. error or MSE)
  private float _bestError = Float.POSITIVE_INFINITY;

  public Key actual_best_model_key;

  // return the most up-to-date model metrics
  DeepLearningScoring last_scored() { return errors == null ? null : errors[errors.length-1]; }

  /**
   * Get the parameters actually used for model building, not the user-given ones (_parms)
   * They might differ since some defaults are filled in, and some invalid combinations are auto-disabled in modifyParams
   * @return actually used parameters
   */
  public final DeepLearningParameters get_params() { return model_info.get_params(); }

//  double missingColumnsType() { return get_params()._sparse ? 0 : Double.NaN; }

  public float error() { return (float) (_output.isClassifier() ? cm().err() : mse()); }

  @Override public ModelMetrics.MetricBuilder makeMetricBuilder(String[] domain) {
    switch(_output.getModelCategory()) {
      case Binomial:    return new ModelMetricsBinomial.MetricBuilderBinomial(domain);
      case Multinomial: return new ModelMetricsMultinomial.MetricBuilderMultinomial(_output.nclasses(),domain);
      case Regression:  return new ModelMetricsRegression.MetricBuilderRegression();
      case AutoEncoder: return new ModelMetricsAutoEncoder.MetricBuilderAutoEncoder(_output.nfeatures());
      default: throw H2O.unimpl("Invalid ModelCategory " + _output.getModelCategory());
    }
  }

  public int compareTo(DeepLearningModel o) {
    if (o._output.isClassifier() != _output.isClassifier()) throw new UnsupportedOperationException("Cannot compare classifier against regressor.");
    if (o._output.nclasses() != _output.nclasses()) throw new UnsupportedOperationException("Cannot compare models with different number of classes.");
    return (error() < o.error() ? -1 : error() > o.error() ? 1 : 0);
  }

  public static class DeepLearningScoring extends Iced {
//    static final int API_WEAVER = 1;
//    static public DocGen.FieldDoc[] DOC_FIELDS;

    public double epoch_counter;
    public long training_samples;
    public long training_time_ms;

    //training/validation sets
    boolean validation;
    int num_folds;
    public long score_training_samples;
    public long score_validation_samples;

    public boolean classification;

    VarImp variable_importances;

    // classification
    public ConfusionMatrix train_confusion_matrix;
    public ConfusionMatrix valid_confusion_matrix;
    public double train_err = Double.NaN;
    public double valid_err = Double.NaN;
    public double train_logloss = Double.NaN;
    public double valid_logloss = Double.NaN;
    public AUC2 training_AUC;
    public AUC2 validation_AUC;
    public float[] train_hitratio; // "Hit ratio on training data"
    public float[] valid_hitratio; // "Hit ratio on validation data"

    // regression
    public double training_MSE = Double.NaN;
    public double validation_MSE = Double.NaN;
    public double training_R2 = Double.NaN;
    public double validation_R2 = Double.NaN;

    public long scoring_time;

    DeepLearningScoring deep_clone() {
      AutoBuffer ab = new AutoBuffer();
      this.write(ab);
      ab.flipForReading();
      return (DeepLearningScoring) new DeepLearningScoring().read(ab);
    }

    @Override public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Training MSE: " + training_MSE + "\n");
      sb.append("Training R^2: " + training_R2 + "\n");
      if (classification) {
        sb.append("Training LogLoss: " + train_logloss + "\n");
        sb.append("Training " + train_confusion_matrix.table().toString(1));
        sb.append("Training Misclassification"
                + (training_AUC != null ? " [using threshold for " + AUC2.DEFAULT_CM.toString().replace("_", " ") + "]: " : ": ")
                + String.format("%.2f", 100 * train_err) + "%");
        if (training_AUC != null) sb.append(", AUC: " + String.format("%.4f", 100 * training_AUC._auc) + "%");
      }
      if (validation || num_folds>0) {
        if (num_folds > 0) {
          sb.append("\nDoing " + num_folds + "-fold cross-validation:");
        }
        sb.append("\nValidation MSE: " + validation_MSE + "\n");
        sb.append("Validation R^2: " + validation_R2 + "\n");
        if (classification) {
          sb.append("Validation LogLoss: " + valid_logloss + "\n");
          sb.append("Validation " + valid_confusion_matrix.table().toString(1));
          sb.append("Validation Misclassification"
                  + (validation_AUC != null ? " [using threshold for " + AUC2.DEFAULT_CM.toString().replace("_", " ") + "]: " : ": ")
                  + String.format("%.2f", (100 * valid_err)) + "%");
          if (validation_AUC != null) sb.append(", AUC: " + String.format("%.4f", 100 * validation_AUC._auc) + "%");
        }
      }
      sb.append("\n");
      return sb.toString();
    }
  }

  final private static class ConfMat extends ConfusionMatrix {
    final private double _err;
    final private double _f1;
    public ConfMat(double err, double f1) {
      super(null, null);
      _err=err;
      _f1=f1;
    }
    @Override public double err() { return _err; }
    @Override public double F1() { return _f1; }
  }

  public ConfusionMatrix cm() {
    final DeepLearningScoring lasterror = last_scored();
    if (lasterror == null) return null;
    ConfusionMatrix cm = lasterror.validation || lasterror.num_folds > 0 ?
            lasterror.valid_confusion_matrix :
            lasterror.train_confusion_matrix;
    if (cm == null ) {
      if (lasterror.validation || lasterror.num_folds > 0) {
        return new ConfMat(lasterror.valid_err, lasterror.validation_AUC != null ? lasterror.validation_AUC.maxF1() : 0);
      } else {
        return new ConfMat(lasterror.train_err, lasterror.training_AUC != null ? lasterror.training_AUC.maxF1() : 0);
      }
    }
    return cm;
  }

  public double mse() {
    if (errors == null) return Double.NaN;
    return last_scored().validation || last_scored().num_folds > 0 ? last_scored().validation_MSE : last_scored().training_MSE;
  }

  public double logloss() {
    if (errors == null) return Double.NaN;
    return last_scored().validation || last_scored().num_folds > 0 ? last_scored().valid_logloss : last_scored().train_logloss;
  }

  private TwoDimTable createScoringHistoryTable(DeepLearningScoring[] errors) {
    List<String> colHeaders = new ArrayList<>();
    List<String> colTypes = new ArrayList<>();
    List<String> colFormat = new ArrayList<>();
    colHeaders.add("Timestamp"); colTypes.add("string"); colFormat.add("%s");
    colHeaders.add("Duration"); colTypes.add("string"); colFormat.add("%s");
    colHeaders.add("Training Speed"); colTypes.add("string"); colFormat.add("%s");
    colHeaders.add("Epochs"); colTypes.add("double"); colFormat.add("%.5f");
    colHeaders.add("Samples"); colTypes.add("long"); colFormat.add("%d");
    colHeaders.add("Training MSE"); colTypes.add("double"); colFormat.add("%.5f");

    if (!_output.autoencoder) {
      colHeaders.add("Training R^2");
      colTypes.add("double");
      colFormat.add("%.5f");
    }
    if (_output.isClassifier()) {
      colHeaders.add("Training LogLoss");
      colTypes.add("double");
      colFormat.add("%.5f");
    }
    if (_output.getModelCategory() == ModelCategory.Binomial) {
      colHeaders.add("Training AUC");
      colTypes.add("double");
      colFormat.add("%.5f");
    }
    if (_output.getModelCategory() == ModelCategory.Binomial || _output.getModelCategory() == ModelCategory.Multinomial) {
      colHeaders.add("Training Classification Error");
      colTypes.add("double");
      colFormat.add("%.5f");
    }
    if (get_params()._valid != null) {
      colHeaders.add("Validation MSE"); colTypes.add("double"); colFormat.add("%.5f");
      if (!_output.autoencoder) {
        colHeaders.add("Validation R^2");
        colTypes.add("double");
        colFormat.add("%.5f");
      }
      if (_output.isClassifier()) {
        colHeaders.add("Validation LogLoss");
        colTypes.add("double");
        colFormat.add("%.5f");
      }
      if (_output.getModelCategory() == ModelCategory.Binomial) {
        colHeaders.add("Validation AUC");
        colTypes.add("double");
        colFormat.add("%.5f");
      }
      if (_output.isClassifier()) {
        colHeaders.add("Validation Classification Error");
        colTypes.add("double");
        colFormat.add("%.5f");
      }
    } else if (get_params().getNumFolds() > 0) {
      colHeaders.add("Cross-Validation MSE"); colTypes.add("double"); colFormat.add("%.5f");
//      colHeaders.add("Validation R^2"); colTypes.add("double"); colFormat.add("%g");
      if (_output.getModelCategory() == ModelCategory.Binomial) {
        colHeaders.add("Cross-Validation AUC");
        colTypes.add("double");
        colFormat.add("%.5f");
      }
      if (_output.isClassifier()) {
        colHeaders.add("Cross-Validation Classification Error");
        colTypes.add("double");
        colFormat.add("%.5f");
      }
    }

    final int rows = errors.length;
    TwoDimTable table = new TwoDimTable(
            "Scoring History", null,
            new String[rows],
            colHeaders.toArray(new String[0]),
            colTypes.toArray(new String[0]),
            colFormat.toArray(new String[0]),
            "");
    int row = 0;
    for( int i = 0; i<errors.length ; i++ ) {
      final DeepLearningScoring e = errors[i];
      int col = 0;
      assert(row < table.getRowDim());
      assert(col < table.getColDim());
      DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
      table.set(row, col++, fmt.print(start_time + e.training_time_ms));
      table.set(row, col++, PrettyPrint.msecs(e.training_time_ms, true));
      table.set(row, col++, e.training_time_ms == 0 ? null : (String.format("%.3f", e.training_samples/(e.training_time_ms/1e3)) + " rows/sec"));
      table.set(row, col++, e.epoch_counter);
      table.set(row, col++, e.training_samples);
      table.set(row, col++, e.training_MSE);
      if (!_output.autoencoder) {
        table.set(row, col++, e.training_R2);
      }
      if (_output.isClassifier()) {
        table.set(row, col++, e.train_logloss);
      }
      if (_output.getModelCategory() == ModelCategory.Binomial) {
        table.set(row, col++, e.training_AUC != null ? e.training_AUC._auc : Double.NaN);
      }
      if (_output.isClassifier()) {
        table.set(row, col++, e.train_err);
      }
      if (get_params()._valid != null) {
        table.set(row, col++, e.validation_MSE);
        if (!_output.autoencoder) {
          table.set(row, col++, e.validation_R2);
        }
        if (_output.isClassifier()) {
          table.set(row, col++, e.valid_logloss);
        }
        if (_output.getModelCategory() == ModelCategory.Binomial) {
          table.set(row, col++, e.validation_AUC != null ? e.validation_AUC._auc : Double.NaN);
        }
        if (_output.isClassifier()) {
          table.set(row, col++, e.valid_err);
        }
      }
      else if(get_params().getNumFolds() > 1) {
        throw H2O.unimpl("n_folds >= 2 is not (yet) implemented.");
      }
      row++;
    }
    return table;
  }


  // This describes the model, together with the parameters
  // This will be shared: one per node
  public static class DeepLearningModelInfo extends Iced {

    public TwoDimTable summaryTable;
    private DataInfo data_info;
    public DataInfo data_info() { return data_info; }

    // model is described by parameters and the following arrays
    private Neurons.DenseRowMatrix[] dense_row_weights; //one 2D weight matrix per layer (stored as a 1D array each)
    private Neurons.DenseColMatrix[] dense_col_weights; //one 2D weight matrix per layer (stored as a 1D array each)
    private Neurons.DenseVector[] biases; //one 1D bias array per layer
    private Neurons.DenseVector[] avg_activations; //one 1D array per hidden layer

    // helpers for storing previous step deltas
    // Note: These two arrays *could* be made transient and then initialized freshly in makeNeurons() and in DeepLearningTask.initLocal()
    // But then, after each reduction, the weights would be lost and would have to restart afresh -> not *exactly* right, but close...
    private Neurons.DenseRowMatrix[] dense_row_weights_momenta;
    private Neurons.DenseColMatrix[] dense_col_weights_momenta;
    private Neurons.DenseVector[] biases_momenta;

    // helpers for AdaDelta
    private Neurons.DenseRowMatrix[] dense_row_ada_dx_g;
    private Neurons.DenseColMatrix[] dense_col_ada_dx_g;
    private Neurons.DenseVector[] biases_ada_dx_g;

    // compute model size (number of model parameters required for making predictions)
    // momenta are not counted here, but they are needed for model building
    public long size() {
      long siz = 0;
      for (Neurons.Matrix w : dense_row_weights) if (w != null) siz += w.size();
      for (Neurons.Matrix w : dense_col_weights) if (w != null) siz += w.size();
      for (Neurons.Vector b : biases) siz += b.size();
      return siz;
    }

    // accessors to (shared) weights and biases - those will be updated racily (c.f. Hogwild!)
    boolean has_momenta() { return get_params()._momentum_start != 0 || get_params()._momentum_stable != 0; }
    boolean adaDelta() { return get_params()._adaptive_rate; }
    public final Neurons.Matrix get_weights(int i) { return dense_row_weights[i] == null ? dense_col_weights[i] : dense_row_weights[i]; }
    public final Neurons.DenseVector get_biases(int i) { return biases[i]; }
    public final Neurons.Matrix get_weights_momenta(int i) { return dense_row_weights_momenta[i] == null ? dense_col_weights_momenta[i] : dense_row_weights_momenta[i]; }
    public final Neurons.DenseVector get_biases_momenta(int i) { return biases_momenta[i]; }
    public final Neurons.Matrix get_ada_dx_g(int i) { return dense_row_ada_dx_g[i] == null ? dense_col_ada_dx_g[i] : dense_row_ada_dx_g[i]; }
    public final Neurons.DenseVector get_biases_ada_dx_g(int i) { return biases_ada_dx_g[i]; }
    //accessor to shared parameter defining avg activations
    public final Neurons.DenseVector get_avg_activations(int i) { return avg_activations[i]; }

    private DeepLearningParameters parameters;
    public final DeepLearningParameters get_params() { return parameters; }

    private float[] mean_rate;

    private float[] rms_rate;

    private float[] mean_bias;

    private float[] rms_bias;

    private float[] mean_weight;

    public float[] rms_weight;

    public float[] mean_a;

    private volatile boolean unstable = false;
    public boolean unstable() { return unstable; }
    public void set_unstable() { if (!unstable) computeStats(); unstable = true; }

    private long processed_global;
    public synchronized long get_processed_global() { return processed_global; }
    public synchronized void set_processed_global(long p) { processed_global = p; }
    public synchronized void add_processed_global(long p) { processed_global += p; }

    private long processed_local;
    public synchronized long get_processed_local() { return processed_local; }
    public synchronized void set_processed_local(long p) { processed_local = p; }
    public synchronized void add_processed_local(long p) { processed_local += p; }

    public synchronized long get_processed_total() { return processed_global + processed_local; }

    // package local helpers
    int[] units; //number of neurons per layer, extracted from parameters and from datainfo

    final boolean _classification; // Classification cache (nclasses>1)
    final Frame _train;         // Prepared training frame
    final Frame _valid;         // Prepared validation frame

    public DeepLearningModelInfo() {
      _classification = false;
      _train = _valid = null;
    }

    public DeepLearningModelInfo(final DeepLearningParameters params, final DataInfo dinfo, boolean classification, Frame train, Frame valid) {
      _classification = classification;
      _train = train;
      _valid = valid;
      data_info = dinfo;
      parameters = (DeepLearningParameters)params.clone();
      modifyParms(parameters, parameters, _classification);

      final int num_input = dinfo.fullN();
      final int num_output = get_params()._autoencoder ? num_input : (_classification ? train.lastVec().cardinality() : 1);
      assert(num_input > 0);
      assert(num_output > 0);
      if (has_momenta() && adaDelta()) throw new IllegalArgumentException("Cannot have non-zero momentum and adaptive rate at the same time.");
      final int layers=get_params()._hidden.length;
      // units (# neurons for each layer)
      units = new int[layers+2];
      if (get_params()._max_categorical_features <= Integer.MAX_VALUE - dinfo._nums)
        units[0] = Math.min(dinfo._nums + get_params()._max_categorical_features, num_input);
      else
        units[0] = num_input;
      System.arraycopy(get_params()._hidden, 0, units, 1, layers);
      units[layers+1] = num_output;

      boolean printLevels = units[0] > 1000L;
      boolean warn = units[0] > 100000L;
      if (printLevels) {
        final String[][] domains = dinfo._adaptedFrame.domains();
        int[] levels = new int[domains.length];
        for (int i=0; i<levels.length; ++i) {
          levels[i] = domains[i] != null ? domains[i].length : 0;
        }
        Arrays.sort(levels);
        if (warn) {
          Log.warn("===================================================================================================================================");
          Log.warn(num_input + " input features" + (dinfo._cats > 0 ? " (after categorical one-hot encoding)" : "") + ". Can be slow and require a lot of memory.");
        }
        if (levels[levels.length-1] > 0) {
          int levelcutoff = levels[levels.length-1-Math.min(10, levels.length-1)];
          int count = 0;
          for (int i=0; i<dinfo._adaptedFrame.numCols() - (get_params()._autoencoder ? 0 : 1) && count < 10; ++i) {
            if (dinfo._adaptedFrame.domains()[i] != null && dinfo._adaptedFrame.domains()[i].length >= levelcutoff) {
              if (warn) {
                Log.warn("Categorical feature '" + dinfo._adaptedFrame._names[i] + "' has cardinality " + dinfo._adaptedFrame.domains()[i].length + ".");
              } else {
                Log.info("Categorical feature '" + dinfo._adaptedFrame._names[i] + "' has cardinality " + dinfo._adaptedFrame.domains()[i].length + ".");
              }
            }
            count++;
          }
        }
        if (warn) {
          Log.warn("Suggestions:");
          Log.warn(" *) Limit the size of the first hidden layer");
          if (dinfo._cats > 0) {
            Log.warn(" *) Limit the total number of one-hot encoded features with the parameter 'max_categorical_features'");
            Log.warn(" *) Run h2o.interaction(...,pairwise=F) on high-cardinality categorical columns to limit the factor count, see http://learn.h2o.ai");
          }
          Log.warn("===================================================================================================================================");
        }
      }

      // weights (to connect layers)
      dense_row_weights = new Neurons.DenseRowMatrix[layers+1];
      dense_col_weights = new Neurons.DenseColMatrix[layers+1];

      // decide format of weight matrices row-major or col-major
      if (get_params()._col_major) dense_col_weights[0] = new Neurons.DenseColMatrix(units[1], units[0]);
      else dense_row_weights[0] = new Neurons.DenseRowMatrix(units[1], units[0]);
      for (int i = 1; i <= layers; ++i)
        dense_row_weights[i] = new Neurons.DenseRowMatrix(units[i + 1] /*rows*/, units[i] /*cols*/);

      // biases (only for hidden layers and output layer)
      biases = new Neurons.DenseVector[layers+1];
      for (int i=0; i<=layers; ++i) biases[i] = new Neurons.DenseVector(units[i+1]);
      // average activation (only for hidden layers)
      if (get_params()._autoencoder && get_params()._sparsity_beta > 0) {
        avg_activations = new Neurons.DenseVector[layers];
        mean_a = new float[layers];
        for (int i = 0; i < layers; ++i) avg_activations[i] = new Neurons.DenseVector(units[i + 1]);
      }
      fillHelpers();
      // for diagnostics
      mean_rate = new float[units.length];
      rms_rate = new float[units.length];
      mean_bias = new float[units.length];
      rms_bias = new float[units.length];
      mean_weight = new float[units.length];
      rms_weight = new float[units.length];
    }

    // deep clone all weights/biases
    DeepLearningModelInfo deep_clone() {
      AutoBuffer ab = new AutoBuffer();
      this.write(ab);
      ab.flipForReading();
      return (DeepLearningModelInfo) new DeepLearningModelInfo().read(ab);
    }

    void fillHelpers() {
      if (has_momenta()) {
        dense_row_weights_momenta = new Neurons.DenseRowMatrix[dense_row_weights.length];
        dense_col_weights_momenta = new Neurons.DenseColMatrix[dense_col_weights.length];
        if (dense_row_weights[0] != null)
          dense_row_weights_momenta[0] = new Neurons.DenseRowMatrix(units[1], units[0]);
        else
          dense_col_weights_momenta[0] = new Neurons.DenseColMatrix(units[1], units[0]);
        for (int i=1; i<dense_row_weights_momenta.length; ++i) dense_row_weights_momenta[i] = new Neurons.DenseRowMatrix(units[i+1], units[i]);

        biases_momenta = new Neurons.DenseVector[biases.length];
        for (int i=0; i<biases_momenta.length; ++i) biases_momenta[i] = new Neurons.DenseVector(units[i+1]);
      }
      else if (adaDelta()) {
        dense_row_ada_dx_g = new Neurons.DenseRowMatrix[dense_row_weights.length];
        dense_col_ada_dx_g = new Neurons.DenseColMatrix[dense_col_weights.length];
        //AdaGrad
        if (dense_row_weights[0] != null) {
          dense_row_ada_dx_g[0] = new Neurons.DenseRowMatrix(units[1], 2*units[0]);
        } else {
          dense_col_ada_dx_g[0] = new Neurons.DenseColMatrix(2*units[1], units[0]);
        }
        for (int i=1; i<dense_row_ada_dx_g.length; ++i) {
          dense_row_ada_dx_g[i] = new Neurons.DenseRowMatrix(units[i+1], 2*units[i]);
        }
        biases_ada_dx_g = new Neurons.DenseVector[biases.length];
        for (int i=0; i<biases_ada_dx_g.length; ++i) {
          biases_ada_dx_g[i] = new Neurons.DenseVector(2*units[i+1]);
        }
      }
    }

    public TwoDimTable createSummaryTable() {
      Neurons[] neurons = DeepLearningTask.makeNeuronsForTesting(this);
      long byte_size = new AutoBuffer().put(this).buf().length;
      TwoDimTable table = new TwoDimTable(
              "Status of Neuron Layers",
                  (!get_params()._autoencoder ? ("predicting " + _train.lastVecName() + ", ") : "") +
                          (get_params()._autoencoder ? "auto-encoder" :
                                  _classification ? (units[units.length-1] + "-class classification") : "regression" )
                          + ", " + get_params()._loss.toString() + " loss, "
                          + String.format("%,d", size()) + " weights/biases, " + PrettyPrint.bytes(byte_size),
              new String[neurons.length],
              new String[]{"Layer", "Units", "Type", "Dropout", "L1", "L2",
                      "Mean Rate", "Rate RMS", "Momentum",
                      "Mean Weight", "Weight RMS",
                      "Mean Bias", "Bias RMS"
              },
              new String[]{"int", "int", "string", "double", "double", "double",
                           "double", "double", "double",
                      "double", "double",
                      "double", "double"
              },
              new String[]{"%d", "%d", "%s", "%2.2f %%", "%5f", "%5f", "%5f", "%5f", "%5f", "%5f", "%5f", "%5f", "%5f"},
              "");

      final String format = "%7g";
      for (int i = 0; i < neurons.length; ++i) {
        table.set(i, 0, i + 1);
        table.set(i, 1, neurons[i].units);
        table.set(i, 2, neurons[i].getClass().getSimpleName());

        if (i == 0) {
          table.set(i, 3, neurons[i].params._input_dropout_ratio*100);
          continue;
        } else if (i < neurons.length - 1) {
          if (neurons[i].params._hidden_dropout_ratios == null) {
            table.set(i, 3, 0);
          } else {
            table.set(i, 3, neurons[i].params._hidden_dropout_ratios[i - 1]*100);
          }
        }
        table.set(i, 4, neurons[i].params._l1);
        table.set(i, 5, neurons[i].params._l2);
        table.set(i, 6, (get_params()._adaptive_rate ? mean_rate[i] : neurons[i].rate(get_processed_total())));
        table.set(i, 7, (get_params()._adaptive_rate ? rms_rate[i] : 0));
        table.set(i, 8, get_params()._adaptive_rate ? 0 : neurons[i].momentum(get_processed_total()));
        table.set(i, 9, mean_weight[i]);
        table.set(i, 10, rms_weight[i]);
        table.set(i, 11, mean_bias[i]);
        table.set(i, 12, rms_bias[i]);
      }
      summaryTable = table;
      return summaryTable;
    }

    @Override public String toString() {
      StringBuilder sb = new StringBuilder();
      if (get_params()._diagnostics && !get_params()._quiet_mode) {
        if (get_params()._sparsity_beta > 0) {
          for (int k = 0; k < get_params()._hidden.length; k++) {
            sb.append("Average activation in hidden layer ").append(k).append(" is  ").append(mean_a[k]).append(" \n");
          }
        }
        createSummaryTable();
        sb.append(summaryTable.toString(1));
      }
      return sb.toString();
    }

    // DEBUGGING
    public String toStringAll() {
      StringBuilder sb = new StringBuilder();
      sb.append(toString());

      for (int i=0; i<units.length-1; ++i)
        sb.append("\nweights[").append(i).append("][]=").append(Arrays.toString(get_weights(i).raw()));
      for (int i=0; i<units.length-1; ++i) {
        sb.append("\nbiases[").append(i).append("][]=").append(Arrays.toString(get_biases(i).raw()));
      }
      if (has_momenta()) {
        for (int i=0; i<units.length-1; ++i)
          sb.append("\nweights_momenta[").append(i).append("][]=").append(Arrays.toString(get_weights_momenta(i).raw()));
      }
      if (biases_momenta != null) {
        for (int i=0; i<units.length-1; ++i) {
          sb.append("\nbiases_momenta[").append(i).append("][]=").append(Arrays.toString(biases_momenta[i].raw()));
        }
      }
      sb.append("\nunits[]=").append(Arrays.toString(units));
      sb.append("\nprocessed global: ").append(get_processed_global());
      sb.append("\nprocessed local:  ").append(get_processed_local());
      sb.append("\nprocessed total:  ").append(get_processed_total());
      sb.append("\n");
      return sb.toString();
    }

    void initializeMembers() {
      randomizeWeights();
      //TODO: determine good/optimal/best initialization scheme for biases
      // hidden layers
      for (int i=0; i<get_params()._hidden.length; ++i) {
        if (get_params()._activation == DeepLearningParameters.Activation.Rectifier
                || get_params()._activation == DeepLearningParameters.Activation.RectifierWithDropout
                || get_params()._activation == DeepLearningParameters.Activation.Maxout
                || get_params()._activation == DeepLearningParameters.Activation.MaxoutWithDropout
                ) {
//          Arrays.fill(biases[i], 1.); //old behavior
          Arrays.fill(biases[i].raw(), i == 0 ? 0.5f : 1f); //new behavior, might be slightly better
        }
        else if (get_params()._activation == DeepLearningParameters.Activation.Tanh || get_params()._activation == DeepLearningParameters.Activation.TanhWithDropout) {
          Arrays.fill(biases[i].raw(), 0f);
        }
      }
      Arrays.fill(biases[biases.length-1].raw(), 0f); //output layer
    }
    public void add(DeepLearningModelInfo other) {
      for (int i=0;i<dense_row_weights.length;++i)
        ArrayUtils.add(get_weights(i).raw(), other.get_weights(i).raw());
      for (int i=0;i<biases.length;++i) ArrayUtils.add(biases[i].raw(), other.biases[i].raw());
      if (avg_activations != null)
        for (int i=0;i<avg_activations.length;++i)
          ArrayUtils.add(avg_activations[i].raw(), other.biases[i].raw());
      if (has_momenta()) {
        assert(other.has_momenta());
        for (int i=0;i<dense_row_weights_momenta.length;++i)
          ArrayUtils.add(get_weights_momenta(i).raw(), other.get_weights_momenta(i).raw());
        for (int i=0;i<biases_momenta.length;++i)
          ArrayUtils.add(biases_momenta[i].raw(),  other.biases_momenta[i].raw());
      }
      if (adaDelta()) {
        assert(other.adaDelta());
        for (int i=0;i<dense_row_ada_dx_g.length;++i) {
          ArrayUtils.add(get_ada_dx_g(i).raw(), other.get_ada_dx_g(i).raw());
        }
      }
      add_processed_local(other.get_processed_local());
    }
    protected void div(float N) {
      for (int i=0; i<dense_row_weights.length; ++i)
        ArrayUtils.div(get_weights(i).raw(), N);
      for (Neurons.Vector bias : biases) ArrayUtils.div(bias.raw(), N);
      if (avg_activations != null)
        for (Neurons.Vector avgac : avg_activations)
          ArrayUtils.div(avgac.raw(), N);
      if (has_momenta()) {
        for (int i=0; i<dense_row_weights_momenta.length; ++i)
          ArrayUtils.div(get_weights_momenta(i).raw(), N);
        for (Neurons.Vector bias_momenta : biases_momenta) ArrayUtils.div(bias_momenta.raw(), N);
      }
      if (adaDelta()) {
        for (int i=0;i<dense_row_ada_dx_g.length;++i) {
          ArrayUtils.div(get_ada_dx_g(i).raw(), N);
        }
      }
    }
    double uniformDist(Random rand, double min, double max) {
      return min + rand.nextFloat() * (max - min);
    }
    void randomizeWeights() {
      for (int w=0; w<dense_row_weights.length; ++w) {
        final Random rng = water.util.RandomUtils.getRNG(get_params()._seed + 0xBAD5EED + w+1); //to match NeuralNet behavior
        final double range = Math.sqrt(6. / (units[w] + units[w+1]));
        for( int i = 0; i < get_weights(w).rows(); i++ ) {
          for( int j = 0; j < get_weights(w).cols(); j++ ) {
            if (get_params()._initial_weight_distribution == DeepLearningParameters.InitialWeightDistribution.UniformAdaptive) {
              // cf. http://machinelearning.wustl.edu/mlpapers/paper_files/AISTATS2010_GlorotB10.pdf
              if (w==dense_row_weights.length-1 && _classification)
                get_weights(w).set(i,j, (float)(4.*uniformDist(rng, -range, range))); //Softmax might need an extra factor 4, since it's like a sigmoid
              else
                get_weights(w).set(i,j, (float)uniformDist(rng, -range, range));
            }
            else if (get_params()._initial_weight_distribution == DeepLearningParameters.InitialWeightDistribution.Uniform) {
              get_weights(w).set(i,j, (float)uniformDist(rng, -get_params()._initial_weight_scale, get_params()._initial_weight_scale));
            }
            else if (get_params()._initial_weight_distribution == DeepLearningParameters.InitialWeightDistribution.Normal) {
              get_weights(w).set(i,j, (float)(rng.nextGaussian() * get_params()._initial_weight_scale));
            }
          }
        }
      }
    }

    // TODO: Add "subset randomize" function
//        int count = Math.min(15, _previous.units);
//        double min = -.1f, max = +.1f;
//        //double min = -1f, max = +1f;
//        for( int o = 0; o < units; o++ ) {
//          for( int n = 0; n < count; n++ ) {
//            int i = rand.nextInt(_previous.units);
//            int w = o * _previous.units + i;
//            _w[w] = uniformDist(rand, min, max);
//          }
//        }

    /**
     * Compute Variable Importance, based on
     * GEDEON: DATA MINING OF INPUTS: ANALYSING MAGNITUDE AND FUNCTIONAL MEASURES
     * @return variable importances for input features
     */
    public float[] computeVariableImportances() {
      float[] vi = new float[units[0]];
      Arrays.fill(vi, 0f);

      float[][] Qik = new float[units[0]][units[2]]; //importance of input i on output k
      float[] sum_wj = new float[units[1]]; //sum of incoming weights into first hidden layer
      float[] sum_wk = new float[units[2]]; //sum of incoming weights into output layer (or second hidden layer)
      for (float[] Qi : Qik) Arrays.fill(Qi, 0f);
      Arrays.fill(sum_wj, 0f);
      Arrays.fill(sum_wk, 0f);

      // compute sum of absolute incoming weights
      for( int j = 0; j < units[1]; j++ ) {
        for( int i = 0; i < units[0]; i++ ) {
          float wij = get_weights(0).get(j, i);
          sum_wj[j] += Math.abs(wij);
        }
      }
      for( int k = 0; k < units[2]; k++ ) {
        for( int j = 0; j < units[1]; j++ ) {
          float wjk = get_weights(1).get(k,j);
          sum_wk[k] += Math.abs(wjk);
        }
      }
      // compute importance of input i on output k as product of connecting weights going through j
      for( int i = 0; i < units[0]; i++ ) {
        for( int k = 0; k < units[2]; k++ ) {
          for( int j = 0; j < units[1]; j++ ) {
            float wij = get_weights(0).get(j,i);
            float wjk = get_weights(1).get(k,j);
            //Qik[i][k] += Math.abs(wij)/sum_wj[j] * wjk; //Wong,Gedeon,Taggart '95
            Qik[i][k] += Math.abs(wij)/sum_wj[j] * Math.abs(wjk)/sum_wk[k]; //Gedeon '97
          }
        }
      }
      // normalize Qik over all outputs k
      for( int k = 0; k < units[2]; k++ ) {
        float sumQk = 0;
        for( int i = 0; i < units[0]; i++ ) sumQk += Qik[i][k];
        for( int i = 0; i < units[0]; i++ ) Qik[i][k] /= sumQk;
      }
      // importance for feature i is the sum over k of i->k importances
      for( int i = 0; i < units[0]; i++ ) vi[i] = ArrayUtils.sum(Qik[i]);

      //normalize importances such that max(vi) = 1
      ArrayUtils.div(vi, ArrayUtils.maxValue(vi));
      return vi;
    }

    // compute stats on all nodes
    public void computeStats() {
      float[][] rate = get_params()._adaptive_rate ? new float[units.length-1][] : null;

      if (get_params()._autoencoder && get_params()._sparsity_beta > 0) {
        for (int k = 0; k < get_params()._hidden.length; k++) {
          mean_a[k] = 0;
          for (int j = 0; j < avg_activations[k].size(); j++)
            mean_a[k] += avg_activations[k].get(j);
          mean_a[k] /= avg_activations[k].size();
        }
      }

      for( int y = 1; y < units.length; y++ ) {
        mean_rate[y] = rms_rate[y] = 0;
        mean_bias[y] = rms_bias[y] = 0;
        mean_weight[y] = rms_weight[y] = 0;
        for(int u = 0; u < biases[y-1].size(); u++) {
          mean_bias[y] += biases[y-1].get(u);
        }
        if (rate != null) rate[y-1] = new float[get_weights(y-1).raw().length];
        for(int u = 0; u < get_weights(y-1).raw().length; u++) {
          mean_weight[y] += get_weights(y-1).raw()[u];
          if (rate != null) {
//            final float RMS_dx = (float)Math.sqrt(ada[y-1][2*u]+(float)get_params().epsilon);
//            final float invRMS_g = (float)(1/Math.sqrt(ada[y-1][2*u+1]+(float)get_params().epsilon));
            final float RMS_dx = MathUtils.approxSqrt(get_ada_dx_g(y-1).raw()[2*u]+(float)get_params()._epsilon);
            final float invRMS_g = MathUtils.approxInvSqrt(get_ada_dx_g(y-1).raw()[2*u+1]+(float)get_params()._epsilon);
            rate[y-1][u] = RMS_dx*invRMS_g; //not exactly right, RMS_dx should be from the previous time step -> but close enough for diagnostics.
            mean_rate[y] += rate[y-1][u];
          }
        }


        mean_bias[y] /= biases[y-1].size();

        mean_weight[y] /= get_weights(y-1).size();
        if (rate != null) mean_rate[y] /= rate[y-1].length;

        for(int u = 0; u < biases[y-1].size(); u++) {
          final double db = biases[y-1].get(u) - mean_bias[y];
          rms_bias[y] += db * db;
        }
        for(int u = 0; u < get_weights(y-1).size(); u++) {
          final double dw = get_weights(y-1).raw()[u] - mean_weight[y];
          rms_weight[y] += dw * dw;
          if (rate != null) {
            final double drate = rate[y-1][u] - mean_rate[y];
            rms_rate[y] += drate * drate;
          }
        }
        rms_bias[y] = MathUtils.approxSqrt(rms_bias[y]/biases[y-1].size());
        rms_weight[y] = MathUtils.approxSqrt(rms_weight[y] / get_weights(y - 1).size());
        if (rate != null) rms_rate[y] = MathUtils.approxSqrt(rms_rate[y]/rate[y-1].length);
//        rms_bias[y] = (float)Math.sqrt(rms_bias[y]/biases[y-1].length);
//        rms_weight[y] = (float)Math.sqrt(rms_weight[y]/weights[y-1].length);
//        if (rate != null) rms_rate[y] = (float)Math.sqrt(rms_rate[y]/rate[y-1].length);

        // Abort the run if weights or biases are unreasonably large (Note that all input values are normalized upfront)
        // This can happen with Rectifier units when L1/L2/max_w2 are all set to 0, especially when using more than 1 hidden layer.
        final double thresh = 1e10;
        unstable |= mean_bias[y] > thresh  || isNaN(mean_bias[y])
                || rms_bias[y] > thresh    || isNaN(rms_bias[y])
                || mean_weight[y] > thresh || isNaN(mean_weight[y])
                || rms_weight[y] > thresh  || isNaN(rms_weight[y]);
      }
    }

    // unique identifier for this model's state
    protected long checksum_impl() {
      long cs = parameters._seed;
      cs ^= size() * get_processed_total();
      cs ^= (long)(2234.3424*ArrayUtils.sum(mean_bias));
      cs *= (long)(9234.1343*ArrayUtils.sum(rms_bias));
      cs ^= (long)(9723.9734*ArrayUtils.sum(mean_weight));
      cs *= (long)(9234.1783*ArrayUtils.sum(rms_weight));
      cs ^= (long)(4273.2344*ArrayUtils.sum(mean_rate));
      cs *= (long)(3378.1999*ArrayUtils.sum(rms_rate));
      return cs;
    }
  }

  /**
   * Helper to allocate keys for output frames for weights and biases
   * @param destKey
   */
  private void makeWeightsBiases(Key destKey) {
    if (!model_info.get_params()._export_weights_and_biases) {
      _output.weights = null;
      _output.biases = null;
    } else {
      _output.weights = new Key[model_info.get_params()._hidden.length + 1];
      for (int i = 0; i < _output.weights.length; ++i) {
        _output.weights[i] = Key.makeUserHidden(Key.make(destKey + ".weights." + i));
      }
      _output.biases = new Key[model_info.get_params()._hidden.length + 1];
      for (int i = 0; i < _output.biases.length; ++i) {
        _output.biases[i] = Key.makeUserHidden(Key.make(destKey + ".biases." + i));
      }
    }
  }

  /** Constructor to restart from a checkpointed model
   * @param destKey New destination key for the model
   *  @param parms User-given parameters for checkpoint restart
   *  @param cp Checkpoint to restart from
   * @param store_best_model Store only the best model instead of the latest one  */
  public DeepLearningModel(final Key destKey, final DeepLearningParameters parms, final DeepLearningModel cp, final boolean store_best_model, final DataInfo dataInfo) {
    super(destKey, parms == null ? (DeepLearningParameters)cp._parms.clone() : parms, (DeepLearningModelOutput)cp._output.clone());
    assert(_parms != cp._parms); //make sure we have a clone
    model_info = cp.model_info.deep_clone(); //don't want to interfere with model being built, just make a deep copy and store that
    if (store_best_model) {
      model_info.data_info = dataInfo.deep_clone(); //replace previous data_info with updated version that's passed in (contains enum for classification)
    } else {
      model_info.data_info = dataInfo; //shallow clone is ok
      if (parms != null) {
        assert (_parms == parms);
        assert (_parms._checkpoint == parms._checkpoint);
        assert (_parms._checkpoint == cp._key);
      }
//      _parms._checkpoint = cp._key; //it's only a "real" checkpoint if job != null, otherwise a best model copy
    }
    assert(model_info().get_params() != cp.model_info().get_params()); //make sure we have a clone
    actual_best_model_key = cp.actual_best_model_key;
    start_time = cp.start_time;
    run_time = cp.run_time;
    training_rows = cp.training_rows; //copy the value to display the right number on the model page before training has started
    validation_rows = cp.validation_rows; //copy the value to display the right number on the model page before training has started
    _bestError = cp._bestError;

    // deep clone scoring history
    errors = cp.errors.clone();
    for (int i=0; i<errors.length;++i)
      errors[i] = cp.errors[i].deep_clone();
    _output.errors = last_scored();
    makeWeightsBiases(destKey);
    _output._scoring_history = createScoringHistoryTable(errors);
    _output._variable_importances = calcVarImp(last_scored().variable_importances);
    _output._names = dataInfo._adaptedFrame.names();
    _output._domains = dataInfo._adaptedFrame.domains();

    // set proper timing
    _timeLastScoreEnter = System.currentTimeMillis();
    _timeLastScoreStart = 0;
    _timeLastScoreEnd = 0;
    _timeLastPrintStart = 0;
    assert(Arrays.equals(_key._kb, destKey._kb));
  }

  public DeepLearningModel(final Key destKey, final DeepLearningParameters parms, final DeepLearningModelOutput output, Frame train, Frame valid) {
    super(destKey, parms, output);
    boolean classification = train.lastVec().isEnum();
    final DataInfo dinfo = makeDataInfo(train, valid, _parms);
    _output._names  = train._names   ; // Since changed by DataInfo, need to be reflected in the Model output as well
    _output._domains= train.domains();
    _output._names = dinfo._adaptedFrame.names();
    _output._domains = dinfo._adaptedFrame.domains();
    DKV.put(dinfo._key,dinfo);
    model_info = new DeepLearningModelInfo(parms, dinfo, classification, train, valid);
    actual_best_model_key = Key.makeUserHidden(Key.make());
    if (parms.getNumFolds() != 0) actual_best_model_key = null;
    if (!parms._autoencoder) {
      errors = new DeepLearningScoring[1];
      errors[0] = new DeepLearningScoring();
      errors[0].validation = (parms._valid != null);
      errors[0].num_folds = parms.getNumFolds();
      _output.errors = last_scored();
      _output._scoring_history = createScoringHistoryTable(errors);
      _output._variable_importances = calcVarImp(last_scored().variable_importances);
    }
    makeWeightsBiases(destKey);
    run_time = 0;
    start_time = System.currentTimeMillis();
    _timeLastScoreEnter = start_time;
    assert _key.equals(destKey);
    boolean fail = false;
    long byte_size = 0;
    try {
      byte_size = new AutoBuffer().put(this).buf().length;
    } catch(Throwable t) {
      fail = true;
    }
    if (byte_size > Value.MAX || fail)
      throw new IllegalArgumentException("Model is too large: PUBDEV-941");
  }

  /**
   * Take user-given parameters and turn them into usable, fully populated parameters (e.g., to be used by Neurons during training)
   * @param fromParms raw user-given parameters from the REST API
   * @param toParms modified set of parameters, with defaults filled in
   * @param classification
   */
  public static void modifyParms(DeepLearningParameters fromParms, DeepLearningParameters toParms, boolean classification) {
    if (fromParms._hidden_dropout_ratios == null) {
      if (fromParms._activation == DeepLearningParameters.Activation.TanhWithDropout
              || fromParms._activation == DeepLearningParameters.Activation.MaxoutWithDropout
              || fromParms._activation == DeepLearningParameters.Activation.RectifierWithDropout) {
        toParms._hidden_dropout_ratios = new double[fromParms._hidden.length];
        if (!fromParms._quiet_mode)
          Log.info("_hidden_dropout_ratios: Automatically setting all hidden dropout ratios to 0.5.");
        Arrays.fill(toParms._hidden_dropout_ratios, 0.5);
      }
    } else {
      toParms._hidden_dropout_ratios = fromParms._hidden_dropout_ratios.clone();
    }
    if (H2O.CLOUD.size() == 1 && fromParms._replicate_training_data) {
      Log.info("_replicate_training_data: Disabling replicate_training_data on 1 node.");
      toParms._replicate_training_data = false;
    }
    if (fromParms._single_node_mode && (H2O.CLOUD.size() == 1 || !fromParms._replicate_training_data)) {
      Log.info("_single_node_mode: Disabling single_node_mode (only for multi-node operation with replicated training data).");
      toParms._single_node_mode = false;
    }
    if (!fromParms._use_all_factor_levels && fromParms._autoencoder ) {
      Log.info("_use_all_factor_levels: Automatically enabling all_factor_levels for auto-encoders.");
      toParms._use_all_factor_levels = true;
    }
    if(fromParms._override_with_best_model && fromParms.getNumFolds() != 0) {
      Log.info("_override_with_best_model: Disabling override_with_best_model in combination with n-fold cross-validation.");
      toParms._override_with_best_model = false;
    }
    if (fromParms._adaptive_rate) {
      Log.info("_adaptive_rate: Using automatic learning rate. Ignoring the following input parameters: "
              + "rate, rate_decay, rate_annealing, momentum_start, momentum_ramp, momentum_stable, nesterov_accelerated_gradient.");
      toParms._rate = 0;
      toParms._rate_decay = 0;
      toParms._rate_annealing = 0;
      toParms._momentum_start = 0;
      toParms._momentum_ramp = 0;
      toParms._momentum_stable = 0;
      toParms._nesterov_accelerated_gradient = false;
    } else {
      Log.info("_adaptive_rate: Using manual learning rate. Ignoring the following input parameters: "
              + "rho, epsilon.");
      toParms._rho = 0;
      toParms._epsilon = 0;
    }
    if (fromParms.getNumFolds() != 0) {
      if (fromParms._override_with_best_model) {
        Log.info("_override_with_best_model: Automatically disabling override_with_best_model, since the final model is the only scored model with n-fold cross-validation.");
        toParms._override_with_best_model = false;
      }
    }
    if (fromParms._loss == DeepLearningParameters.Loss.Automatic) {
        toParms._loss = (classification && !fromParms._autoencoder) ? DeepLearningParameters.Loss.CrossEntropy : DeepLearningParameters.Loss.MeanSquare;
        Log.info("_loss: Automatically setting loss function to " + toParms._loss);
    }
    if (fromParms._reproducible) {
      Log.info("_reproducibility: Automatically enabling force_load_balancing, disabling single_node_mode and replicate_training_data\n"
                      +"and setting train_samples_per_iteration to -1 to enforce reproducibility.");
      toParms._force_load_balance = true;
      toParms._single_node_mode = false;
      toParms._train_samples_per_iteration = -1;
      toParms._replicate_training_data = false; //there's no benefit from having multiple nodes compute the exact same thing, and then average it back to the same
      //      replicate_training_data = true; //doesn't hurt, but does replicated identical work
    }
  }

  public long _timeLastScoreEnter; //not transient: needed for HTML display page
  transient private long _timeLastScoreStart;
  transient private long _timeLastScoreEnd;
  transient private long _timeLastPrintStart;
  /**
   *
   * @param ftrain potentially downsampled training data for scoring
   * @param ftest  potentially downsampled validation data for scoring
   * @param job_key key of the owning job
   * @param progressKey key of the progress
   * @return true if model building is ongoing
   */
  boolean doScoring(Frame ftrain, Frame ftest, Key job_key, Key progressKey) {
    boolean keep_running;
    try {
      final long now = System.currentTimeMillis();
      epoch_counter = (float)model_info().get_processed_total()/training_rows;
      final double time_last_iter_millis = Math.max(5,now-_timeLastScoreEnter);

      // Auto-tuning
      // if multi-node and auto-tuning and at least 10 ms for communication (to avoid doing thins on multi-JVM on same node),
      // then adjust the auto-tuning parameter 'actual_train_samples_per_iteration' such that the targeted ratio of comm to comp is achieved
      // Note: actual communication time is estimated by the NetworkTest's collective test.
      if (H2O.CLOUD.size() > 1 && get_params()._train_samples_per_iteration == -2 && time_for_communication_us > 1e4) {
//        Log.info("Time taken for communication: " + PrettyPrint.usecs((long)time_for_communication_us));
//        Log.info("Time taken for Map/Reduce iteration: " + PrettyPrint.msecs((long)time_last_iter_millis, true));
        final double comm_to_work_ratio = (time_for_communication_us *1e-3) / time_last_iter_millis;
//        Log.info("Ratio of network communication to computation: " + String.format("%.3f", comm_to_work_ratio));
//        Log.info("target_comm_to_work: " + get_params().target_ratio_comm_to_comp);
        final double correction = get_params()._target_ratio_comm_to_comp / comm_to_work_ratio;
//        Log.warn("Suggested value for train_samples_per_iteration: " + get_params().actual_train_samples_per_iteration/correction);
        actual_train_samples_per_iteration /= correction;
        actual_train_samples_per_iteration = Math.max(1, actual_train_samples_per_iteration);
      }

      run_time += time_last_iter_millis;
      _timeLastScoreEnter = now;
      keep_running = (epoch_counter < model_info().get_params()._epochs);
      final long sinceLastScore = now -_timeLastScoreStart;
      final long sinceLastPrint = now -_timeLastPrintStart;
      if (!keep_running || sinceLastPrint > get_params()._score_interval * 1000) { //print this after every score_interval, not considering duty cycle
        _timeLastPrintStart = now;
        if (!get_params()._quiet_mode) {
          Log.info("Training time: " + PrettyPrint.msecs(run_time, true)
                  + ". Processed " + String.format("%,d", model_info().get_processed_total()) + " samples" + " (" + String.format("%.3f", epoch_counter) + " epochs)."
                  + " Speed: " + String.format("%.3f", 1000. * model_info().get_processed_total() / run_time) + " samples/sec.\n");
        }
      }

      // this is potentially slow - only do every so often
      if( !keep_running ||
              (sinceLastScore > get_params()._score_interval *1000 //don't score too often
                      &&(double)(_timeLastScoreEnd-_timeLastScoreStart)/sinceLastScore < get_params()._score_duty_cycle) ) { //duty cycle
        if (progressKey != null) {
          new Job.ProgressUpdate("Scoring on " + ftrain.numRows() + " training samples" +
                  (ftest != null ? (", " + ftest.numRows() + " validation samples)") : ")")
          ).fork(progressKey);
        }
        final boolean printme = !get_params()._quiet_mode;
        _timeLastScoreStart = now;
        if (get_params()._diagnostics) model_info().computeStats();
        DeepLearningScoring err = new DeepLearningScoring();
        err.training_time_ms = run_time;
        err.epoch_counter = epoch_counter;
        err.training_samples = model_info().get_processed_total();
        err.validation = ftest != null;
        err.score_training_samples = ftrain.numRows();
        err.classification = _output.isClassifier();

        if (get_params()._autoencoder) {
          if (printme) Log.info("Scoring the auto-encoder.");
          // training
          {
            final Frame mse_frame = scoreAutoEncoder(ftrain, Key.make());
            final Vec l2 = mse_frame.anyVec();
            Log.info("Mean reconstruction error on training data: " + l2.mean() + "\n");
            err.training_MSE = l2.mean();
            mse_frame.delete();

            hex.ModelMetricsAutoEncoder mm1 = (ModelMetricsAutoEncoder)ModelMetrics.getFromDKV(this,ftrain);
            err.training_MSE = err.train_err = mm1._MSE;
            _output._training_metrics = mm1;
          }
          if (ftest != null) {
            final Frame mse_frame = scoreAutoEncoder(ftest, Key.make());
            final Vec l2 = mse_frame.anyVec();
            Log.info("Mean reconstruction error on validation data: " + l2.mean() + "\n");
            err.validation_MSE = l2.mean();
            mse_frame.delete();

            hex.ModelMetricsAutoEncoder mm1 = (ModelMetricsAutoEncoder)ModelMetrics.getFromDKV(this,ftest);
            err.validation_MSE = err.valid_err = mm1._MSE;
            _output._validation_metrics = mm1;
          }
        } else {
          if (printme) Log.info("Scoring the model.");
          // compute errors
          final String m = model_info().toString();
          if (m.length() > 0) Log.info(m);
          final Frame trainPredict = score(ftrain, ftrain.vecs()[ftrain.vecs().length-2]);
          trainPredict.delete();

          hex.ModelMetricsSupervised mm1 = (ModelMetricsSupervised)ModelMetrics.getFromDKV(this,ftrain);
          if (mm1 instanceof ModelMetricsBinomial) {
            ModelMetricsBinomial mm = (ModelMetricsBinomial)(mm1);
            err.training_AUC = mm._auc;
            err.train_confusion_matrix = mm.cm();
            err.train_err = err.train_confusion_matrix.err();
            err.train_logloss = mm._logloss;
          }
          else if (mm1 instanceof ModelMetricsMultinomial) {
            ModelMetricsMultinomial mm = (ModelMetricsMultinomial)(mm1);
            err.train_confusion_matrix = mm.cm();
            err.train_err = err.train_confusion_matrix.err();
            err.train_logloss = mm._logloss;
            err.train_hitratio = mm._hit_ratios;
          }
          err.training_MSE = mm1._MSE;
          err.training_R2 = mm1.r2();
          _output._training_metrics = mm1;
          if (get_params()._score_training_samples != 0 && get_params()._score_training_samples < ftrain.numRows()) {
            _output._training_metrics._description = "Metrics reported on " + ftrain.numRows() + " training set samples";
          }

          if (ftest != null) {
            Frame validPred = score(ftest, ftest.vecs()[ftest.vecs().length-2]);
            validPred.delete();
            hex.ModelMetricsSupervised mm2 = (ModelMetricsSupervised)hex.ModelMetrics.getFromDKV(this, ftest);
            if (mm2 != null) {
              if (mm2 instanceof ModelMetricsBinomial) {
                ModelMetricsBinomial mm = (ModelMetricsBinomial) (mm2);
                err.validation_AUC = mm._auc;
                err.valid_confusion_matrix = mm.cm();
                err.valid_logloss = mm._logloss;
                err.valid_err = err.valid_confusion_matrix.err();
              } else if (mm2 instanceof ModelMetricsMultinomial) {
                ModelMetricsMultinomial mm = (ModelMetricsMultinomial) (mm2);
                err.valid_confusion_matrix = mm.cm();
                err.valid_err = err.valid_confusion_matrix.err();
                err.valid_logloss = mm._logloss;
                err.valid_hitratio = mm._hit_ratios;
              }
              err.validation_MSE = mm2._MSE;
              err.validation_R2 = mm2.r2();
              _output._validation_metrics = mm2;
              if (get_params()._score_validation_samples != 0 && get_params()._score_validation_samples != ftest.numRows()) {
                _output._validation_metrics._description = "Metrics reported on " + ftest.numRows() + " validation set samples";
                if (get_params()._score_validation_sampling == DeepLearningParameters.ClassSamplingMethod.Stratified) {
                  _output._validation_metrics._description += " (stratified sampling)";
                }
              }
            }
          }
        }
        if (get_params()._variable_importances) {
          if (!get_params()._quiet_mode) Log.info("Computing variable importances.");
          final float[] vi = model_info().computeVariableImportances();
          err.variable_importances = new VarImp(vi, Arrays.copyOfRange(model_info().data_info().coefNames(), 0, vi.length));
        }

        _timeLastScoreEnd = System.currentTimeMillis();
        err.scoring_time = System.currentTimeMillis() - now;
        // enlarge the error array by one, push latest score back
        if (errors == null) {
          errors = new DeepLearningScoring[]{err};
        } else {
          DeepLearningScoring[] err2 = new DeepLearningScoring[errors.length + 1];
          System.arraycopy(errors, 0, err2, 0, errors.length);
          err2[err2.length - 1] = err;
          errors = err2;
        }
        _output.errors = last_scored();
        water.util.Timer t = new Timer();
        // store weights and matrices to Frames
        if (_output.weights != null && _output.biases != null) {
          for (int i = 0; i < _output.weights.length; ++i) {
            model_info.get_weights(i).toFrame(_output.weights[i]);
          }
          for (int i = 0; i < _output.biases.length; ++i) {
            model_info.get_biases(i).toFrame(_output.biases[i]);
          }
          Log.info("Writing weights and biases to Frames took " + t.time()/1000. + " seconds.");
        }
        _output._scoring_history = createScoringHistoryTable(errors);
        _output._variable_importances = calcVarImp(last_scored().variable_importances);
        _output._model_summary = model_info.createSummaryTable();

        if (!get_params()._autoencoder) {
          // always keep a copy of the best model so far (based on the following criterion)
          if (actual_best_model_key != null && get_params()._override_with_best_model && (
                  // if we have a best_model in DKV, then compare against its error() (unless it's a different model as judged by the network size)
                  (DKV.get(actual_best_model_key) != null && (error() < DKV.get(actual_best_model_key).<DeepLearningModel>get().error() || !Arrays.equals(model_info().units, DKV.get(actual_best_model_key).<DeepLearningModel>get().model_info().units)))
                          ||
                          // otherwise, compare against our own _bestError
                          (DKV.get(actual_best_model_key) == null && error() < _bestError)
          ) ) {
            if (!get_params()._quiet_mode)
              Log.info("Error reduced from " + _bestError + " to " + error() + ".");
            _bestError = error();
            putMeAsBestModel(actual_best_model_key);

            // debugging check
            //if (false) {
            //  DeepLearningModel bestModel = DKV.get(actual_best_model_key).get();
            //  final Frame fr = ftest != null ? ftest : ftrain;
            //  final Frame bestPredict = bestModel.score(fr);
            //  final Frame hitRatio_bestPredict = new Frame(bestPredict);
            //  final double err3 = calcError(fr, fr.lastVec(), bestPredict, hitRatio_bestPredict, "cross-check",
            //    printme, get_params()._max_confusion_matrix_size, new hex.ConfusionMatrix2(), _output.isClassifier() && _output.nclasses() == 2 ? new AUC(null,null) : null, null);
            //  if (_output.isClassifier())
            //    assert (ftest != null ? Math.abs(err.valid_err - err3) < 1e-5 : Math.abs(err.train_err - err3) < 1e-5);
            //  else
            //    assert (ftest != null ? Math.abs(err.validation_MSE - err3) < 1e-5 : Math.abs(err.training_MSE - err3) < 1e-5);
            //  bestPredict.delete();
            //}
          }
//        else {
//          // keep output JSON small
//          if (errors.length > 1) {
//            if (last_scored().training_AUC != null) last_scored().training_AUC.clear();
//            if (last_scored().validation_AUC != null) last_scored().validation_AUC.clear();
//            last_scored()._variable_importances = null;
//          }
//        }

          // print the freshly scored model to ASCII
          if (keep_running)
            for (String s : toString().split("\n")) Log.info(s);
          if (printme) Log.info("Time taken for scoring and diagnostics: " + PrettyPrint.msecs(err.scoring_time, true));
        }
      }
      if (model_info().unstable()) {
        Log.warn(unstable_msg);
        keep_running = false;
      } else if ( (_output.isClassifier() && last_scored().train_err <= get_params()._classification_stop)
              || (!_output.isClassifier() && last_scored().training_MSE <= get_params()._regression_stop) ) {
        Log.info("Achieved requested predictive accuracy on the training data. Model building completed.");
        keep_running = false;
      }
      update(job_key);
    }
    catch (Exception ex) {
      //ex.printStackTrace();
      throw new RuntimeException(ex);
//      return false;
    }
    return keep_running;
 }

  @Override public String toString() {
    return _output.toString();
  }

  /** Make either a prediction or a reconstruction.
   * @param orig Test dataset
   * @param adaptedFr Test dataset, adapted to the model
   * @return A frame containing the prediction or reconstruction
   */
  @Override protected Frame scoreImpl(Frame orig, Frame adaptedFr, Vec weights, String destination_key) {
    if (!get_params()._autoencoder) {
      return super.scoreImpl(orig,adaptedFr,weights,destination_key);
    } else {
      // Reconstruction
      final int len = model_info().data_info().fullN();
      String prefix = "reconstr_";
      assert(model_info().data_info()._responses == 0);
      String[] coefnames = model_info().data_info().coefNames();
      assert(len == coefnames.length);
      Frame adaptFrm = new Frame(adaptedFr);
      for( int c=0; c<len; c++ )
        adaptFrm.add(prefix+coefnames[c],adaptFrm.anyVec().makeZero());
      new MRTask() {
        @Override public void map( Chunk chks[] ) {
          double tmp [] = new double[_output._names.length];
          float preds[] = new float [len];
          final Neurons[] neurons = DeepLearningTask.makeNeuronsForTesting(model_info);
          for( int row=0; row<chks[0]._len; row++ ) {
            float p[] = score_autoencoder(chks, row, tmp, preds, neurons);
            for( int c=0; c<preds.length; c++ )
              chks[_output._names.length+c].set(row,p[c]);
          }
        }
      }.doAll(adaptFrm);

      // Return the predicted columns
      int x=_output._names.length, y=adaptFrm.numCols();
      Frame f = adaptFrm.extractFrame(x, y); //this will call vec_impl() and we cannot call the delete() below just yet

      f = new Frame((null == destination_key ? Key.make() : Key.make(destination_key)), f.names(), f.vecs());
      DKV.put(f);
      makeMetricBuilder(null).makeModelMetrics(this, orig, Double.NaN);
      return f;
    }
  }

  /**
   * Predict from raw double values representing the data
   * @param data raw array containing categorical values (horizontalized to 1,0,0,1,0,0 etc.) and numerical values (0.35,1.24,5.3234,etc), both can contain NaNs
   * @param preds predicted label and per-class probabilities (for classification), predicted target (regression), can contain NaNs
   * @return preds, can contain NaNs
   */
  @Override public double[] score0(double[] data, double[] preds) {
    if (model_info().unstable()) {
      Log.warn(unstable_msg);
      throw new UnsupportedOperationException("Trying to predict with an unstable model.");
    }
    Neurons[] neurons = DeepLearningTask.makeNeuronsForTesting(model_info);
    ((Neurons.Input)neurons[0]).setInput(-1, data);
    DeepLearningTask.step(-1, neurons, model_info, false, null);
    float[] out = neurons[neurons.length - 1]._a.raw();
    if (_output.isClassifier()) {
      assert (preds.length == out.length + 1);
      for (int i = 0; i < preds.length - 1; ++i) {
        preds[i + 1] = out[i];
        if (Double.isNaN(preds[i + 1])) throw new RuntimeException("Predicted class probability NaN!");
      }
      preds[0] = hex.genmodel.GenModel.getPrediction(preds, data);
    } else {
      if (model_info().data_info()._normRespMul != null)
        preds[0] = ((double)out[0] / model_info().data_info()._normRespMul[0] + model_info().data_info()._normRespSub[0]);
      else
        preds[0] = (double)out[0];
      if (Double.isNaN(preds[0])) throw new RuntimeException("Predicted regression target NaN!");
    }
    return preds;
  }

  /**
   * Score auto-encoded reconstruction (on-the-fly, without allocating the reconstruction as done in Frame score(Frame fr))
   * @param frame Original data (can contain response, will be ignored)
   * @return Frame containing one Vec with reconstruction error (MSE) of each reconstructed row, caller is responsible for deletion
   */
  public Frame scoreAutoEncoder(Frame frame, Key destination_key) {
    if (!get_params()._autoencoder)
      throw new H2OIllegalArgumentException("Only for AutoEncoder Deep Learning model.", "");
    final int len = _output._names.length;
    Frame adaptFrm = new Frame(frame);
    Vec v0 = adaptFrm.anyVec().makeZero();
    Scope.enter();
    adaptTestForTrain(adaptFrm,true);
    adaptFrm.add("Reconstruction.MSE", v0);
    new MRTask() {
      @Override public void map( Chunk chks[] ) {
        double tmp [] = new double[len];
        final Neurons[] neurons = DeepLearningTask.makeNeuronsForTesting(model_info);
        for( int row=0; row<chks[0]._len; row++ ) {
          for( int i=0; i<len; i++ )
            tmp[i] = chks[i].atd(row);
          //store the per-row reconstruction error (MSE) in the last column
          chks[len].set(row, score_autoencoder(tmp, null, neurons));
        }
      }
    }.doAll(adaptFrm);
    Scope.exit();

    Frame res = adaptFrm.extractFrame(len, adaptFrm.numCols());
    res = new Frame(destination_key, res.names(), res.vecs());
    DKV.put(res);
    makeMetricBuilder(null).makeModelMetrics(this, frame, res.vecs()[0].mean());
    return res;
  }

  @Override public Frame score(Frame fr, Vec weights, String destination_key) {
    if (!_parms._autoencoder)
      return super.score(fr, weights, destination_key);
    else {
      Frame adaptFr = new Frame(fr);
      adaptTestForTrain(adaptFr, true);   // Adapt
      Frame output = scoreImpl(fr, adaptFr, weights, destination_key); // Score

      Vec[] vecs = adaptFr.vecs();
      for (int i = 0; i < vecs.length; i++)
        if (fr.find(vecs[i]) != -1) // Exists in the original frame?
          vecs[i] = null;            // Do not delete it
      adaptFr.delete();
      return output;
    }
  }

   /**
   * Score auto-encoded reconstruction (on-the-fly, and materialize the deep features of given layer
   * @param frame Original data (can contain response, will be ignored)
   * @param layer index of the hidden layer for which to extract the features
   * @return Frame containing the deep features (#cols = hidden[layer])
   */
  public Frame scoreDeepFeatures(Frame frame, final int layer) {
    if (layer < 0 || layer >= model_info().get_params()._hidden.length)
      throw new H2OIllegalArgumentException("hidden layer (index) to extract must be between " + 0 + " and " + (model_info().get_params()._hidden.length-1),"");
    final int len = _output.nfeatures();
    Vec resp = null;
    if (isSupervised()) {
      int ridx = frame.find(_output.responseName());
      if (ridx != -1) { // drop the response for scoring!
        frame = new Frame(frame);
        resp = frame.vecs()[ridx];
        frame.remove(ridx);
      }
    }
    Frame adaptFrm = new Frame(frame);
    //create new features, will be dense
    final int features = model_info().get_params()._hidden[layer];
    Vec[] vecs = adaptFrm.anyVec().makeZeros(features);

    Scope.enter();
    adaptTestForTrain(adaptFrm,true);
    for (int j=0; j<features; ++j) {
      adaptFrm.add("DF.L"+(layer+1)+".C" + (j+1), vecs[j]);
    }
    new MRTask() {
      @Override public void map( Chunk chks[] ) {
        double tmp [] = new double[len];
        final Neurons[] neurons = DeepLearningTask.makeNeuronsForTesting(model_info);
        for( int row=0; row<chks[0]._len; row++ ) {
          for( int i=0; i<len; i++ )
            tmp[i] = chks[i].atd(row);
          ((Neurons.Input)neurons[0]).setInput(-1, tmp);
          DeepLearningTask.step(-1, neurons, model_info, false, null);
          float[] out = neurons[layer+1]._a.raw(); //extract the layer-th hidden feature
          for( int c=0; c<features; c++ )
            chks[_output._names.length+c].set(row,out[c]);
        }
      }
    }.doAll(adaptFrm);

    // Return just the output columns
    int x=_output._names.length, y=adaptFrm.numCols();
    Frame ret = adaptFrm.extractFrame(x, y);
    if (resp != null) ret.prepend(_output.responseName(), resp);
    Scope.exit();
    return ret;
  }


  // Make (potentially expanded) reconstruction
  private float[] score_autoencoder(Chunk[] chks, int row_in_chunk, double[] tmp, float[] preds, Neurons[] neurons) {
    assert(get_params()._autoencoder);
    assert(tmp.length == _output._names.length);
    for( int i=0; i<tmp.length; i++ )
      tmp[i] = chks[i].atd(row_in_chunk);
    score_autoencoder(tmp, preds, neurons); // this fills preds, returns MSE error (ignored here)
    return preds;
  }

  /**
   * Helper to reconstruct original data into preds array and compute the reconstruction error (MSE)
   * @param data Original data (unexpanded)
   * @param preds Reconstruction (potentially expanded)
   * @return reconstruction error
   */
  private double score_autoencoder(double[] data, float[] preds, Neurons[] neurons) {
    assert(model_info().get_params()._autoencoder);
    if (model_info().unstable()) {
      Log.warn(unstable_msg);
      throw new UnsupportedOperationException("Trying to predict with an unstable model.");
    }
    ((Neurons.Input)neurons[0]).setInput(-1, data); // expands categoricals inside
    DeepLearningTask.step(-1, neurons, model_info, false, null); // reconstructs data in expanded space
    float[] in  = neurons[0]._a.raw(); //input (expanded)
    float[] out = neurons[neurons.length - 1]._a.raw(); //output (expanded)
    assert(in.length == out.length);

    // First normalize categorical reconstructions to be probabilities
    // (such that they can be better compared to the input where one factor was 1 and the rest was 0)
//    model_info().data_info().softMaxCategoricals(out,out); //only modifies the categoricals

    // Compute MSE of reconstruction in expanded space (with categorical probabilities)
    double l2 = 0;
    for (int i = 0; i < in.length; ++i)
      l2 += Math.pow((out[i] - in[i]), 2);
    l2 /= in.length;

    if (preds!=null) {
      // Now scale back numerical columns to original data space (scale + shift)
      model_info().data_info().unScaleNumericals(out, out); //only modifies the numericals
      System.arraycopy(out, 0, preds, 0, out.length); //copy reconstruction into preds
    }
    return l2;
  }

  /**
   * Compute quantile-based threshold (in reconstruction error) to find outliers
   * @param mse Vector containing reconstruction errors
   * @param quantile Quantile for cut-off
   * @return Threshold in MSE value for a point to be above the quantile
   */
  public double calcOutlierThreshold(Vec mse, double quantile) {
    Frame mse_frame = new Frame(Key.make(), new String[]{"Reconstruction.MSE"}, new Vec[]{mse});
    DKV.put(mse_frame._key, mse_frame);

    QuantileModel.QuantileParameters parms = new QuantileModel.QuantileParameters();
    parms._train = mse_frame._key;
    parms._probs = new double[]{quantile};
    Quantile job = new Quantile(parms).trainModel();
    QuantileModel kmm = job.get();
    job.remove();
    double q = kmm._output._quantiles[0][0];
    kmm.delete();
    DKV.remove(mse_frame._key);
    return q;
  }

  // helper to push this model to another key (for keeping good models)
  private void putMeAsBestModel(Key bestModelKey) {
    DeepLearningModel bestModel = new DeepLearningModel(bestModelKey, null, this, true, model_info().data_info());
    DKV.put(bestModel._key, bestModel);
    assert (DKV.get(bestModelKey) != null);
    assert (bestModel.compareTo(this) <= 0);
  }

  @Override public void delete() {
    if (_output.weights != null && _output.biases != null) {
      for (Key k : _output.weights) {
        if (DKV.getGet(k) != null) ((Frame) DKV.getGet(k)).delete();
      }
      for (Key k : _output.biases) {
        if (DKV.getGet(k) != null) ((Frame) DKV.getGet(k)).delete();
      }
    }
    super.delete();
  }

  void delete_xval_models( ) {
//    if (get_params().xval_models != null) {
//      for (Key k : get_params().xval_models) {
//        DKV.get(k).<DeepLearningModel>get().delete_best_model();
//        DKV.get(k).<DeepLearningModel>get().delete();
//      }
//    }
  }

  private String getHeader() {
    assert get_params()._autoencoder;
    StringBuilder sb = new StringBuilder();
    final int len = model_info().data_info().fullN();
    String prefix = "reconstr_";
    assert (model_info().data_info()._responses == 0);
    String[] coefnames = model_info().data_info().coefNames();
    assert (len == coefnames.length);
    for (int c = 0; c < len; c++) {
      if (c>0) sb.append(",");
      sb.append(prefix + coefnames[c]);
    }
    return sb.toString();
  }

  @Override protected SB toJavaInit(SB sb, SB fileContextSB) {
    sb = super.toJavaInit(sb, fileContextSB);
    String mname = JCodeGen.toJavaId(_key.toString());

    Neurons[] neurons = DeepLearningTask.makeNeuronsForTesting(model_info());
    final DeepLearningParameters p = model_info.get_params();

    sb.ip("public boolean isSupervised() { return " + isSupervised() + "; }").nl();
    sb.ip("public int nfeatures() { return "+_output.nfeatures()+"; }").nl();
    sb.ip("public int nclasses() { return "+ (p._autoencoder ? neurons[neurons.length-1].units : _output.nclasses()) + "; }").nl();
    sb.ip("public ModelCategory getModelCategory() { return ModelCategory."+_output.getModelCategory()+"; }").nl();

    if (model_info().data_info()._nums > 0) {
      JCodeGen.toStaticVar(sb, "NUMS", new double[model_info().data_info()._nums], "Workspace for storing numerical input variables.");
      JCodeGen.toStaticVar(sb, "NORMMUL", model_info().data_info()._normMul, "Standardization/Normalization scaling factor for numerical variables.");
      JCodeGen.toStaticVar(sb, "NORMSUB", model_info().data_info()._normSub, "Standardization/Normalization offset for numerical variables.");
    }
    if (model_info().data_info()._cats > 0) {
      JCodeGen.toStaticVar(sb, "CATS", new int[model_info().data_info()._cats], "Workspace for storing categorical input variables.");
    }
    JCodeGen.toStaticVar(sb, "CATOFFSETS", model_info().data_info()._catOffsets, "Workspace for categorical offsets.");
    if (model_info().data_info()._normRespMul != null) {
      JCodeGen.toStaticVar(sb, "NORMRESPMUL", model_info().data_info()._normRespMul, "Standardization/Normalization scaling factor for response.");
      JCodeGen.toStaticVar(sb, "NORMRESPSUB", model_info().data_info()._normRespSub, "Standardization/Normalization offset for response.");
    }
    if (p._hidden_dropout_ratios != null) {
      JCodeGen.toStaticVar(sb, "HIDDEN_DROPOUT_RATIOS", p._hidden_dropout_ratios, "Hidden layer dropout ratios.");
    }

    int[] layers = new int[neurons.length];
    for (int i=0;i<neurons.length;++i)
      layers[i] = neurons[i].units;
    JCodeGen.toStaticVar(sb, "NEURONS", layers, "Number of neurons for each layer.");

    if (get_params()._autoencoder) {
      sb.i(1).p("public int getPredsSize() { return " + model_info.units[model_info.units.length-1] + "; }").nl();
      sb.i(1).p("public boolean isAutoEncoder() { return true; }").nl();
      sb.i(1).p("public String getHeader() { return \"" + getHeader() + "\"; }").nl();
    }

    // activation storage
    sb.i(1).p("// Storage for neuron activation values.").nl();
    sb.i(1).p("public static final float[][] ACTIVATION = new float[][] {").nl();
    for (int i=0; i<neurons.length; i++) {
      String colInfoClazz = mname + "_Activation_"+i;
      sb.i(2).p("/* ").p(neurons[i].getClass().getSimpleName()).p(" */ ");
      sb.p(colInfoClazz).p(".VALUES");
      if (i!=neurons.length-1) sb.p(',');
      sb.nl();
      fileContextSB.i().p("// Neuron activation values for ").p(neurons[i].getClass().getSimpleName()).p(" layer").nl();
      JCodeGen.toClassWithArray(fileContextSB, null, colInfoClazz, new float[layers[i]]);
    }
    sb.i(1).p("};").nl();

    // biases
    sb.i(1).p("// Neuron bias values.").nl();
    sb.i(1).p("public static final float[][] BIAS = new float[][] {").nl();
    for (int i=0; i<neurons.length; i++) {
      String colInfoClazz = mname + "_Bias_"+i;
      sb.i(2).p("/* ").p(neurons[i].getClass().getSimpleName()).p(" */ ");
      sb.p(colInfoClazz).p(".VALUES");
      if (i!=neurons.length-1) sb.p(',');
      sb.nl();
      fileContextSB.i().p("// Neuron bias values for ").p(neurons[i].getClass().getSimpleName()).p(" layer").nl();
      float[] bias = i == 0 ? null : new float[model_info().get_biases(i-1).size()];
      if (i>0) {
        for (int j=0; j<bias.length; ++j) bias[j] = model_info().get_biases(i-1).get(j);
      }
      JCodeGen.toClassWithArray(fileContextSB, null, colInfoClazz, bias);
    }
    sb.i(1).p("};").nl();

    // weights
    sb.i(1).p("// Connecting weights between neurons.").nl();
    sb.i(1).p("public static final float[][] WEIGHT = new float[][] {").nl();
    for (int i=0; i<neurons.length; i++) {
      String colInfoClazz = mname + "_Weight_"+i;
      sb.i(2).p("/* ").p(neurons[i].getClass().getSimpleName()).p(" */ ");
      sb.p(colInfoClazz).p(".VALUES");
      if (i!=neurons.length-1) sb.p(',');
      sb.nl();
      if (i > 0) {
        fileContextSB.i().p("// Neuron weights connecting ").
                p(neurons[i - 1].getClass().getSimpleName()).p(" and ").
                p(neurons[i].getClass().getSimpleName()).
                p(" layer").nl();
      }
      float[] weights = i == 0 ? null : new float[model_info().get_weights(i-1).rows()*model_info().get_weights(i-1).cols()];
      if (i>0) {
        final int rows = model_info().get_weights(i-1).rows();
        final int cols = model_info().get_weights(i-1).cols();
        for (int j=0; j<rows; ++j)
          for (int k=0; k<cols; ++k)
            weights[j*cols+k] = model_info().get_weights(i-1).get(j,k);
      }
      JCodeGen.toClassWithArray(fileContextSB, null, colInfoClazz, weights);
    }
    sb.i(1).p("};").nl();
    return sb;
  }

  @Override protected boolean toJavaCheckTooBig() { return (model_info.size() > 1e6); }

  @Override protected void toJavaPredictBody( final SB bodySb, final SB classCtxSb, final SB fileCtxSb) {
    SB model = new SB();
    final DeepLearningParameters p = model_info.get_params();
    bodySb.i().p("java.util.Arrays.fill(preds,0);").nl();
    final int cats = model_info().data_info()._cats;
    final int nums = model_info().data_info()._nums;
    // initialize input layer
    if (nums > 0) bodySb.i().p("java.util.Arrays.fill(NUMS,0f);").nl();
    if (cats > 0) bodySb.i().p("java.util.Arrays.fill(CATS,0);").nl();
    bodySb.i().p("int i = 0, ncats = 0;").nl();
    if (cats > 0) {
      bodySb.i().p("for(; i<"+cats+"; ++i) {").nl();
      bodySb.i(1).p("if (!Double.isNaN(data[i])) {").nl();
      bodySb.i(2).p("int c = (int) data[i];").nl();
      if (model_info().data_info()._useAllFactorLevels)
        bodySb.i(2).p("CATS[ncats++] = c + CATOFFSETS[i];").nl();
      else
        bodySb.i(2).p("if (c != 0) CATS[ncats++] = c + CATOFFSETS[i] - 1;").nl();
      bodySb.i(1).p("}").nl();
      bodySb.i().p("}").nl();
    }
    if (nums > 0) {
      bodySb.i().p("final int n = data.length;").nl();
      bodySb.i().p("for(; i<n; ++i) {").nl();
      bodySb.i(1).p("NUMS[i" + (cats > 0 ? "-" + cats : "") + "] = Double.isNaN(data[i]) ? 0 : ");
      if (model_info().data_info()._normMul != null) {
        bodySb.p("(data[i] - NORMSUB[i" + (cats > 0 ? "-" + cats : "") + "])*NORMMUL[i" + (cats > 0 ? "-" + cats : "") + "];").nl();
      } else {
        bodySb.p("data[i];").nl();
      }
      bodySb.i(0).p("}").nl();
    }
    bodySb.i().p("java.util.Arrays.fill(ACTIVATION[0],0);").nl();
    if (cats > 0) {
      bodySb.i().p("for (i=0; i<ncats; ++i) ACTIVATION[0][CATS[i]] = 1f;").nl();
    }
    if (nums > 0) {
      bodySb.i().p("for (i=0; i<NUMS.length; ++i) {").nl();
      bodySb.i(1).p("ACTIVATION[0][CATOFFSETS[CATOFFSETS.length-1] + i] = Double.isNaN(NUMS[i]) ? 0f : (float) NUMS[i];").nl();
      bodySb.i().p("}").nl();
    }

    boolean tanh=(p._activation == DeepLearningParameters.Activation.Tanh || p._activation == DeepLearningParameters.Activation.TanhWithDropout);
    boolean relu=(p._activation == DeepLearningParameters.Activation.Rectifier || p._activation == DeepLearningParameters.Activation.RectifierWithDropout);
    boolean maxout=(p._activation == DeepLearningParameters.Activation.Maxout || p._activation == DeepLearningParameters.Activation.MaxoutWithDropout);

    final String stopping = p._autoencoder ? "(i<=ACTIVATION.length-1)" : "(i<ACTIVATION.length-1)";

    // make prediction: forward propagation
    bodySb.i().p("for (i=1; i<ACTIVATION.length; ++i) {").nl();
    bodySb.i(1).p("java.util.Arrays.fill(ACTIVATION[i],0f);").nl();
    if (maxout) {
      bodySb.i(1).p("float rmax = 0;").nl();
      bodySb.i(1).p("for (int r=0; r<ACTIVATION[i].length; ++r) {").nl();
      bodySb.i(2).p("final int cols = ACTIVATION[i-1].length;").nl();
      bodySb.i(2).p("float cmax = Float.NEGATIVE_INFINITY;").nl();
      bodySb.i(2).p("for (int c=0; c<cols; ++c) {").nl();
      bodySb.i(3).p("if " + stopping + " cmax = Math.max(ACTIVATION[i-1][c] * WEIGHT[i][r*cols+c], cmax);").nl();
      bodySb.i(3).p("else ACTIVATION[i][r] += ACTIVATION[i-1][c] * WEIGHT[i][r*cols+c];").nl();
      bodySb.i(2).p("}").nl();
      bodySb.i(2).p("if "+ stopping +" ACTIVATION[i][r] = Float.isInfinite(cmax) ? 0f : cmax;").nl();
      bodySb.i(2).p("ACTIVATION[i][r] += BIAS[i][r];").nl();
      bodySb.i(2).p("if " + stopping + " rmax = Math.max(rmax, ACTIVATION[i][r]);").nl();
      bodySb.i(1).p("}").nl();
      bodySb.i(2).p("for (int r=0; r<ACTIVATION[i].length; ++r) {").nl();
      bodySb.i(3).p("if (rmax > 1 ) ACTIVATION[i][r] /= rmax;").nl();
    } else {
      // optimized
      bodySb.i(1).p("int cols = ACTIVATION[i-1].length;").nl();
      bodySb.i(1).p("int rows = ACTIVATION[i].length;").nl();
      bodySb.i(1).p("int extra=cols-cols%8;").nl();
      bodySb.i(1).p("int multiple = (cols/8)*8-1;").nl();
      bodySb.i(1).p("int idx = 0;").nl();
      bodySb.i(1).p("float[] a = WEIGHT[i];").nl();
      bodySb.i(1).p("float[] x = ACTIVATION[i-1];").nl();
      bodySb.i(1).p("float[] y = BIAS[i];").nl();
      bodySb.i(1).p("float[] res = ACTIVATION[i];").nl();
      bodySb.i(1).p("for (int row=0; row<rows; ++row) {").nl();
      bodySb.i(2).p("float psum0 = 0, psum1 = 0, psum2 = 0, psum3 = 0, psum4 = 0, psum5 = 0, psum6 = 0, psum7 = 0;").nl();
      bodySb.i(2).p("for (int col = 0; col < multiple; col += 8) {").nl();
      bodySb.i(3).p("int off = idx + col;").nl();
      bodySb.i(3).p("psum0 += a[off    ] * x[col    ];").nl();
      bodySb.i(3).p("psum1 += a[off + 1] * x[col + 1];").nl();
      bodySb.i(3).p("psum2 += a[off + 2] * x[col + 2];").nl();
      bodySb.i(3).p("psum3 += a[off + 3] * x[col + 3];").nl();
      bodySb.i(3).p("psum4 += a[off + 4] * x[col + 4];").nl();
      bodySb.i(3).p("psum5 += a[off + 5] * x[col + 5];").nl();
      bodySb.i(3).p("psum6 += a[off + 6] * x[col + 6];").nl();
      bodySb.i(3).p("psum7 += a[off + 7] * x[col + 7];").nl();
      bodySb.i(2).p("}").nl();
      bodySb.i(2).p("res[row] += psum0 + psum1 + psum2 + psum3;").nl();
      bodySb.i(2).p("res[row] += psum4 + psum5 + psum6 + psum7;").nl();
      bodySb.i(2).p("for (int col = extra; col < cols; col++)").nl();
      bodySb.i(3).p("res[row] += a[idx + col] * x[col];").nl();
      bodySb.i(2).p("res[row] += y[row];").nl();
      bodySb.i(2).p("idx += cols;").nl();
      bodySb.i(1).p("}").nl();
      // Activation function
      bodySb.i(1).p("if " + stopping + " {").nl();
      bodySb.i(2).p("for (int r=0; r<ACTIVATION[i].length; ++r) {").nl();
      if (tanh) {
        bodySb.i(3).p("ACTIVATION[i][r] = 1f - 2f / (1f + (float)Math.exp(2*ACTIVATION[i][r]));").nl();
      } else if (relu) {
        bodySb.i(3).p("ACTIVATION[i][r] = Math.max(0f, ACTIVATION[i][r]);").nl();
      }
    }
    if (p._hidden_dropout_ratios != null) {
      bodySb.i(3).p("if (i<ACTIVATION.length-1) {").nl();
      bodySb.i(4).p("ACTIVATION[i][r] *= HIDDEN_DROPOUT_RATIOS[i-1];").nl();
      bodySb.i(3).p("}").nl();
    }
//    if (maxout) bodySb.i(1).p("}").nl();
    bodySb.i(2).p("}").nl();
    if (!maxout) bodySb.i(1).p("}").nl();
    if (_output.isClassifier()) {
      bodySb.i(1).p("if (i == ACTIVATION.length-1) {").nl();
      // softmax
      bodySb.i(2).p("float max = ACTIVATION[i][0];").nl();
      bodySb.i(2).p("for (int r=1; r<ACTIVATION[i].length; r++) {").nl();
      bodySb.i(3).p("if (ACTIVATION[i][r]>max) max = ACTIVATION[i][r];").nl();
      bodySb.i(2).p("}").nl();
      bodySb.i(2).p("float scale = 0f;").nl();
      bodySb.i(2).p("for (int r=0; r<ACTIVATION[i].length; r++) {").nl();
      bodySb.i(3).p("ACTIVATION[i][r] = (float) Math.exp(ACTIVATION[i][r] - max);").nl();
      bodySb.i(3).p("scale += ACTIVATION[i][r];").nl();
      bodySb.i(2).p("}").nl();
      bodySb.i(2).p("for (int r=0; r<ACTIVATION[i].length; r++) {").nl();
      bodySb.i(3).p("if (Float.isNaN(ACTIVATION[i][r]))").nl();
      bodySb.i(4).p("throw new RuntimeException(\"Numerical instability, predicted NaN.\");").nl();
      bodySb.i(3).p("ACTIVATION[i][r] /= scale;").nl();
      bodySb.i(3).p("preds[r+1] = ACTIVATION[i][r];").nl();
      bodySb.i(2).p("}").nl();
      bodySb.i(1).p("}").nl();
      bodySb.i().p("}").nl();
    } else if (!p._autoencoder) { //Regression
      bodySb.i(1).p("if (i == ACTIVATION.length-1) {").nl();
      // regression: set preds[1], FillPreds0 will put it into preds[0]
      if (model_info().data_info()._normRespMul != null) {
        bodySb.i(2).p("preds[1] = (ACTIVATION[i][0] / NORMRESPMUL[0] + NORMRESPSUB[0]);").nl();
      }
      else {
        bodySb.i(2).p("preds[1] = ACTIVATION[i][0];").nl();
      }
      bodySb.i(2).p("if (Double.isNaN(preds[1])) throw new RuntimeException(\"Predicted regression target NaN!\");").nl();
      bodySb.i(1).p("}").nl();
      bodySb.i().p("}").nl();
    } else { //AutoEncoder
      bodySb.i(1).p("if (i == ACTIVATION.length-1) {").nl();
      bodySb.i(2).p("for (int r=0; r<ACTIVATION[i].length; r++) {").nl();
      bodySb.i(3).p("if (Float.isNaN(ACTIVATION[i][r]))").nl();
      bodySb.i(4).p("throw new RuntimeException(\"Numerical instability, reconstructed NaN.\");").nl();
      bodySb.i(3).p("preds[r] = ACTIVATION[i][r];").nl();
      bodySb.i(2).p("}").nl();
      if (model_info().data_info()._nums > 0) {
        int ns = model_info().data_info().numStart();
        bodySb.i(2).p("for (int k=" + ns + "; k<" + model_info().data_info().fullN() + "; ++k) {").nl();
        bodySb.i(3).p("preds[k] = preds[k] / NORMMUL[k-" + ns + "] + NORMSUB[k-" + ns + "];").nl();
        bodySb.i(2).p("}").nl();
      }
      bodySb.i(1).p("}").nl();
      bodySb.i().p("}").nl();
      // DEBUGGING
//      bodySb.i().p("System.out.println(java.util.Arrays.toString(data));").nl();
//      bodySb.i().p("System.out.println(java.util.Arrays.toString(ACTIVATION[0]));").nl();
//      bodySb.i().p("System.out.println(java.util.Arrays.toString(ACTIVATION[ACTIVATION.length-1]));").nl();
//      bodySb.i().p("System.out.println(java.util.Arrays.toString(preds));").nl();
//      bodySb.i().p("System.out.println(\"\");").nl();
    }
    fileCtxSb.p(model);
    if (_output.autoencoder) return;
    if (_output.isClassifier()) {
      bodySb.ip("water.util.ModelUtils.correctProbabilities(preds, PRIOR_CLASS_DISTRIB, MODEL_CLASS_DISTRIB);").nl();
      bodySb.ip("preds[0] = hex.genmodel.GenModel.getPrediction(preds, data);").nl();
    } else {
      bodySb.ip("preds[0] = (float)preds[1];").nl();
    }
  }

  transient private final String unstable_msg = "Job was aborted due to observed numerical instability (exponential growth)."
          + "\nTry a different initial distribution, a bounded activation function or adding"
          + "\nregularization with L1, L2 or max_w2 and/or use a smaller learning rate or faster annealing.";

  @Override protected long checksum_impl() {
    return super.checksum_impl() * model_info.checksum_impl();
  }
}

