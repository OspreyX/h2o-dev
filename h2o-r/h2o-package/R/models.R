#'
#' H2O Model Related Functions
#'

# ------------------------------- Helper Functions --------------------------- #
# Used to verify data, x, y and turn into the appropriate things
.verify_dataxy <- function(data, x, y, autoencoder = FALSE) {
  if(!is(data,  "H2OFrame"))
    stop('`data` must be an H2OFrame object')
  if(!is.character(x) && !is.numeric(x))
    stop('`x` must be column names or indices')
  if( !autoencoder )
    if(!is.character(y) && !is.numeric(y))
      stop('`y` must be a column name or index')

  cc <- colnames(data)

  if(is.character(x)) {
    if(!all(x %in% cc))
      stop("Invalid column names: ", paste(x[!(x %in% cc)], collapse=','))
    x_i <- match(x, cc)
  } else {
    if(any( x < 1L | x > length(cc)))
      stop('out of range explanatory variable ', paste(x[x < 1L | x > length(cc)], collapse=','))
    x_i <- x
    x <- cc[x_i]
  }

  x_ignore <- c()
  if( !autoencoder ) {
    if(is.character(y)){
      if(!(y %in% cc))
        stop(y, ' is not a column name')
      y_i <- which(y == cc)
    } else {
      if(y < 1L || y > length(cc))
        stop('response variable index ', y, ' is out of range')
      y_i <- y
      y <- cc[y]
    }

    if(!autoencoder && (y %in% x)) {
      warning('removing response variable from the explanatory variables')
      x <- setdiff(x,y)
    }
    x_ignore <- setdiff(setdiff(cc, x), y)
    if( length(x_ignore) == 0L ) x_ignore <- ''
    return(list(x=x, y=y, x_i=x_i, x_ignore=x_ignore, y_i=y_i))
  } else {
    if( !missing(y) ) stop("`y` should not be specified for autoencoder=TRUE, remove `y` input")
    return(list(x=x,x_i=x_i,x_ignore=x_ignore))
  }
}

.verify_datacols <- function(data, cols) {
  if(!is(data, "H2OFrame"))
    stop('`data` must be an H2OFrame object')
  if(!is.character(cols) && !is.numeric(cols))
    stop('`cols` must be column names or indices')

  cc <- colnames(data)
  if(length(cols) == 1L && cols == '')
    cols <- cc
  if(is.character(cols)) {
    if(!all(cols %in% cc))
      stop("Invalid column names: ", paste(cols[which(!cols %in% cc)], collapse=", "))
    cols_ind <- match(cols, cc)
  } else {
    if(any(cols < 1L | cols > length(cc)))
      stop('out of range explanatory variable ', paste(cols[cols < 1L | cols > length(cc)], collapse=','))
    cols_ind <- cols
    cols <- cc[cols_ind]
  }

  cols_ignore <- setdiff(cc, cols)
  if( length(cols_ignore) == 0L )
    cols_ignore <- ''
  list(cols=cols, cols_ind=cols_ind, cols_ignore=cols_ignore)
}

.build_cm <- function(cm, actual_names = NULL, predict_names = actual_names, transpose = TRUE) {
  categories <- length(cm)
  cf_matrix <- matrix(unlist(cm), nrow=categories)
  if(transpose)
    cf_matrix <- t(cf_matrix)

  cf_total <- apply(cf_matrix, 2L, sum)
  cf_error <- c(1 - diag(cf_matrix)/apply(cf_matrix,1L,sum), 1 - sum(diag(cf_matrix))/sum(cf_matrix))
  cf_matrix <- rbind(cf_matrix, cf_total)
  cf_matrix <- cbind(cf_matrix, round(cf_error, 3L))

  if(!is.null(actual_names))
    dimnames(cf_matrix) = list(Actual = c(actual_names, "Totals"), Predicted = c(predict_names, "Error"))
  cf_matrix
}




