package hex.schemas;

import hex.tree.SharedTree;
import hex.tree.SharedTreeModel.SharedTreeParameters;
import water.api.*;

public class SharedTreeV3<B extends SharedTree, S extends SharedTreeV3<B,S,P>, P extends SharedTreeV3.SharedTreeParametersV3> extends SupervisedModelBuilderSchema<B,S,P> {

  public static class SharedTreeParametersV3<P extends SharedTreeParameters, S extends SharedTreeParametersV3<P, S>> extends SupervisedModelParametersSchema<P, S> {
    static public String[] own_fields = new String[] {
      "ntrees", "max_depth", "min_rows", "nbins", "seed"
    };

    @API(help="Number of trees.")
    public int ntrees;

    @API(help="Maximum tree depth.")
    public int max_depth;

    @API(help="Fewest allowed observations in a leaf (in R called 'nodesize').")
    public int min_rows;

    @API(help="Build a histogram of this many bins, then split at the best point")
    public int nbins;

    @API(help = "Seed for pseudo random number generator (if applicable)", level = API.Level.expert)
    public long seed;
  }
}
