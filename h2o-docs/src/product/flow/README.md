#Introduction

This guide will walk you through how to use H2O-dev's web UI, H2O Flow. To view a demo video of H2O Flow, click <a href="https://www.youtube.com/watch?feature=player_embedded&v=wzeuFfbW7WE" target="_blank">here</a>. 

##About H2O Flow

H2O Flow is an open-source user interface for H2O. It is a web-based interactive computational environment that allows you to combine code execution, text, mathematics, plots, and rich media in a single document, similar to <a href="http://ipython.org/notebook.html" target="_blank">iPython Notebooks</a>. 

With H2O Flow, you can capture, rerun, annotate, present, and share your workflow. H2O Flow allows you to use H2O interactively to import files, build models, and iteratively improve them. Based on your models, you can make predictions and add rich text to create vignettes of your work - all within Flow's browser-based environment. 

Flow's hybrid user interface seamlessly blends command-line computing with a modern graphical user interface. However, rather than displaying output as plain text, Flow provides a point-and-click user interface for every H2O operation. It allows you to access any H2O object in the form of well-organized tabular data. 

H2O Flow sends commands to H2O as a sequence of executable cells. The cells can be modified, rearranged, or saved to a library. Each cell contains an input field that allows you to enter commands, define functions, call other functions, and access other cells or objects on the page. When you execute the cell, the output is a graphical object, which can be inspected to view additional details. 

While H2O Flow supports REST API, R scripts, and Coffeescript, no programming experience is required to run H2O Flow. You can click your way through any H2O operation without ever writing a single line of code. You can even disable the input cells to run H2O Flow using only the GUI. H2O Flow is designed to guide you every step of the way, by providing input prompts, interactive help, and example flows. 

---

<a name="GetHelp"></a> 
# Getting Help 
---

First, let's go over the basics. Type `h` to view a list of helpful shortcuts. 

The following help window displays: 