.h2o.startModelJob <- function(conn = h2o.getConnection(), algo, params) {
  .key.validate(params$key)
  #---------- Force evaluate temporary ASTs ----------#
  ALL_PARAMS <- .h2o.__remoteSend(conn, method = "GET", .h2o.__MODEL_BUILDERS(algo))$model_builders[[algo]]$parameters

  params <- lapply(params, function(x) {if(is.integer(x)) x <- as.numeric(x); x})
  #---------- Check user parameter types ----------#
  error <- lapply(ALL_PARAMS, function(i) {
    e <- ""
    if (i$required && !(i$name %in% names(params)))
      e <- paste0("argument \"", i$name, "\" is missing, with no default\n")
    else if (i$name %in% names(params)) {
      # changing Java types to R types
      mapping <- .type.map[i$type,]
      type    <- mapping[1L, 1L]
      scalar  <- mapping[1L, 2L]
      if (is.na(type))
        stop("Cannot find type ", i$type, " in .type.map")
      if (scalar) { # scalar == TRUE
        if (type == "H2OModel")
            type <-  "character"
        if (!inherits(params[[i$name]], type))
          e <- paste0("\"", i$name , "\" must be of type ", type, ", but got ", class(params[[i$name]]), ".\n")
        else if ((length(i$values) > 1L) && !(params[[i$name]] %in% i$values)) {
          e <- paste0("\"", i$name,"\" must be in")
          for (fact in i$values)
            e <- paste0(e, " \"", fact, "\",")
          e <- paste(e, "but got", params[[i$name]])
        }
        if (inherits(params[[i$name]], 'numeric') && params[[i$name]] ==  Inf)
          params[[i$name]] <<- "Infinity"
        else if (inherits(params[[i$name]], 'numeric') && params[[i$name]] == -Inf)
          params[[i$name]] <<- "-Infinity"
      } else {      # scalar == FALSE
        k = which(params[[i$name]] == Inf | params[[i$name]] == -Inf)
        if (length(k) > 0)
          for (n in k)
            if (params[[i$name]][n] == Inf)
              params[[i$name]][n] <<- "Infinity"
            else
              params[[i$name]][n] <<- "-Infinity"
        if (!inherits(params[[i$name]], type))
          e <- paste0("vector of ", i$name, " must be of type ", type, ", but got ", class(params[[i$name]]), ".\n")
        else if (type == "character")
          params[[i$name]] <<- .collapse.char(params[[i$name]])
        else
          params[[i$name]] <<- .collapse(params[[i$name]])
      }
    }
    e
  })

  if(any(nzchar(error)))
    stop(error)

  #---------- Create parameter list to pass ----------#
  param_values <- lapply(params, function(i) {
    if(is(i, "H2OFrame"))
      i@frame_id
    else
      i
  })

  #---------- Validate parameters ----------#
  validation <- .h2o.__remoteSend(conn, method = "POST", paste0(.h2o.__MODEL_BUILDERS(algo), "/parameters"), .params = param_values)
  if(length(validation$validation_messages) != 0L) {
    error <- lapply(validation$validation_messages, function(i) {
      if( i$message_type == "ERROR" )
        paste0(i$message, ".\n")
      else ""
    })
    if(any(nzchar(error))) stop(error)
    warn <- lapply(validation$validation_messages, function(i) {
      if( i$message_type == "WARN" )
        paste0(i$message, ".\n")
      else ""
    })
    if(any(nzchar(warn))) warning(warn)
  }

  #---------- Build! ----------#
  res <- .h2o.__remoteSend(conn, method = "POST", .h2o.__MODEL_BUILDERS(algo), .params = param_values)

  job_key  <- res$job$key$name
  dest_key <- res$job$dest$name

  new("H2OModelFuture",conn=conn, job_key=job_key, model_id=dest_key)
}

.h2o.createModel <- function(conn = h2o.getConnection(), algo, params) {
 params$training_frame <- get("training_frame", parent.frame())
 tmp_train <- !.is.eval(params$training_frame)
 if( tmp_train ) {
    temp_train_key <- params$training_frame@frame_id
    .h2o.eval.frame(conn = conn, ast = params$training_frame@mutable$ast, frame_id = temp_train_key)
 }

 if (!is.null(params$validation_frame)){
    params$validation_frame <- get("validation_frame", parent.frame())
    tmp_valid <- !.is.eval(params$validation_frame)
    if( tmp_valid ) {
      temp_valid_key <- params$validation_frame@frame_id
      .h2o.eval.frame(conn = conn, ast = params$validation_frame@mutable$ast, frame_id = temp_valid_key)
    }
  }

  h2o.getFutureModel(.h2o.startModelJob(conn, algo, params))
}

h2o.getFutureModel <- function(object) {
  .h2o.__waitOnJob(object@conn, object@job_key)
  h2o.getModel(object@model_id, object@conn)
}

#' Predict on an H2O Model
#'
#' Obtains predictions from various fitted H2O model objects.
#'
#' This method dispatches on the type of H2O model to select the correct
#' prediction/scoring algorithm.
#'
#' @param object a fitted \linkS4class{H2OModel} object for which prediction is
#'        desired
#' @param newdata A \linkS4class{H2OFrame} object in which to look for
#'        variables with which to predict.
#' @param ... additional arguments to pass on.
#' @return Returns an \linkS4class{H2OFrame} object with probabilites and
#'         default predictions.
#' @seealso \code{link{h2o.deeplearning}}, \code{link{h2o.gbm}},
#'          \code{link{h2o.glm}}, \code{link{h2o.randomForest}} for model
#'          generation in h2o.
#' @export
predict.H2OModel <- function(object, newdata, ...) {
  if (missing(newdata)) {
    stop("predictions with a missing `newdata` argument is not implemented yet")
  }

  tmp_data <- !.is.eval(newdata)
  if( tmp_data ) {
    key  <- newdata@frame_id
    .h2o.eval.frame(conn=h2o.getConnection(), ast=newdata@mutable$ast, frame_id=key)
  }

  # Send keys to create predictions
  url <- paste0('Predictions/models/', object@model_id, '/frames/', newdata@frame_id)
  res <- .h2o.__remoteSend(object@conn, url, method = "POST")
  res <- res$predictions_frame
  h2o.getFrame(res$name)
}
#' @rdname predict.H2OModel
#' @export
h2o.predict <- predict.H2OModel

