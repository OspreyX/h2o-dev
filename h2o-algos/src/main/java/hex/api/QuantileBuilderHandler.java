package hex.api;

import hex.quantile.Quantile;
import hex.schemas.QuantileV3;
import water.api.ModelBuilderHandler;
import water.api.Schema;

public class QuantileBuilderHandler extends ModelBuilderHandler<Quantile, QuantileV3, QuantileV3.QuantileParametersV2> {
  /** Required so that Handler.handle() gets the correct schema types. */
  @SuppressWarnings("unused") // called through reflection by RequestServer
  public Schema train(int version, QuantileV3 builderSchema) {
    return super.do_train(version, builderSchema);
  }

  /** Required so that Handler.handle() gets the correct schema types. */
  @SuppressWarnings("unused") // called through reflection by RequestServer
  public QuantileV3 validate_parameters(int version, QuantileV3 builderSchema) {
    return super.do_validate_parameters(version, builderSchema);
  }
}
