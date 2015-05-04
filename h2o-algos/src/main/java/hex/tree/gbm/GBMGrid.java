package hex.tree.gbm;

import hex.*;
import hex.tree.SharedTreeGrid;
import water.DKV;
import water.util.ArrayUtils;
import water.H2O;
import water.Key;
import water.fvec.Frame;
import hex.tree.gbm.GBMModel.GBMParameters.Family;

/** A Grid of Models
 *  Used to explore Model hyper-parameter space.  Lazily filled in, this object
 *  represents the potentially infinite variety of hyperparameters of a given
 *  model & dataset.
 *
 *  One subclass per kind of Model, e.g. GBM or GLM or GBM or DL.  The Grid
 *  tracks Models and their hyperparameters, and will allow discovery of
 *  existing Models by hyperparameter, or building Models on demand by
 *  hyperparameter.  The Grid can manage a (simplistic) hyperparameter search
 *  space.
 *
 *  Hyperparameter values are limited to doubles in the API, but can be
 *  anything the subclass Grid desires internally.  E.g. the Grid for GBM
 *  will convert the initial center selection Enum to and from a simple integer
 *  value internally.
 */
public class GBMGrid<G extends GBMGrid<G>> extends SharedTreeGrid<G> {

  public static final String MODEL_NAME = "GBM";
  /** @return Model name */
  @Override protected String modelName() { return MODEL_NAME; }

  private static final String[] HYPER_NAMES    = ArrayUtils.append(SharedTreeGrid.HYPER_NAMES   ,new String[] {    "_distribution"               , "_learn_rate"});
  private static final double[] HYPER_DEFAULTS = ArrayUtils.append(SharedTreeGrid.HYPER_DEFAULTS,new double[] { Family.gaussian.ordinal(),     0.1f     });
  /** @return hyperparameter names corresponding to a Model.Parameter field names */
  @Override protected String[] hyperNames() { return HYPER_NAMES; }
  /** @return hyperparameter defaults, aligned with the field names */
  @Override protected double[] hyperDefaults() { return HYPER_DEFAULTS; }

  /** Ask the Grid for a suggested next hyperparameter value, given an existing
   *  Model as a starting point and the complete set of hyperparameter limits.
   *  Returning a NaN signals there is no next suggestion, which is reasonable
   *  if the obvious "next" value does not exist (e.g. exhausted all
   *  possibilities of an enum).  It is OK if a Model for the suggested value
   *  already exists; this will be checked before building any model.
   *  @param h The h-th hyperparameter 
   *  @param m A model to act as a starting point 
   *  @param hyperLimits Upper bounds for this search 
   *  @return Suggested next value for hyperparameter h or NaN if no next value */
  @Override protected double suggestedNextHyperValue( int h, Model m, double[] hyperLimits ) {
    throw H2O.unimpl();
  }

  /** @param hypers A set of hyper parameter values
   *  @return A ModelBuilder, blindly filled with parameters.  Assumed to be
   *  cheap; used to check hyperparameter sanity or make models */
  @Override protected GBM getBuilder( double[] hypers ) {
    GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
    getBuilder(parms,hypers);
    int slen = SharedTreeGrid.HYPER_NAMES.length;
    parms._distribution = Family.values()[(int)hypers[slen+0]];
    parms._learn_rate =         (float)hypers[slen+1];
    return new GBM(parms);
  }

  /** @param parms Model parameters
   *  @return Gridable parameters pulled out of the parms */
  @Override public double[] getHypers( Model.Parameters parms ) {
    GBMModel.GBMParameters gbmp = (GBMModel.GBMParameters)parms;
    double[] hypers = new double[HYPER_NAMES.length];
    super.getHypers(gbmp,hypers);
    int slen = SharedTreeGrid.HYPER_NAMES.length;
    hypers[slen+0] = gbmp._distribution.ordinal();
    hypers[slen+1] = gbmp._learn_rate;
    return hypers;
  }

  // Factory for returning a grid based on an algorithm flavor
  private GBMGrid( Key key, Frame fr ) { super(key,fr); }
  public static GBMGrid get( Frame fr ) { 
    Key k = Grid.keyName(MODEL_NAME, fr);
    GBMGrid kmg = DKV.getGet(k);
    if( kmg != null ) return kmg;
    kmg = new GBMGrid(k,fr);
    DKV.put(kmg);
    return kmg;
  }

  @Override protected long checksum_impl() { throw H2O.unimpl(); }
}