h2o.crossValidate <- function(model, nfolds, model.type = c("gbm", "glm", "deeplearning"), params, strategy = c("mod1", "random"), ...)
{
  output <- data.frame()

  if( nfolds < 2 ) stop("`nfolds` must be greater than or equal to 2")
  if( missing(model) & missing(model.type) ) stop("must declare `model` or `model.type`")
  else if( missing(model) )
  {
    if(model.type == "gbm") model.type = "h2o.gbm"
    else if(model.type == "glm") model.type = "h2o.glm"
    else if(model.type == "deeplearning") model.type = "h2o.deeplearning"

    model <- do.call(model.type, c(params))
  }
  output[1, "fold_num"] <- -1
  output[1, "model_key"] <- model@model_id
  # output[1, "model"] <- model@model$mse_valid

  data <- params$training_frame
  data <- eval(data)
  data.len <- nrow(data)

  # nfold_vec <- h2o.sample(fr, 1:nfolds)
  nfold_vec <- sample(rep(1:nfolds, length.out = data.len), data.len)

  fnum_id <- as.h2o(nfold_vec, model@conn)
  fnum_id <- h2o.cbind(fnum_id, data)

  xval <- lapply(1:nfolds, function(i) {
      params$training_frame <- data[fnum_id$object != i, ]
      params$validation_frame <- data[fnum_id$object != i, ]
      fold <- do.call(model.type, c(params))
      output[(i+1), "fold_num"] <<- i - 1
      output[(i+1), "model_key"] <<- fold@model_id
      # output[(i+1), "cv_err"] <<- mean(as.vector(fold@model$mse_valid))
      fold
    })
  print(output)

  model
}

#' Model Performance Metrics in H2O
#'
#' Given a trained h2o model, compute its performance on the given
#' dataset
#'
#'
#' @param model An \linkS4class{H2OModel} object
#' @param data An \linkS4class{H2OFrame}. The model will make predictions
#'        on this dataset, and subsequently score them. The dataset should
#'        match the dataset that was used to train the model, in terms of
#'        column names, types, and dimensions. If data is passed in, then train and valid are ignored.
#' @param valid A logical value indicating whether to return the validation metrics (constructed during training).
#' @param ... Extra args passed in for use by other functions.
#' @return Returns an object of the \linkS4class{H2OModelMetrics} subclass.
#' @examples
#' library(h2o)
#' localH2O <- h2o.init()
#' prosPath <- system.file("extdata", "prostate.csv", package="h2o")
#' prostate.hex <- h2o.uploadFile(localH2O, path = prosPath)
#' prostate.hex$CAPSULE <- as.factor(prostate.hex$CAPSULE)
#' prostate.gbm <- h2o.gbm(3:9, "CAPSULE", prostate.hex)
#' h2o.performance(model = prostate.gbm, data=prostate.hex)
#' @export
h2o.performance <- function(model, data=NULL, valid=FALSE, ...) {
  # Some parameter checking
  if(!is(model, "H2OModel")) stop("`model` must an H2OModel object")
  if(!is.null(data) && !is(data, "H2OFrame")) stop("`data` must be an H2OFrame object")

  missingData <- missing(data) || is.null(data)
  trainingFrame <- model@parameters$training_frame
  data.frame_id <- if( missingData ) trainingFrame else data@frame_id
  if( !missingData && data.frame_id == trainingFrame ) {
    warning("Given data is same as the training data. Returning the training metrics.")
    return(model@model$training_metrics)
  }
  else if( missingData && !valid ) return(model@model$training_metrics)    # no data, valid is false, return the training metrics
  else if( missingData &&  valid ) {
    if( is.null(model@model$validation_metrics@metrics) ) return(NULL)
    else                                                  return(model@model$validation_metrics)  # no data, but valid is true, return the validation metrics
  }
  else if( !missingData ) {
    parms <- list()
    parms[["model"]] <- model@model_id
    parms[["frame"]] <- data.frame_id
    res <- .h2o.__remoteSend(model@conn, method = "POST", .h2o.__MODEL_METRICS(model@model_id,data.frame_id), .params = parms)

    ####
    # FIXME need to do the client-side filtering...  PUBDEV-874:   https://0xdata.atlassian.net/browse/PUBDEV-874
    model_metrics <- Filter(function(mm) { mm$frame$name==data.frame_id}, res$model_metrics)[[1]]   # filter on data.frame_id, R's builtin Filter function
    #
    ####
    metrics <- model_metrics[!(names(model_metrics) %in% c("__meta", "names", "domains", "model_category"))]
    model_category <- model_metrics$model_category
    Class <- paste0("H2O", model_category, "Metrics")
    metrics$frame <- list()
    metrics$frame$name <- data.frame_id
    new(Class     = Class,
        algorithm = model@algorithm,
        on_train  = missingData,
        metrics   = metrics)
  } else {
    warning("Shouldn't be here, returning NULL")
    return(NULL)
  }
}

#' Retrieve an H2O AUC metric
#'
#' Retrieves the AUC value from an \linkS4class{H2OBinomialMetrics}.
#'
#' @param object An \linkS4class{H2OBinomialMetrics} object.
#' @param valid Retrieve the validation AUC
#' @param \dots extra arguments to be passed if `object` is of type
#'              \linkS4class{H2OModel} (e.g. train=TRUE)
#' @seealso \code{\link{h2o.giniCoef}} for the Gini coefficient,
#'          \code{\link{h2o.mse}} for MSE, and \code{\link{h2o.metric}} for the
#'          various threshold metrics. See \code{\link{h2o.performance}} for
#'          creating H2OModelMetrics objects.
#' @examples
#' library(h2o)
#' h2o.init()
#'
#' prosPath <- system.file("extdata", "prostate.csv", package="h2o")
#' hex <- h2o.uploadFile(prosPath)
#'
#' hex[,2] <- as.factor(hex[,2])
#' model <- h2o.gbm(x = 3:9, y = 2, training_frame = hex, distribution = "bernoulli")
#' perf <- h2o.performance(model, hex)
#' h2o.auc(perf)
#' @export
h2o.auc <- function(object, valid=FALSE, ...) {
  if(is(object, "H2OBinomialMetrics")){
    object@metrics$AUC
  } else if( is(object, "H2OModel") ) {
    l <- list(...)
    l <- .trainOrValid(l)
    l$valid <- l$valid || valid
    if( l$valid )
      if(!is.null(object@model$validation_metrics@metrics))
       return(object@model$validation_metrics$AUC)
      else {
        warning("This model has no validation metrics.", call. = FALSE)
        return(invisible(NULL))
      }
    else          return(object@model$training_metrics$AUC  )
  } else {
    warning(paste0("No AUC for ",class(object)))
    return(NULL)
  }
}