![help menu](https://raw.githubusercontent.com/h2oai/h2o/master/docs/Flow-images/Shortcuts.png)

To close this window, click the **X** in the upper-right corner, or click the **Close** button in the lower-right corner. You can also click behind the window to close it. You can also access this list of shortcuts by clicking the **Help** menu and selecting **Keyboard Shortcuts**. 

For additional help, select the **Help** sidebar to the right and click the **Assist Me!** button. 

![Assist Me](images/Flow_AssistMeButton.png) 

You can also type `assist` in a blank cell and press **Ctrl+Enter**. A list of common tasks displays to help you find the correct command. 

 ![Assist Me links](images/Flow_assist.png)
 
There are multiple resources to help you get started with Flow in the **Help** sidebar. To access this document, select the **Getting Started with H2O Flow** link below the **Help Topics** heading. 

You can also explore the pre-configured flows available in H2O Flow for a demonstration of how to create a flow. To view the example flows, click the **Browse installed packs...** link in the **Packs** subsection of the **Help** sidebar. Click the **examples** folder and select the example flow from the list. 

  ![Flow Packs](images/Flow_ExampleFlows.png)

If you have a flow currently open, a confirmation window appears asking if the current notebook should be replaced. To load the example flow, click the **Load Notebook** button. 

To view the REST API documentation, click the **Help** tab in the sidebar and then select the type of REST API documentation (**Routes** or **Schemas**). 

 ![REST API documentation](images/Flow_REST_docs.png)

Before getting started with H2O Flow, make sure you understand the different cell modes. 

---

<a name="Cell"></a>
# Understanding Cell Modes

There are two modes for cells: edit and command. 


##Using Edit Mode
In edit mode, the cell is yellow with a blinking bar to indicate where text can be entered and there is an orange flag to the left of the cell.

![Edit Mode](images/Flow_EditMode.png)
 

##Using Command Mode
 In command mode, the flag is yellow. The flag also indicates the cell's format: 

- **MD**: Markdown 
   
   **Note**: Markdown formatting is not applied until you run the cell by clicking the **Run** button or clicking the **Run** menu and selecting **Run**. 

 ![Flow - Markdown](images/Flow_markdown.png)

- **CS**: Code

 ![Flow - Code](images/Flow_parse_code_ex.png)

- **RAW**: Raw format (for code comments)

 ![Flow - Raw](images/Flow_raw.png)

- **H[1-5]**: Heading level (where 1 is a first-level heading)

 ![Flow - Heading Levels](images/Flow_headinglevels.png)

**NOTE**: If there is an error in the cell, the flag is red. 

 ![Cell error](images/Flow_redflag.png)
 
 If the cell is executing commands, the flag is teal. The flag returns to yellow when the task is complete. 
 
 ![Cell executing](images/Flow_cellmode_runningflag.png)


##Running Flows
When you run the flow, a progress bar that indicates the current status of the flow. You can cancel the currently running flow by clicking the **Stop** button in the progress bar. 

  ![Flow Progress Bar](images/Flow_progressbar.png)

When the flow is complete, a message displays in the upper right. 
**Note**: If there is an error in the flow, H2O Flow stops the flow at the cell that contains the error. 

  ![Flow - Completed Successfully](images/Flow_run_pass.png)
  ![Flow - Did Not Complete](images/Flow_run_fail.png) 


##Using Keyboard Shortcuts

Here are some important keyboard shortcuts to remember: 

- Click a cell and press **Enter** to enter edit mode, which allows you to change the contents of a cell. 
- To exit edit mode, press **Esc**. 
- To execute the contents of a cell, press the **Ctrl** and **Enter** buttons at the same time.

The following commands must be entered in command mode.  

- To add a new cell *above* the current cell, press **a**. 
- To add a new cell *below* the current cell, press **b**. 
- To delete the current cell, press the **d** key *twice*. (**dd**). 

You can view these shortcuts by clicking **Help** > **Keyboard Shortcuts** or by clicking the **Help** tab in the sidebar. 


##Using Flow Buttons
There are also a series of buttons at the top of the page below the flow name that allow you to save the current flow, add a new cell, move cells up or down, run the current cell, and cut, copy, or paste the current cell. If you hover over the button, a description of the button's function displays. 

 ![Flow buttons](images/Flow_buttons.png)
 
You can also use the menus at the top of the screen to edit the cells, view specific format types (such as input or output), change the cell's format, or run the cell. You can also access troubleshooting information or obtain help with Flow.  
 ![Flow menus](images/Flow_menus.png)


Now that you are familiar with the cell modes, let's import some data. 

---

<a name="ImportData"></a>
# Importing Data

If you don't have any of your own data to work with, you can find some example datasets here: 

- <a href="http://docs.h2o.ai/resources/publicdata.html"  target="_blank">http://docs.h2o.ai/resources/publicdata.html </a>
- <a href="http://data.h2o.ai" target="_blank">http://data.h2o.ai</a>


There are multiple ways to import data in H2O flow:

- Click the **Assist Me!** button in the **Help** sidebar, then click the **importFiles** link. Enter the file path in the auto-completing **Search** entry field and press **Enter**. Select the file from the search results and select it by clicking the **Add All** link.
 
- You can also drag and drop the file onto the **Search** field in the cell.
  
 ![Flow - Import Files](images/Flow_Import_DragDrop.png)

- In a blank cell, select the CS format, then enter `importFiles [ "path/filename.format" ]` (where `path/filename.format` represents the complete file path to the file, including the full file name. The file path can be a local file path or a website address. 

After selecting the file to import, the file path displays in the "Search Results" section. To import a single file, click the plus sign next to the file. To import all files in the search results, click the **Add all** link. The files selected for import display in the "Selected Files" section. 

 ![Import Files](images/Flow_import.png)

- To import the selected file(s), click the **Import** button. 

- To remove all files from the "Selected Files" list, click the **Clear All** link. 

- To remove a specific file, click the **X** next to the file path. 

After you click the **Import** button, the raw code for the current job displays. A summary displays the results of the file import, including the number of imported files and their Network File System (nfs) locations. 

 ![Import Files - Results](images/Flow_import_results.png)

##Uploading Data

To upload a local file, click the **Flow** menu and select **Upload File...**. Click the **Choose File** button, select the file, click the **Choose** button, then click the **Upload** button. 
  
  ![File Upload Pop-Up](images/Flow_UploadDataset.png)
  
  When the file has uploaded successfully, a message displays in the upper right and the **Setup Parse** cell displays. 

  
  ![File Upload Successful](images/Flow_FileUploadPass.png)

Ok, now that your data is available in H2O Flow, let's move on to the next step: parsing. Click the **Parse these files** button to continue. 

---

<a name="ParseData"></a>
# Parsing Data

After you have imported your data, parse the data.

Select the parser type (if necessary) from the drop-down **Parser** list. For most data parsing, H2O automatically recognizes the data type, so the default settings typically do not need to be changed. The following options are available: 

- Auto
- XLS
- CSV
- SVMLight

If a separator or delimiter is used, select it from the **Separator** list. 

Select a column header option, if applicable: 

- **Auto**: Automatically detect header types.
- **First row contains column names**: Specify heading as column names.
- **First row contains data**: Specify heading as data. This option is selected by default.

Select any necessary additional options: 

- **Enable single quotes as a field quotation character**: Treat single quote marks (also known as apostrophes) in the data as a character, rather than an enum. This option is not selected by default. 
- **Delete on done**: Check this checkbox to delete the imported data after parsing. This option is selected by default. 

A preview of the data displays in the "Data Preview" section. 
 ![Flow - Parse options](images/Flow_parse_setup.png)

**Note**: To change the column type, select the drop-down list at the top of the column and select the data type. The options are: 
  - Unknown
  - Numeric
  - Enum
  - Time
  - UUID
  - String
  - Invalid


After making your selections, click the **Parse** button. 


After you click the **Parse** button, the code for the current job displays. 

 ![Flow - Parse code](images/Flow_parse_code_ex.png)
 
Since we've submitted a couple of jobs (data import & parse) to H2O now, let's take a moment to learn more about jobs in H2O.  
 
--- 
 
<a name="ViewJobs"></a>
# Viewing Jobs

Any command (such as `importFiles`) you enter in H2O is submitted as a job, which is associated with a key. The key identifies the job within H2O and is used as a reference. 

## Viewing Recent Jobs

To view all recent jobs, click the **Admin** menu, then click **Jobs**, or enter `getJobs` in a cell in CS mode. 

 ![View Jobs](images/Flow_getJobs.png)

The following information displays: 

- Key (linked to the specified job)
- Description of the type of job (for example, `GLM` or `Parse`)
- Status (`RUNNING` or `DONE`)

## Viewing Specific Jobs

To view a specific job, click the **Destination Key** link. 

![View Job - Model](images/Flow_ViewJob_Model.png)

The following information displays: 

- Current status
- Key (for example, `$0301ac10021432d4ffffffff$_8590b37303844cca7c603512c4064b04`)
- Destination key, which is linked to the originally imported data set (for example, AirlinesTest.hex)
- Run time
- Progress

To refresh this information, click the **Refresh** button. To view the details of the job, click the **View** button. 

**NOTE**: For a better understanding of how jobs work, make sure to review the [Viewing Frames](#ViewFrames) section as well. 
 
Ok, now that you understand how to find jobs in H2O, let's submit a new one by building a model. 

---

<a name="BuildModel"></a>
# Building Models

To build a model: 

- Click the **Assist Me!** button and select **buildModel**

  or 

- Click the **Assist Me!** button, select **getFrames**, then click the **Build Model...** button below the parsed .hex data set

The **Build Model...** button can be accessed from any page containing the .hex key for the parsed data (for example, `getJobs` > `getFrame`). 
 
In the **Build a Model** cell, select an algorithm from the drop-down menu: 

<a name="Kmeans"></a>
- **K-means**: Create a K-Means model
**Note**: For a K-Means model, the columns in the training frame cannot contain categorical values. If you select a dataset with categorical values as the training frame, the categorical columns are identified.

<a name="GLM"></a>
- **Generalized Linear Model**: Create a Generalized Linear model

<a name="drf"></a>
- **Distributed RF**: Create a distributed Random Forest model.  

<a name="nb"></a>
- **Naive Bayes**: Create a Naive Bayes model. 

<a name="pca"></a> 
- **Principal Component Analysis**: Create a Principal Components Analysis model for modeling without regularization or performing dimensionality reduction. 

<a name="GBM"></a>
- **Gradient Boosting Machine**: Create a Gradient Boosted model

<a name="DL"></a>
- **Deep Learning**: Create a Deep Learning model


The available options vary depending on the selected model. If an option is only available for a specific model type, the model type is listed. If no model type is specified, the option is applicable to all model types. 

- **Destination\_key**: (Optional) Enter a custom name for the model to use as a reference. By default, H2O automatically generates a destination key. 

- **Training_frame**: (Required) Select the dataset used to build the model. 
**NOTE**: If you click the **Build a model** button from the `Parse` cell, the training frame is entered automatically. 

- **Validation_frame**: (Optional) Select the dataset used to evaluate the accuracy of the model. 

- **Ignored_columns**: (Optional) Click the plus sign next to a column name to add it to the list of columns excluded from the model. To add all columns, click the **Add all** button. To remove a column from the list of ignored columns, click the X next to the column name. To remove all columns from the list of ignored columns, click the **Clear all** button. 

- **DropNA20Cols**: (Optional) Check this checkbox to omit columns that are missing (i.e., use 0 or NA) over 20% of their values. 

- **User_points**: [(K-Means](#Kmeans), [PCA)](#pca) For K-Means, specify the number of initial cluster centers. For PCA, specify the initial Y matrix. 
**Note**: The PCA **User_points** parameter should only be used by advanced users for testing purposes.  

- **Transform**: [(PCA)](#pca) Select the transformation method for the training data: None, Standardize, Normalize, Demean, or Descale. The default is None. 

- **Score\_each\_iteration**: (Optional) Score the validation set after each iteration of the model-building process. If you select this option, the model-building time increases. 

- **Response_column**: (Required for [GLM](#GLM), [GBM](#GBM), [DL](#DL)) Select the column to use as the independent variable.

- **Ratios**: [(Splitframe)](#sf) Specify the split ratio. The resulting number of the split is the ratio length +1. The default value is 0.5. 

- **Balance_classes**: ([GBM](#GBM), [DL](#DL), [DRF](#drf), [NaiveBayes)](#nb) Upsample the minority classes to balance the class distribution. This option is not selected by default. 

- **Max\_after\_balance\_size**: [(GLM](#GLM), [GBM](#GBM), [DL](#DL), [DRF](#drf), [NaiveBayes)](#nb) Specify the balanced class dataset size (as a multiple of the original dataset size). The default value is 5. 

- **Ntrees**: [(GBM](#GBM), [DRF)](#drf) Specify the number of trees. For Grid Search, use comma-separated values (for example: 50,100,150,200). The default value is 50. 

- **Max\_depth**: [(GBM](#GBM), [DRF)](#drf) Specify the maximum tree depth. For Grid Search, use comma-separated values (for example: 5,7). For GBM, the default value is 5. For DRF, the default value is 20. 

- **Min\_rows**: [(GBM](#GBM), [DRF)](#drf) Specify the minimum number of observations for a leaf ("nodesize" in R). For Grid Search, use comma-separated values. The default value is 10. 

- **Nbins**: [(GBM](#GBM), [DRF)](#drf) Specify the number of bins for the histogram. The default value is 20. 

- **Mtries**: [(DRF)](#drf) Specify the columns to randomly select at each level. To use the square root of the columns, enter `-1`.  The default value is -1.  

- **Sample\_rate**: [(DRF)](#drf) Specify the sample rate. The range is 0 to 1.0 and the default value is 0.6666667. 

- **Build\_tree\_one\_node**: [(DRF)](#drf) To run on a single node, check this checkbox. This is suitable for small datasets as there is no network overhead but fewer CPUs are used. The default setting is disabled. 

- **Learn_rate**: [(GBM)](#GBM) Specify the learning rate. The range is 0.0 to 1.0 and the default is 0.1. 

- **Loss**: ([GBM](#GBM), [DL](#DL)) Select the loss function. For GBM, the options are auto, bernoulli, or none and the default is auto. For DL, the options are automatic, mean square, cross-entropy, or none and the default value is mean square. 

- **Variable_importance**: ([GBM](#GBM), [DL](#DL)) Check this checkbox to compute variable importance. This option is not selected by default. 

- **K**: [(K-Means](#Kmeans), [PCA)](#pca) For K-Means, specify the number of clusters. The K-Means default is 0. For PCA, specify the rank of matrix approximation. The PCA default is 1.  

- **Gamma**: [(PCA)](#pca) Specify the regularization weight for PCA. The default is 0. 

- **Max_iterations**: [(K-Means](#Kmeans), [PCA)](#pca) Specify the number of training iterations. The default is 1000.

- **Max_iter**: [(GLM)](#GLM) Specify the number of training iterations. The default is 50.  

- **Init**: [(K-Means](#Kmeans), [PCA)](#pca) Select the initialization mode. For K-Means, the options are Furthest, PlusPlus, or None. For PCA, the options are PlusPlus, User, or None. 
**Note**: If PlusPlus is selected, the initial Y matrix is chosen by the final cluster centers from the K-Means PlusPlus algorithm. 

- **Family**: [(GLM)](#GLM) Select the model type (Gaussian, Binomial, Poisson, or Gamma).

- **N_folds**: ([GLM](#GLM), [DL](#DL)) Specify the number of cross-validations to perform. The default is 0. 

- **Keep\_cross\_validation\_splits**: [(DL)](#DL) Check this checkbox to keep the cross-validation frames. This option is not selected by default. 

- **Checkpoint**: [(DL)](#DL) Enter a model key associated with a previously-trained Deep Learning model. Use this option to build a new model as a continuation of a previously-generated model (e.g., by a grid search).

- **Override\_with\_best\_model**: [(DL)](#DL) Check this checkbox to override the final model with the best model found during training. This option is selected by default. 

- **Expert_mode**: [(DL)](#DL) Check this checkbox to enable "expert mode" and configure additional options. This option is not selected by default.

- **Autoencoder**: [(DL)](#DL) Check this checkbox to enable the Deep Learning autoencoder. This option is not selected by default. **Note**: This option requires **MeanSquare** as the loss function. 

- **Activation**: [(DL)](#DL) Select the activation function (Tahn, Tahn with dropout, Rectifier, Rectifier with dropout, Maxout, Maxout with dropout). The default option is Rectifier. 

- **Hidden**: [(DL)](#DL) Specify the hidden layer sizes (e.g., 100,100). For Grid Search, use comma-separated values: (10,10),(20,20,20). The default value is [200,200]. 

- **Epochs**: ([DL](#DL)) Specify the number of times to iterate (stream) the dataset. The value can be a fraction. The default value for DL is 10.0. 

- **Loss**: [(DL)](#DL) *Required* Select the loss function (MeanSquare, CrossEntropy, or MeanSqaureClassification). 

- **Quiet_mode**: [(DL)](#DL) Check this checkbox to display less output in the standard output. This option is not selected by default. 

- **Max\_confusion\_matrix\_size**: [(DL)](#DL) Specify the number of classes for the confusion matrices. The default value is 20. 

- **Class\_sampling\_factors**: ([GLM](#GLM), [DL](#DL), [DRF](#drf), [NaiveBayes)](#nb) Specify the per-class (in lexicographical order) over/under-sampling ratios. By default, these ratios are automatically computed during training to obtain the class balance. There is no default value. 

- **Solver**: [(GLM)](#GLM) Select the solver to use (ADMM, L\_BFGS, or none). [ADMM](http://www.stanford.edu/~boyd/papers/admm_distr_stats.html) supports more features and [L_BFGS](http://cran.r-project.org/web/packages/lbfgs/vignettes/Vignette.pdf) scales better for datasets with many columns. The default is ADMM. 

- **Beta_epsilon**: [(GLM)](#GLM) Specify the beta epsilon value. If the L1 normalization of the current beta change is below this threshold, consider using convergence. 

- **Diagnostics**: [(DL)](#DL) Check this checkbox to compute the variable importances for input features (using the Gedeon method). For large networks, selecting this option can reduce speed. This option is selected by default. 

- **Force\_load\_balance**: [(DL)](#DL) Check this checkbox to force extra load balancing to increase training speed for small datasets and use all cores. This option is selected by default. 

- **Single\_node\_mode**: [(DL)](#DL) Check this checkbox to force H2O to run on a single node for fine-tuning of model parameters. This option is not selected by default. 

- **Missing\_values\_handling**: [(DL)](#DL) Select how to handle missing values (skip, mean imputation, or none). The default value is mean imputation. 

- **Average_activation**: [(DL)](#DL) Specify the average activation for the sparse autoencoder. The default value is 0.0. 

- **Sparsity_beta**: [(DL)](#DL) Specify the sparsity regularization. The default value is 0.0. 

- **Max\_categorical\_features**: [(DL)](#DL) Specify the maximum number of categorical features enforced via hashing.

- **Reproducible**: [(DL)](#DL) To force reproducibility on small data, check this checkbox. If this option is enabled, the model takes more time to generate, since it uses only one thread. 

- **Laplace**: [(NaiveBayes)](#nb) Specify the Laplace smoothing parameter. The default value is 0. 

- **Min\_sdev**: [(NaiveBayes)](#nb) Specify the minimum standard deviation to use for observations without enough data. The default value is 0.001. 

- **Eps\_sdev**: [(NaiveBayes)](#nb) Specify the threshold for standard deviation. If this threshold is not met, the **min\_sdev** value is used. The default value is 1e-10. 

- **Min\_prob**: [(NaiveBayes)](#nb) Specify the minimum probability to use for observations without enough data. The default value is 0.001. 

- **Eps\_prob**: [(NaiveBayes)](#nb) Specify the threshold for standard deviation. If this threshold is not met, the **min\_sdev** value is used. The default value is 1e-10. 

**Advanced Options**

- **Standardize**: ([K-Means](#Kmeans), [GLM](#GLM)) To standardize the numeric columns to have a mean of zero and unit variance, check this checkbox. Standardization is highly recommended; if you do not use standardization, the results can include components that are dominated by variables that appear to have larger variances relative to other attributes as a matter of scale, rather than true contribution. This option is selected by default. 

- **Link**: [(GLM)](#GLM) Select a link function (Family_Default, Identity, Logit, Log, Inverse).

- **Alpha**: [(GLM)](#GLM) Specify the regularization distribution between L2 and L2. The default value is 0.5. 

- **Lambda**: [(GLM)](#GLM) Specify the regularization strength. The default value is 1.0E-5. 

- **Lambda_search**: [(GLM)](#GLM) Check this checkbox to enable lambda search, starting with lambda max. The given lambda is then interpreted as lambda min. 

- **Higher_accuracy**: [(GLM)](#GLM) Check this checkbox to enable line search. This provides GLM convergence but can reduce speed. 

- **Use\_all\_factor\_levels**: ([GLM](#GLM), [DL](#DL)) Check this checkbox to use all factor levels in the possible set of predictors; if you enable this option, sufficient regularization is required. By default, the first factor level is skipped. For Deep Learning models, this option is useful for determining variable importances and is automatically enabled if the autoencoder is selected. 

- **Train\_samples\_per\_iteration**: [(DL)](#DL) Specify the number of global training samples per MapReduce iteration. To specify one epoch, enter 0. To specify all available data (e.g., replicated training data), enter -1. To use the automatic values, enter -2. The default is -2. 

- **Target\_ratio\_comm\_to\_comp**: [(DL)](#DL) Specify the target ratio of communication overhead to computation. This option is only enabled for multi-node operation and if **train\_samples\_per\_iteration** equals -2 (auto-tuning). The default value is 0.02. 

- **Adaptive_rate**: [(DL)](#DL) Check this checkbox to enable the adaptive learning rate (ADADELTA). This option is selected by default. 

- **Rho**: [(DL)](#DL) Specify the adaptive learning rate time decay factor. The default value is 0.99. 

- **Epsilon**: [(DL)](#DL) Specify the adaptive learning rate time smoothing factor to avoid dividing by zero. The default value is 1.0E-8. 

- **Rate**: [(DL)](#DL) Specify the learning rate. Higher rates result in less stable models and lower rates result in slower convergence. The default value is 0.005. 

- **Rate_annealing**: [(DL)](#DL) Specify the learning rate annealing. The formula is rate/(1+rate_annealing value * samples). The default value is 1.0E-6. 

- **Momentum_start**: [(DL)](#DL) Specify the initial momentum at the beginning of training. A suggested value is 0.5. The default value is 0.0. 

- **Momentum_ramp**: [(DL)](#DL) Specify the number of training samples for increasing the momentum. The default value is 1000000.0. 

- **Nesterov\_accelerated\_gradient**: [(DL)](#DL) Check this checkbox to use the Nesterov accelerated gradient. This option is recommended and selected by default. 

- **Input\_dropout\_ratio**: [(DL)](#DL) Specify the input layer dropout ratio to improve generalization. Suggested values are 0.1 or 0.2. The default value is 0.0. 

- **Hidden\_dropout\_ratios**: [(DL)](#DL) Specify the hidden layer dropout ratios to improve generalization. Specify one value per hidden layer. The default is 0.5. 

- **L1**: [(DL)](#DL) Specify the L1 regularization to add stability and improve generalization; sets the value of many weights to 0. The default value is 0.0. 

- **L2**: [(DL)](#DL) Specify the L2 regularization to add stability and improve generalization; sets the value of many weights to smaller values. The default value is 0.0.

- **Score_interval**: [(DL)](#DL) Specify the shortest time interval (in seconds) to wait between model scoring. The default value is 5.0. 

**Expert Options**

- **Rate_decay**: [(DL)](#DL) Specify the learning rate decay factor between layers. 

- **Max_W2**: [(DL)](#DL) Specify the constraint for the squared sum of the incoming weights per unit (e.g., for Rectifier). The default value is infinity. 

- **Initial\_weight\_distribution**: [(DL)](#DL) Select the initial weight distribution (Uniform Adaptive, Uniform, Normal, or None). The default is Uniform Adaptive. 

- **Initial\_weight\_scale**: [(DL)](#DL) Specify the initial weight scale of the distribution function for Uniform or Normal distributions. For Uniform, the values are drawn uniformly from initial weight scale. For Normal, the values are drawn from a Normal distribution with the standard deviation of the initial weight scale. The default value is 1.0. 

- **Score\_training\_samples**: [(DL)](#DL) Specify the number of training set samples for scoring. To use all training samples, enter 0. The default value is 10000. 

- **Score\_validation\_samples**: [(DL)](#DL) Specify the number of validation set samples for scoring. To use all validation set samples, enter 0. The default value is 0. 

- **Score\_duty\_cycle**: [(DL)](#DL) Specify the maximum duty cycle fraction for scoring. A lower value results in more training and a higher value results in more scoring. The default value is 0.1.

- **Classification_stop**: [(DL)](#DL) Specify the stopping criterion for classification error fractions on training data. To disable this option, enter -1. The default value is 0.0. 

- **Regression_stop**: [(DL)](#DL) Specify the stopping criterion for regression error (MSE) on the training data. To disable this option, enter -1. The default value is 1.0E-6. 

- **Max\_hit\_ratio\_k**: [(DL)](#DL) Specify the maximum number (top K) of predictions to use for hit ratio computation (for multi-class only). To disable this option, enter 0. The default value is 10. 

- **Score\_validation\_sampling**: [(DL)](#DL) Select the method for sampling the validation dataset for scoring (uniform, stratified, or none). The default value is uniform. 

- **Fast_mode**: [(DL)](#DL) Check this checkbox to enable fast mode, a minor approximation in back-propagation. This option is selected by default. 

- **Ignore\_const\_cols**: [(DL)](#DL) Check this checkbox to ignore constant training columns, since no information can be gained from them. This option is selected by default. 

- **Replicate\_training\_data**: [(DL)](#DL) Check this checkbox to replicate the entire training dataset on every node for faster training on small datasets. This option is not selected by default. 

- **Shuffle\_training\_data**: [(DL)](#DL) Check this checkbox to shuffle the training data. This option is recommended if the training data is replicated and the value of **train\_samples\_per\_iteration** is close to the number of nodes times the number of rows. This option is not selected by default. 

- **Sparse**: [(DL)](#DL) Check this checkbox to use sparse data handling. This option is not selected by default. 

- **Col_major**: [(DL)](#DL) Check this checkbox to use a column major weight matrix for the input layer. This option can speed up forward propagation but may reduce the speed of backpropagation. This option is not selected by default. 

- **Seed**: ([K-Means](#Kmeans), [GBM](#GBM), [DL](#DL), [DRF](#drf)) Specify the random number generator (RNG) seed for algorithm components dependent on randomization. The seed is consistent for each H2O instance so that you can create models with the same starting conditions in alternative configurations. 

- **Prior**: [(GLM)](#GLM) Specify prior probability for y ==1. Use this parameter for logistic regression if the data has been sampled and the mean of response does not reflect reality. The default value is 0.0. 

- **NLambdas**: [(GLM)](#GLM) Specify the number of lambdas to use in the search. The default value is -1. 

- **Lambda\_min\_ratio**: [(GLM)](#GLM) Specify the min lambda to use in the lambda search (as a ratio of lambda max). The default value is -1.0. 

---

<a name="ViewModel"></a>
## Viewing Models

Click the **Assist Me!** button, then click the **getModels** link, or enter `getModels` in the cell in CS mode and press **Ctrl+Enter**. A list of available models displays. 

 ![Flow Models](images/Flow_getModels.png)

To inspect a model, check its checkbox then click the **Inspect** button, or click the **Inspect** button to the right of the model name. 

 ![Flow Model](images/Flow_GetModel.png)
 
 A summary of the model's parameters displays. To display more details, click the **Show All Parameters** button. 
 
 **NOTE**: The **Clone this model...** button will be supported in a future version. 
 
To compare models, check the checkboxes for the models to use in the comparison and click the **Compare selected models** button. To select all models, check the checkbox at the top of the checkbox column (next to the **KEY** heading). 

To learn how to make predictions, continue to the next section. 

---

<a name="Predict"></a>
# Making Predictions

After creating your model, click the destination key link for the model, then click the **Predict** button. 
Select the model to use in the prediction from the drop-down **Model:** menu and the data frame to use in the prediction from the drop-down **Frame** menu, then click the **Predict** button. 

 ![Making Predictions](images/Flow_makePredict.png)

---
 
<a name="ViewPredict"></a>
## Viewing Predictions

Click the **Assist Me!** button, then click the **getPredictions** link, or enter `getPredictions` in the cell in CS mode and press **Ctrl+Enter**. A list of the stored predictions displays. 
To view a prediction, click the **View** button to the right of the model name. 

 ![Viewing Predictions](images/Flow_getPredict.png)

---

<a name="ViewFrame"></a>
# Viewing Frames

To view a specific frame, click the "Destination Key" link for the specified frame, or enter `getFrame "FrameName"` in a cell in CS mode (where `FrameName` is the name of a frame, such as `allyears2k.hex`.

 ![Viewing specified frame](images/Flow_getFrame.png) 

From the `getFrame` cell, you can: 

- view a truncated list of the rows in the data frame by clicking the **View Data** button
- create a model by clicking the **Build Model** button
- make a prediction based on the data by clicking the **Predict** button
- download the data as a .csv file by clicking the **Download** button
- view the columns, data, and factors in more detail or plot a graph by clicking the **Inspect** button
- view the characteristics or domain of a specific column by clicking the **Summary** link

When you view a frame, you can "drill-down" to the necessary level of detail (such as a specific column or row) using the **View Data** and **Inspect** buttons. The following screenshot displays the results of clicking the **Inspect** button.

![Inspecting Frames](images/Flow_inspectFrame.png)

This screenshot displays the results of clicking the **Summary** link for the first column. 

![Inspecting Columns](images/Flow_inspectCol.png)


To view all frames, click the **Assist Me!** button, then click the **getFrames** link, or enter `getFrames` in the cell in CS mode and press **Ctrl+Enter**. A list of the current frames in H2O displays that includes the following information for each frame: 


- Column headings
- Number of rows and columns
- Size 


For parsed data, the following information displays: 

- Link to the .hex file
- The **Build Model**, **Predict**, and **Inspect** buttons

 ![Parsed Frames](images/Flow_getFrames.png)

To make a prediction, check the checkboxes for the frames you want to use to make the prediction, then click the **Predict on Selected Frames** button. 




## Plotting Frames

To create a plot from a frame, click the **Inspect** button, then click the **Plot** button. 

Select the type of plot (point, line, area, or interval) from the drop-down **Type** menu, then select the x-axis and y-axis from the following options: 

- label 
- missing 
- zeros
- pinfs 
- ninfs 
- min
- max
- mean
- sigma
- type
- cardinality

Select one of the above options from the drop-down **Color** menu to display the specified data in color, then click the **Plot** button to plot the data. 

---

<a name="Clips"></a>

# Using Clips

Clips enable you to save cells containing your workflow for later reuse. To save a cell as a clip, click the paperclip icon to the right of the cell (highlighted in the red box in the following screenshot). 
 ![Paperclip icon](images/Flow_clips_paperclip.png)

To use a clip in a workflow, click the "Clips" tab in the sidebar on the right. 

 ![Clips tab](images/Flow_clips.png)

All saved clips, including the default system clips (such as `assist`, `importFiles`, and `predict`), are listed. Clips you have created are listed under the "My Clips" heading. To select a clip to insert, click the circular button to the left of the clip name. To delete a clip, click the trashcan icon to right of the clip name. 

**NOTE**: The default clips listed under "System" cannot be deleted. 

Deleted clips are stored in the trash. To permanently delete all clips in the trash, click the **Empty Trash** button. 

**NOTE**: Saved data, including flows and clips, are persistent as long as the same IP address is used for the cluster. If a new IP is used, previously saved flows and clips are not available. 

---

<a name="Outline"></a>
# Viewing Outlines

The "Outline" tab in the sidebar displays a brief summary of the cells currently used in your flow; essentially, a command history. To jump to a specific cell, click the cell description. 

 ![View Outline](images/Flow_outline.png)

---

<a name="SaveFlow"></a>
# Saving Flows

You can save your flow for later reuse. To save your flow as a notebook, click the "Save" button (the first button in the row of buttons below the flow name), or click the drop-down "Flow" menu and select "Save." 
To enter a custom name for the flow, click the default flow name ("Untitled Flow") and type the desired flow name. A pencil icon indicates where to enter the desired name. 

 ![Renaming Flows](images/Flow_rename.png)

To confirm the name, click the checkmark to the right of the name field. 
 
 ![Confirm Name](images/Flow_rename2.png)

To reuse a saved flow, click the "Flows" tab in the sidebar, then click the flow name. To delete a saved flow, click the trashcan icon to the right of the flow name. 

 ![Flows](images/Flow_flows.png)

## Finding Saved Flows on your Disk
 
By default, flows are saved to the `h2oflows` directory underneath your home directory.  The directory where flows are saved is printed to stdout:
 
```
03-20 14:54:20.945 172.16.2.39:54323     95667  main      INFO: Flow dir: '/Users/<UserName>/h2oflows'
```

To back up saved flows, copy this directory to your preferred backup location.  

To specify a different location for saved flows, use the command-line argument `-flow_dir` when launching H2O:

`java -jar h2o.jar -flow_dir /<New>/<Location>/<For>/<Saved>/<Flows>`  

where `/<New>/<Location>/<For>/<Saved>/<Flows>` represents the specified location.  If the directory does not exist, it will be created the first time you save a flow.

## Saving Flows on a Hadoop cluster

**Note**: If you are running H2O Flow on a Hadoop cluster, H2O will try to find the HDFS home directory to use as the default directory for flows. If the HDFS home directory is not found, flows cannot be saved unless a directory is specified while launching using `-flow_dir`:

`hadoop jar h2odriver.jar -nodes 1 -mapperXmx 1g -output hdfsOutputDirName -flow_dir hdfs:///<Saved>/<Flows>/<Location>`  

The location specified in `flow_dir` may be either an hdfs or regular filesystem directory.  If the directory does not exist, it will be created the first time you save a flow.

## Duplicating Flows

To create a copy of the current flow, select the **Flow** menu, then click **Duplicate**. The name of the current flow changes to "Copy of <FlowName>" (where <FlowName> is the name of the flow). You can save the duplicated flow using this name by clicking **Flow** > **Save**. 


## Exporting Flows

After saving a flow as a notebook, click the **Flow** menu, then select **Export**. A new window opens and the saved flow is downloaded to the default downloads folder on your computer. The file is exported as *<filename>*.flow, where *<filename>* is the name specified when the flow was saved. 

**Caution**: You must have an active internet connection to export flows. 

## Loading Flows

To load a saved flow, click the **Flows** tab in the sidebar at the right. In the pop-up confirmation window that appears, select **Load Notebook**, or click **Cancel** to return to the current flow. 

 ![Confirm Replace Flow](images/Flow_confirmreplace.png)

After clicking **Load Notebook**, the saved flow is loaded. 

To load an exported flow, click the **Flow** menu and select **Open...**. In the pop-up window that appears, click the **Choose File** button and select the exported flow, then click the **Open** button. 

 ![Open Flow](images/Flow_Open.png)

**Notes**: 
- Only exported flows using the default .flow filetype are supported. Other filetypes will not open. 
- If the current notebook has the same name as the selected file, a pop-up confirmation appears to confirm that the current notebook should be overwritten. 

---

<a name="Troubleshooting"></a>
# Troubleshooting 

To troubleshoot issues in Flow, use the **Admin** menu. The **Admin** menu allows you to check the status of the cluster, view a timeline of events, and view or download logs for issue analysis. 

**NOTE**: To view the current version, click the **Help** menu, then click **About**. 

## Viewing Cluster Status

Click the **Admin** menu, then select **Cluster Status**. A summary of the status of the cluster (also known as a cloud) displays, which includes the same information: 

- Cluster health
- Whether all nodes can communicate (consensus)
- Whether new nodes can join (locked/unlocked)
- H2O version
- Number of used and available nodes
- When the cluster was created

 ![Cluster Status](images/Flow_CloudStatus.png)


The following information displays for each node:   

- IP address (name)
- Time of last ping
- Number of cores
- Load
- Amount of data (used/total)
- Percentage of cached data
- GC (free/total/max)
- Amount of disk space in GB (free/max)
- Percentage of free disk space 

To view more information, click the **Show Advanced** button. 

---

## Viewing CPU Status (Water Meter)

To view the current CPU usage, click the **Admin** menu, then click **Water Meter (CPU Meter)**. A new window opens, displaying the current CPU use statistics. 

---

## Viewing Logs
To view the logs for troubleshooting, click the **Admin** menu, then click **Inspect Log**. 

 ![Inspect Log](images/Flow_viewLog.png)

To view the logs for a specific node, select it from the drop-down **Select Node** menu. 

---

## Downloading Logs

To download the logs for further analysis, click the **Admin** menu, then click **Download Log**. A new window opens and the logs download to your default download folder. You can close the new window after downloading the logs. Send the logs to support@h2o.ai for issue resolution. 

---

## Viewing Stack Trace Information

To view the stack trace information, click the **Admin** menu, then click **Stack Trace**. 

 ![Stack Trace](images/Flow_stacktrace.png)

To view the stack trace information for a specific node, select it from the drop-down **Select Node** menu. 

---

##Viewing Network Test Results

To view network test results, click the **Admin** menu, then click **Network Test**. 

  ![Network Test Results](images/Flow_NetworkTest.png)

---

## Accessing the Profiler

To view the profiler, click the **Admin** menu, then click **Profiler**. 

 ![Profiler](images/Flow_profiler.png)

To view the profiler information for a specific node, select it from the drop-down **Select Node** menu. 

---


## Viewing the Timeline

To view a timeline of events in Flow, click the **Admin** menu, then click **Timeline**. The following information displays for each event: 

- Time of occurrence (HH:MM:SS:MS)
- Number of nanoseconds for duration
- Originator of event ("who")
- I/O type
- Event type
- Number of bytes sent & received

 ![Timeline](images/Flow_timeline.png)

To obtain the most recent information, click the **Refresh** button.  

---

## Shutting Down H2O

To shut down H2O, click the **Admin** menu, then click **Shut Down**. A *Shut down complete* message displays in the upper right when the cluster has been shut down. 


