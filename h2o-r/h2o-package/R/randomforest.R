#' Build a Big Data Random Forest Model
#'
#' Builds a Random Forest Model on an \linkS4class{H2OFrame}
#'
#' @param x A vector containing the names or indices of the predictor variables
#'        to use in building the GBM model.
#' @param y The name or index of the response variable. If the data does not
#'        contain a header, this is the column index number starting at 1, and
#'        increasing from left to right. (The response must be either an integer
#'        or a categorical variable).
#' @param training_frame An \code{\linkS4class{H2OFrame}} object containing the
#'        variables in the model.
#' @param model_id (Optional) The unique id assigned to the resulting model. If
#'        none is given, an id will automatically be generated.
#' @param mtries Columns to randomly select at each level, or -1 for
#'        sqrt(#cols).
#' @param sample_rate Sample rate, from 0. to 1.0.
#' @param build_tree_one_node Run on one node only; no network overhead but
#'        fewer cpus used.  Suitable for small datasets.
#' @param ntrees A nonnegative integer that determines the number of trees to
#'        grow.
#' @param max_depth Maximum depth to grow the tree.
#' @param min_rows Minimum number of rows to assign to teminal nodes.
#' @param nbins Number of bins to use in building histogram.
#' @param validation_frame An \code{\linkS4class{H2OFrame}} object containing the variables in the
#'        model.
#' @param balance_classes logical, indicates whether or not to balance training
#'        data class counts via over/under-sampling (for imbalanced data)
#' @param max_after_balance_size Maximum relative size of the training data after balancing class counts (can be less
#'        than 1.0)
#' @param seed Seed for random numbers (affects sampling) - Note: only
#'        reproducible when running single threaded
#' @param ... (Currently Unimplemented)
#' @return Creates a \linkS4class{H2OModel} object of the right type.
#' @seealso \code{\link{predict.H2OModel}} for prediction.
#' @export
h2o.randomForest <- function( x, y, training_frame,
                             model_id,
                             validation_frame,
                             mtries = -1,
                             sample_rate = 2/3,
                             build_tree_one_node = FALSE,
                             ntrees = 50,
                             max_depth = 20,
                             min_rows = 1,
                             nbins = 20,
                             balance_classes = FALSE,
                             max_after_balance_size = 5,
                             seed,
                             ...)
{
  # Pass over ellipse parameters and deprecated parameters
  if (length(list(...)) > 0)
    dots <- .model.ellipses( list(...))

  # Training_frame and validation_frame may be a key or an H2OFrame object
  if (!inherits(training_frame, "H2OFrame"))
    tryCatch(training_frame <- h2o.getFrame(training_frame),
             error = function(err) {
               stop("argument \"training_frame\" must be a valid H2OFrame or key")
             })
  if (!missing(validation_frame)) {
    if (!inherits(validation_frame, "H2OFrame"))
        tryCatch(validation_frame <- h2o.getFrame(validation_frame),
                 error = function(err) {
                   stop("argument \"validation_frame\" must be a valid H2OFrame or key")
                 })
  }

  # Parameter list to send to model builder
  parms <- list()
  parms$training_frame
  args <- .verify_dataxy(training_frame, x, y)
  parms$ignored_columns <- args$x_ignore
  parms$response_column <- args$y
  if(!missing(model_id))
    parms$model_id <- model_id
  if(!missing(validation_frame))
    parms$validation_frame <- validation_frame
  if(!missing(mtries))
    parms$mtries <- mtries
  if(!missing(sample_rate))
    parms$sample_rate <- sample_rate
  if(!missing(build_tree_one_node))
    parms$build_tree_one_node <- build_tree_one_node
  if(!missing(ntrees))
    parms$ntrees <- ntrees
  if(!missing(max_depth))
    parms$max_depth <- max_depth
  if(!missing(min_rows))
    parms$min_rows <- min_rows
  if(!missing(nbins))
    parms$nbins <- nbins
  if(!missing(balance_classes))
    parms$balance_classes <- balance_classes
  if(!missing(max_after_balance_size))
    parms$max_after_balance_size <- max_after_balance_size
  if(!missing(seed))
    parms$seed <- seed

  .h2o.createModel(training_frame@conn, 'drf', parms)
}