#' Retrieve the GINI Coefficcient
#'
#' Retrieves the GINI coefficient from an \linkS4class{H2OBinomialMetrics}.
#'
#' @param object an \linkS4class{H2OBinomialMetrics} object.
#' @param \dots extra arguments to be passed if `object` is of type
#'              \linkS4class{H2OModel} (e.g. train=TRUE)
#' @seealso \code{\link{h2o.auc}} for AUC,  \code{\link{h2o.giniCoef}} for the
#'          GINI coefficient, and \code{\link{h2o.metric}} for the various. See
#'          \code{\link{h2o.performance}} for creating H2OModelMetrics objects.
#'          threshold metrics.
#' @examples
#' library(h2o)
#' h2o.init()
#'
#' prosPath <- system.file("extdata", "prostate.csv", package="h2o")
#' hex <- h2o.uploadFile(prosPath)
#'
#' hex[,2] <- as.factor(hex[,2])
#' model <- h2o.gbm(x = 3:9, y = 2, training_frame = hex, distribution = "bernoulli")
#' perf <- h2o.performance(model, hex)
#' h2o.giniCoef(perf)
#' @export
h2o.giniCoef <- function(object, ...) {
  if(is(object, "H2OBinomialMetrics")){
    object@metrics$Gini
  }
  else{
    warning(paste0("No Gini for ",class(object)))
    return(NULL)
  }
}
#' Retrieves Mean Squared Error Value
#'
#' Retrieves the mean squared error value from an \linkS4class{H2OModelMetrics}
#' object.
#'
#' This function only supports \linkS4class{H2OBinomialMetrics},
#' \linkS4class{H2OMultinomialMetrics}, and \linkS4class{H2ORegressionMetrics} objects.
#'
#' @param object An \linkS4class{H2OModelMetrics} object of the correct type.
#' @param valid Retreive the validation metric.
#' @param ... Extra arguments to be passed if `object` is of type \linkS4class{H2OModel} (e.g. train=TRUE)
#' @seealso \code{\link{h2o.auc}} for AUC, \code{\link{h2o.mse}} for MSE, and
#'          \code{\link{h2o.metric}} for the various threshold metrics. See
#'          \code{\link{h2o.performance}} for creating H2OModelMetrics objects.
#' @examples
#' library(h2o)
#' h2o.init()
#'
#' prosPath <- system.file("extdata", "prostate.csv", package="h2o")
#' hex <- h2o.uploadFile(prosPath)
#'
#' hex[,2] <- as.factor(hex[,2])
#' model <- h2o.gbm(x = 3:9, y = 2, training_frame = hex, distribution = "bernoulli")
#' perf <- h2o.performance(model, hex)
#' h2o.mse(perf)
#' @export
h2o.mse <- function(object, valid=FALSE, ...) {
  if(is(object, "H2OBinomialMetrics") || is(object, "H2OMultinomialMetrics") || is(object, "H2ORegressionMetrics")){
    object@metrics$MSE
  } else {
    l <- list(...)
    l <- .trainOrValid(l)
    l$valid <- l$valid || valid
    if( l$valid )
      if(!is.null(object@model$validation_metrics@metrics))
       m <- object@model$validation_metrics@metrics
      else {
        warning("This model has no validation metrics.", call. = FALSE)
        return(invisible(NULL))
      }
    else          m <- object@model$training_metrics@metrics

    if( is(object, "H2OClusteringModel") ) return( m$centroid_stats$within_cluster_sum_of_squares )
    else if(      is(object, "H2OModel") ) return( m$MSE                                  )
    else {
      warning(paste0("No MSE for ",class(object)))
      return(NULL)
    }
  }
}

#' Retrieve the Log Loss Value
#'
#' Retrieves the log loss output for a \linkS4class{H2OBinomialMetrics} or
#' \linkS4class{H2OMultinomialMetrics} object
#'
#' @param object a \linkS4class{H2OModelMetrics} object of the correct type.
#' @param valid Retreive the validation metric.
#' @param \dots Extra arguments to be passed if `object` is of type
#'        \linkS4class{H2OModel} (e.g. train=TRUE)
#' @export
h2o.logloss <- function(object, valid=FALSE, ...) {
  if(is(object, "H2OBinomialMetrics") || is(object, "H2OMultinomialMetrics"))
    object@metrics$logloss
  else if( is(object, "H2OModel") ) {
    l <- list(...)
    l <- .trainOrValid(l)
    l$valid <- l$valid || valid
    if( l$valid )
      if(!is.null(object@model$validation_metrics@metrics))
       return(object@model$validation_metrics@metrics$logloss)
      else {
        warning("This model has no validation metrics.", call. = FALSE)
        return(invisible(NULL))
      }
    else          return(object@model$training_metrics@metrics$logloss  )

  } else  {
    warning(paste("No log loss for",class(object)))
    return(NULL)
  }
}

#'
#' Retrieve the variable importance.
#'
#' @param object An \linkS4class{H2OModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.varimp <- function(object, ...) {
  o <- object
  if( is(o, "H2OModel") ) {
    vi <- o@model$variable_importances
    if( is.null(vi) ) {
      warning("This model doesn't have variable importances", call. = FALSE)
      return(invisible(NULL))
    }
    vi
  } else {
    warning( paste0("No variable importances for ", class(o)) )
    return(NULL)
  }
}

#'
#' Retrieve Model Score History
#'
#' @param object An \linkS4class{H2OModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.scoreHistory <- function(object, ...) {
  o <- object
  if( is(o, "H2OModel") ) {
    sh <- o@model$scoring_history
    if( is.null(sh) ) return(NULL)
    print( sh )
    invisible( sh )
  } else {
    warning( paste0("No score history for ", class(o)) )
    return(NULL)
  }
}

#'
#' Retrieve the Hit Ratios
#'
#' @param object An \linkS4class{H2OModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @param valid Retreive the validation metric.
#' @export
h2o.hit_ratio_table <- function(object, valid=FALSE, ...) {
  o <- object
  hrt <- NULL

  # get the hrt if o is a model
  if( is(o, "H2OModel") ) {
    hrt <- o@model$training_metrics@metrics$hit_ratio_table  # by default grab the training metrics hrt
    l <- list(...)
    if( length(l)==0 && valid ) {
      l$valid <- valid
    }
    if( length(l)!=0L ) {
      l <- .trainOrValid(l)
      if( l$valid )
        if(!is.null(object@model$validation_metrics@metrics))
          hrt <- o@model$validation_metrics@metrics$hit_ratio_table  # otherwise get the validation_metrics hrt
        else {
          warning("This model has no validation metrics.", call. = FALSE)
          return(invisible(NULL))
        }
    }

  # if o is a data.frame, then the hrt was passed in -- just for pretty printing
  } else if( is(o, "data.frame") ) hrt <- o

  # warn if we got something unexpected...
  else warning( paste0("No hit ratio table for ", class(o)) )

  # if hrt not NULL, pretty print
  if( !is.null(hrt) ) print(hrt)
  invisible( hrt )  # return something
}

#' H2O Model Metric Accessor Functions
#'
#' A series of functions that retrieve model metric details.
#'
#' Many of these functions have an optional thresholds parameter. Currently
#' only increments of 0.1 are allowed. If not specified, the functions will
#' return all possible values. Otherwise, the function will return the value for
#' the indicated threshold.
#'
#' Currently, the these functions are only supported by
#' \linkS4class{H2OBinomialMetrics} objects.
#'
#' @param object An \linkS4class{H2OModelMetrics} object of the correct type.
#' @param thresholds A value or a list of values between 0.0 and 1.0.
#' @param metric A specified paramter to retrieve.
#' @return Returns either a single value, or a list of values.
#' @seealso \code{\link{h2o.auc}} for AUC, \code{\link{h2o.giniCoef}} for the
#'          GINI coefficient, and \code{\link{h2o.mse}} for MSE. See
#'          \code{\link{h2o.performance}} for creating H2OModelMetrics objects.
#' @examples
#' library(h2o)
#' h2o.init()
#'
#' prosPath <- system.file("extdata", "prostate.csv", package="h2o")
#' hex <- h2o.uploadFile(prosPath)
#'
#' hex[,2] <- as.factor(hex[,2])
#' model <- h2o.gbm(x = 3:9, y = 2, training_frame = hex, distribution = "bernoulli")
#' perf <- h2o.performance(model, hex)
#' h2o.F1(perf)
#' @export
h2o.metric <- function(object, thresholds, metric) {
  if(is(object, "H2OBinomialMetrics")){
    if(!missing(thresholds)) {
      t <- as.character(thresholds)
      t[t=="0"] <- "0.0"
      t[t=="1"] <- "1.0"
      if(!all(t %in% rownames(object@metrics$thresholds_and_metric_scores))) {
        stop(paste0("User-provided thresholds: ", paste(t,collapse=', '), ", are not a subset of the available thresholds: ", paste(rownames(object@metrics$thresholds_and_metric_scores), collapse=', ')))
      }
      else {
        output <- object@metrics$thresholds_and_metric_scores[t, metric]
        names(output) <- t
        output
      }
    }
    else {
      output <- object@metrics$thresholds_and_metric_scores[, metric]
      names(output) <- rownames(object@metrics$thresholds_and_metric_scores)
      output
    }
  }
  else{
    stop(paste0("No ", metric, " for ",class(object)))
  }
}

#' @rdname h2o.metric
#' @export
h2o.F0point5 <- function(object, thresholds){
  h2o.metric(object, thresholds, "f0point5")
}

#' @rdname h2o.metric
#' @export
h2o.F1 <- function(object, thresholds){
  h2o.metric(object, thresholds, "f1")
}

#' @rdname h2o.metric
#' @export
h2o.F2 <- function(object, thresholds){
  h2o.metric(object, thresholds, "f2")
}

#' @rdname h2o.metric
#' @export
h2o.accuracy <- function(object, thresholds){
  h2o.metric(object, thresholds, "accuracy")
}

#' @rdname h2o.metric
#' @export
h2o.error <- function(object, thresholds){
  h2o.metric(object, thresholds, "error")
}

#' @rdname h2o.metric
#' @export
h2o.maxPerClassError <- function(object, thresholds){
  1.0-h2o.metric(object, thresholds, "min_per_class_accuracy")
}

#' @rdname h2o.metric
#' @export
h2o.mcc <- function(object, thresholds){
  h2o.metric(object, thresholds, "absolute_MCC")
}

#' @rdname h2o.metric
#' @export
h2o.precision <- function(object, thresholds){
  h2o.metric(object, thresholds, "precision")
}

#' @rdname h2o.metric
#' @export
h2o.tpr <- function(object, thresholds){
  h2o.metric(object, thresholds, "tpr")
}

#' @rdname h2o.metric
#' @export
h2o.fpr <- function(object, thresholds){
  h2o.metric(object, thresholds, "fpr")
}

#' @rdname h2o.metric
#' @export
h2o.fnr <- function(object, thresholds){
  h2o.metric(object, thresholds, "fnr")
}

#' @rdname h2o.metric
#' @export
h2o.tnr <- function(object, thresholds){
  h2o.metric(object, thresholds, "tnr")
}

#' @rdname h2o.metric
#' @export
h2o.recall <- function(object, thresholds){
  h2o.metric(object, thresholds, "tpr")
}

#' @rdname h2o.metric
#' @export
h2o.sensitivity <- function(object, thresholds){
  h2o.metric(object, thresholds, "tpr")
}

#' @rdname h2o.metric
#' @export
h2o.fallout <- function(object, thresholds){
  h2o.metric(object, thresholds, "fpr")
}

#' @rdname h2o.metric
#' @export
h2o.missrate <- function(object, thresholds){
  h2o.metric(object, thresholds, "fnr")
}

#' @rdname h2o.metric
#' @export
h2o.specificity <- function(object, thresholds){
  h2o.metric(object, thresholds, "tnr")
}

#
#
h2o.find_threshold_by_max_metric <- function(object, metric) {
  if(!is(object, "H2OBinomialMetrics")) stop(paste0("No ", metric, " for ",class(object)))
  max_metrics <- object@metrics$max_criteria_and_metric_scores
  max_metrics[match(metric,max_metrics$metric),"threshold"]
}

#
# No duplicate thresholds allowed
h2o.find_row_by_threshold <- function(object, threshold) {
  if(!is(object, "H2OBinomialMetrics")) stop(paste0("No ", threshold, " for ",class(object)))
  tmp <- object@metrics$thresholds_and_metric_scores
  if( is.null(tmp) ) return(NULL)
  res <- tmp[abs(as.numeric(tmp$threshold) - threshold) < 1e-8,]  # relax the tolerance
  if( nrow(res) == 0L ) {
    # couldn't find any threshold within 1e-8 of the requested value, warn and return closest threshold
    row_num <- which.min(abs(tmp$threshold - threshold))
    closest_threshold <- tmp$threshold[row_num]
    warning( paste0("Could not find exact threshold: ", threshold, " for this set of metrics; using closest threshold found: ", closest_threshold, ". Rerun `h2o.performance` with your desired threshold explicitly set.") )
    return( tmp[row_num,] )
  }
  else if( nrow(res) > 1L ) res <- res[1L,]
  res
}

#'
#' Retrieve the Model Centers
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.centers <- function(object, ...) { as.data.frame(object@model$centers[,-1]) }

#'
#' Retrieve the Model Centers STD
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.centersSTD <- function(object, ...) { as.data.frame(object@model$centers_std)[,-1] }

#'
#' Get the Within SS
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.withinss <- function(object, ...) { h2o.mse(object, ...) }

#'
#' Get the total within cluster sum of squares.
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param valid Retreive the validation metric.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.tot_withinss <- function(object, valid=FALSE, ...) {
  l <- list(...)
  l <- .trainOrValid(l)
  l$valid <- l$valid || valid
  if( l$valid )
    if(!is.null(object@model$validation_metrics@metrics))
      return(object@model$validation_metrics@metrics$tot_withinss)
  else {
    warning("This model has no validation metrics.", call. = FALSE)
    return(invisible(NULL))
  }
  else          return(object@model$training_metrics@metrics$tot_withinss  )

}

#'
#' Get the between cluster sum of squares.
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param valid Retreive the validation metric.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.betweenss <- function(object, valid=FALSE, ...) {
  l <- list(...)
  l <- .trainOrValid(l)
  l$valid <- l$valid || valid
  if( l$valid )
    if(!is.null(object@model$validation_metrics@metrics))
      return(object@model$validation_metrics@metrics$betweenss)
  else {
    warning("This model has no validation metrics.", call. = FALSE)
    return(invisible(NULL))
  }
  else          return(object@model$training_metrics@metrics$betweenss  )
}

#'
#' Get the total sum of squares.
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param valid Retreive the validation metric.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.totss <- function(object,valid=FALSE, ...) {
  l <- list(...)
  l <- .trainOrValid(l)
  l$valid <- l$valid || valid
  if( l$valid )
    if(!is.null(object@model$validation_metrics@metrics))
      return(object@model$validation_metrics@metrics$totss)
  else {
    warning("This model has no validation metrics.", call. = FALSE)
    return(invisible(NULL))
  }
  else          return(object@model$training_metrics@metrics$totss  )
}

#'
#' Retrieve the number of iterations.
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.num_iterations <- function(object) { object@model$model_summary$number_of_iterations }

#'
#' Retrieve the cluster sizes
#'
#' @param object An \linkS4class{H2OClusteringModel} object.
#' @param valid Retrieve the validation metric.
#' @param \dots further arguments to be passed on (currently unimplemented)
#' @export
h2o.cluster_sizes <- function(object,valid=FALSE, ...) {
  l <- list(...)
  l <- .trainOrValid(l)
  l$valid <- l$valid || valid
  if( l$valid )
    if(!is.null(object@model$validation_metrics@metrics))
     return(object@model$validation_metrics@metrics$centroid_stats$size)
    else {
      warning("This model has no validation metrics.", call. = FALSE)
      return(invisible(NULL))
    }
  else          return(object@model$training_metrics@metrics$centroid_stats$size  )
}

#'
#' Print the Model Summary
#'
#' @param object An \linkS4class{H2OModel} object.
#' @param ... further arguments to be passed on (currently unimplemented)
#' @export
setMethod("summary", "H2OModel", function(object, ...) {
  o <- object
  m <- o@model
  cat("Model Details:\n")
  cat("==============\n\n")
  cat(class(o), ": ", o@algorithm, "\n", sep = "")
  cat("Model Key: ", o@model_id, "\n")

  # summary
  print(m$model_summary)

  # metrics
  cat("\n")
  if( !is.null(m$training_metrics) && !is.null(m$training_metrics@metrics) ) print(m$training_metrics)
  cat("\n")
  if( !is.null(m$validation_metrics) && !is.null(m$validation_metrics@metrics) ) print(m$validation_metrics)

  # History
  cat("\n")
  h2o.scoreHistory(o)

  # Varimp
  cat("\n")
  if( !is.null( m$variable_importances ) ) {
    cat("Variable Importances: (Extract with `h2o.varimp`) \n")
    cat("=================================================\n\n")
    h2o.varimp(o)
  }
})

#' Access H2O Confusion Matrices
#'
#' Retrieve either a single or many confusion matrices from H2O objects.
#'
#' The \linkS4class{H2OModelMetrics} version of this function will only take
#' \linkS4class{H2OBinomialMetrics} or \linkS4class{H2OMultinomialMetrics}
#' objects. If no threshold is specified, all possible thresholds are selected.
#'
#' @param object Either an \linkS4class{H2OModel} object or an
#'        \linkS4class{H2OModelMetrics} object.
#' @param newdata An \linkS4class{H2OFrame} object that can be scored on.
#'        Requires a valid response column.
#' @param thresholds (Optional) A value or a list of valid values between 0.0 and 1.0.
#'        This value is only used in the case of
#'        \linkS4class{H2OBinomialMetrics} objects.
#' @param valid Retreive the validation metric.
#' @param ... Extra arguments for extracting train or valid confusion matrices.
#' @return Calling this function on \linkS4class{H2OModel} objects returns a
#'         confusion matrix corresponding to the \code{\link{predict}} function.
#'         If used on an \linkS4class{H2OBinomialMetrics} object, returns a list
#'         of matrices corresponding to the number of thresholds specified.
#' @seealso \code{\link{predict}} for generating prediction frames,
#'          \code{\link{h2o.performance}} for creating
#'          \linkS4class{H2OModelMetrics}.
#' @examples
#' library(h2o)
#' h2o.init()
#' prosPath <- system.file("extdata", "prostate.csv", package="h2o")
#' hex <- h2o.uploadFile(prosPath)
#' hex[,2] <- as.factor(hex[,2])
#' model <- h2o.gbm(x = 3:9, y = 2, training_frame = hex, distribution = "bernoulli")
#' h2o.confusionMatrix(model, hex)
#' # Generating a ModelMetrics object
#' perf <- h2o.performance(model, hex)
#' h2o.confusionMatrix(perf)
#' @export
setGeneric("h2o.confusionMatrix", function(object, ...) {})

#' @rdname h2o.confusionMatrix
#' @export
setMethod("h2o.confusionMatrix", "H2OModel", function(object, newdata, valid=FALSE, ...) {
  l <- list(...)
  l <- .trainOrValid(l)
  l$valid <- l$valid || valid
  if( missing(newdata) ) {
    if( l$valid )
      if(!is.null(object@model$validation_metrics@metrics))
        return( h2o.confusionMatrix(object@model$validation_metrics, ...) )
      else {
        warning("This model has no validation metrics.", call. = FALSE)
        return(invisible(NULL))
      }
    else
      return( h2o.confusionMatrix(object@model$training_metrics, ...)   )
  } else if (l$valid)
    stop("Cannot use both newdata and valid = TRUE.", call. = FALSE)

  tmp <- !.is.eval(newdata)
  if( tmp ) {
    temp_key <- newdata@frame_id
    .h2o.eval.frame(conn = newdata@conn, ast = newdata@mutable$ast, frame_id = temp_key)
  }

  url <- paste0("Predictions/models/",object@model_id, "/frames/", newdata@frame_id)
  res <- .h2o.__remoteSend(object@conn, url, method="POST")

  # Make the correct class of metrics object
  metrics <- new(sub("Model", "Metrics", class(object)), algorithm=object@algorithm, metrics= res$model_metrics[[1L]])
  h2o.confusionMatrix(metrics)
})

# TODO: Need to put this in a better place
.trainOrValid <- function(l) {
  if( is.null(l$validation)        ) l$validation <- FALSE
  if( is.null(l$test)              ) l$test       <- FALSE
  if( is.null(l$valid)             ) l$valid      <- FALSE
  if( is.null(l$testing)           ) l$testing    <- FALSE
  l$valid <- l$valid || l$validation || l$test || l$testing
  l
}

#' @rdname h2o.confusionMatrix
#' @export
setMethod("h2o.confusionMatrix", "H2OModelMetrics", function(object, thresholds) {
  if( !is(object, "H2OBinomialMetrics") ) {
    if( is(object, "H2OMultinomialMetrics") )
      return(object@metrics$cm$table)
    warning(paste0("No Confusion Matrices for ",class(object)))
    return(NULL)
  }
  # H2OBinomial case
  if( missing(thresholds) ) {
    thresholds <- list(h2o.find_threshold_by_max_metric(object,"f1"))
    def <- TRUE
  } else def <- FALSE
  thresh2d <- object@metrics$thresholds_and_metric_scores
  max_metrics <- object@metrics$max_criteria_and_metric_scores
  d <- object@metrics$domain
  p <- max_metrics[match("tps",max_metrics$Metric),3]
  n <- max_metrics[match("fps",max_metrics$Metric),3]
  m <- lapply(thresholds,function(t) {
    row <- h2o.find_row_by_threshold(object,t)
    if( is.null(row) ) NULL
    else {
      tns <- row$tns; fps <- row$fps; fns <- row$fns; tps <- row$tps;
      rnames <- c(d, "Totals")
      cnames <- c(d, "Error", "Rate")
      col1 <- c(tns, fns, tns+fns)
      col2 <- c(fps, tps, fps+tps)
      col3 <- c(fps/(fps+tns), fns/(fns+tps), (fps+fns)/(fps+tns+fns+tps))
      col4 <- c( paste0(" =", fps, "/", fps+tns), paste0(" =", fns, "/", fns+tps), paste0(" =", fns+fps, "/", fps+tns+fns+tps) )
      fmts <- c("%i", "%i", "%f", "%s")
      tbl <- data.frame(col1,col2,col3,col4)
      colnames(tbl) <- cnames
      rownames(tbl) <- rnames
      header <-  "Confusion Matrix"
      if(def)
        header <- paste(header, "for max F1 @ threshold =", t)
      else
        header <- paste(header, "@ threshold =", row$threshold)
      attr(tbl, "header") <- header
      attr(tbl, "formats") <- fmts
      oldClass(tbl) <- c("H2OTable", "data.frame")
      tbl
    }
  })
  if( length(m) == 1L ) return( m[[1L]] )
  m
})

#' @export
plot.H2OModel <- function(x, ...) {
  if( is(x, "H2OBinomialModel") ) {
    if( !is.null(x@model$validation_metrics@metrics) ) metrics <- x@model$validation_metrics
    else                                               metrics <- x@model$training_metrics
    plot.H2OBinomialMetrics(metrics, ...)
  } else NULL
}

#' @export
plot.H2OBinomialMetrics <- function(x, type = "roc", ...) {
  # TODO: add more types (i.e. cutoffs)
  if(!type %in% c("roc")) stop("type must be 'roc'")
  if(type == "roc") {
    xaxis <- "False Positive Rate"; yaxis = "True Positive Rate"
    main <- paste(yaxis, "vs", xaxis)
    if( x@on_train ) main <- paste(main, "(on train)")
    else             main <- paste(main, "(on valid)")
    plot(x@metrics$thresholds_and_metric_scores$fpr, x@metrics$thresholds_and_metric_scores$tpr, main = main, xlab = xaxis, ylab = yaxis, ylim=c(0,1), xlim=c(0,1), ...)
    abline(0, 1, lty = 2)
  }
}

#' @export
screeplot.H2ODimReductionModel <- function(x, npcs, type = "barplot", main, ...) {
  # if(x@algorithm != "pca") stop("x must be a H2O PCA model")
  if(x@algorithm == "pca") {
    if(missing(npcs))
      npcs = min(10, x@model$parameters$k)
    else if(!is.numeric(npcs) || npcs < 1 || npcs > x@model$parameters$k)
      stop(paste("npcs must be a positive integer between 1 and", x@model$parameters$k, "inclusive"))


    if(missing(main))
      main = paste("h2o.prcomp(", strtrim(x@parameters$training_frame, 20), ")", sep="")
    if(type == "barplot")
      barplot(x@model$std_deviation[1:npcs]^2, main = main, ylab = "Variances", ...)
    else if(type == "lines")
      lines(x@model$std_deviation[1:npcs]^2, main = main, ylab = "Variances", ...)
    else
      stop("type must be either 'barplot' or 'lines'")
  } else if(x@algorithm == "svd") {
    if(is.null(x@model$std_deviation))
      stop("PCA results not found in SVD model!")
    if(missing(npcs))
      npcs = min(10, x@model$parameters$nv)
    else if(!is.numeric(npcs) || npcs < 1 || npcs > x@model$parameters$nv)
      stop(paste("npcs must be a positive integer between 1 and", x@model$parameters$nv, "inclusive"))
    if(missing(main))
      main = paste("h2o.prcomp(", strtrim(x@parameters$training_frame, 20), ")", sep="")
    if(type == "barplot")
      barplot(x@model$std_deviation[1:npcs]^2, main = main, ylab = "Variances", ...)
    else if(type == "lines")
      lines(x@model$std_deviation[1:npcs]^2, main = main, ylab = "Variances", ...)
    else
      stop("type must be either 'barplot' or 'lines'")
  }
}

# Handles ellipses
.model.ellipses <- function(dots) {
  lapply(names(dots), function(type) {
    stop(paste0('\n  unexpected argument "',
                type,'", is this legacy code? Try ?h2o.shim'), call. = FALSE)
  })
}
