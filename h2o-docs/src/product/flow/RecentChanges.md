#Recent Changes   

##H2O-Dev


###Shackleford (0.2.3.5) - 5/1/15

####New Features 
The following features have been added since the last release: 

#####API

- Need a /Log REST API to log client-side errors to H2O's log [(HEXDEV-291)](https://0xdata.atlassian.net/browse/HEXDEV-291)


#####Python

- add impute to python interface [(github)](https://github.com/h2oai/h2o-dev/commit/8a4d39e8bca6a4acfb8fc5f01a8febe07e519a08)

#####System

- Job admission control [(PUBDEV-536)](https://0xdata.atlassian.net/browse/PUBDEV-536) [(github)](https://github.com/h2oai/h2o-dev/commit/f5ef7323c72cf4be2dabf57a298fcc3d6687e9dd)
- Get Flow Exceptions/Stack Traces in H2O Logs [(PUBDEV-920)](https://0xdata.atlassian.net/browse/PUBDEV-920)

####Enhancements
The following changes are improvements to existing features (which includes changed default values):

#####Algorithms

- GLM: Name to be changed from normalized to standardized in output to be consistent between input/output [(PUBDEV-954)](https://0xdata.atlassian.net/browse/PUBDEV-954)
- GLM: It would be really useful if the coefficient magnitudes are reported in descending order [(PUBDEV-923)](https://0xdata.atlassian.net/browse/PUBDEV-923)
- PUBDEV-536: Limit DL models to 100M parameters [(github)](https://github.com/h2oai/h2o-dev/commit/5678a26447704021d8905e7c37dfcd37b74b7327)
- PUBDEV-536: Add accurate memory-based admission control for GBM/DRF [(github)](https://github.com/h2oai/h2o-dev/commit/fc06a28c64d24ecb3a46a6a84d90809d2aae4875)
- relax the tolerance a little more...[(github)](https://github.com/h2oai/h2o-dev/commit/a24f4886b94b93f71452848af3a7d0f7b440779c)
- Tree depth correction [(github)](https://github.com/h2oai/h2o-dev/commit/2ad89a3eff0d8aa411b94b1d6f387051671b9bf8)
- Comment out `duration_in_ms` for now, as it's always left at 0 [(github)](https://github.com/h2oai/h2o-dev/commit/8008f017e10424623f966c141280d080f08f80b5)
- Updated min mem computation for glm [(github)](https://github.com/h2oai/h2o-dev/commit/446d5c30cdffcf04a4b7e0feaefa501187049efb)
- GLM update: added lambda search info to scoring history [(github)](https://github.com/h2oai/h2o-dev/commit/90ac3bb9cc07e4f50b50b08aad8a33279a0ff43d)

#####Python

- python .show() on model and metric objects should match R/Flow as much as possible [(HEXDEV-289)](https://0xdata.atlassian.net/browse/HEXDEV-289)
- GLM model output, details from Python [(HEXDEV-95)](https://0xdata.atlassian.net/browse/HEXDEV-95)
- GBM model output, details from Python [(HEXDEV-102)](https://0xdata.atlassian.net/browse/HEXDEV-102)
- Run GBM from Python [(HEXDEV-99)](https://0xdata.atlassian.net/browse/HEXDEV-99)
- map domain to result from /Frames if needed [(github)](https://github.com/h2oai/h2o-dev/commit/b1746a52cd4399d58385cd29914fa54870680093)
- added confusion matrix to metric output [(github)](https://github.com/h2oai/h2o-dev/commit/f913cc1643774e9c2ec5455620acf11cbd613711)
- update `metrics_base_confusion_matrices()` [(github)](https://github.com/h2oai/h2o-dev/commit/41c0a4b0079426860ac3b65079d6be0e46c6f69c)
- fetch out `string_data` if type is string [(github)](https://github.com/h2oai/h2o-dev/commit/995e135e0a49e492cccfb65974160b04c764eb11)

#####R

- GBM model output, details from R [(HEXDEV-101)](https://0xdata.atlassian.net/browse/HEXDEV-101)
- Run GBM from R [(HEXDEV-98)](https://0xdata.atlassian.net/browse/HEXDEV-98)
- check if it's a frame then check NA [(github)](https://github.com/h2oai/h2o-dev/commit/d61de7d0b8a9dac7d5d6c7f841e19c88983308a1)

#####System

- Report MTU to logs [(PUBDEV-614)](https://0xdata.atlassian.net/browse/PUBDEV-614) [(github)](https://github.com/h2oai/h2o-dev/commit/bbc3ad54373a2c865ce913917ef07c9892d62603)
- Make parameter changes Log.info() instead of Log.warn() [(github)](https://github.com/h2oai/h2o-dev/commit/7047a46fff612f41cc678f297cfcbc57ed8165fd)


#####Web UI

- Flow: Confusion matrix: good to have consistency in the column and row name (letter) case [(PUBDEV-971)](https://0xdata.atlassian.net/browse/PUBDEV-971)
- Run GBM Multinomial from Flow [(HEXDEV-111)](https://0xdata.atlassian.net/browse/HEXDEV-111)
- Run GBM Regression from Flow [(HEXDEV-112)](https://0xdata.atlassian.net/browse/HEXDEV-112)
- Sort model types in alphabetical order in Flow [(PUBDEV-1011)](https://0xdata.atlassian.net/browse/PUBDEV-1011)



####Bug Fixes

The following changes are to resolve incorrect software behavior: 

#####Algorithms

- GLM: Model output display issues [(PUBDEV-956)](https://0xdata.atlassian.net/browse/PUBDEV-956)
- h2o.glm: ignores validation set [(PUBDEV-958)](https://0xdata.atlassian.net/browse/PUBDEV-958)
- DRF: reports wrong number of leaves in a summary [(PUBDEV-930)](https://0xdata.atlassian.net/browse/PUBDEV-930)
- h2o.glm: summary of a prediction frame gives na's as labels [(PUBDEV-959)](https://0xdata.atlassian.net/browse/PUBDEV-959)
- GBM: reports wrong max depth for a binary model on german data [(PUBDEV-839)](https://0xdata.atlassian.net/browse/PUBDEV-839)
- GLM: Confusion matrix missing in R for binomial models [(PUBDEV-950)](https://0xdata.atlassian.net/browse/PUBDEV-950) [(github)](https://github.com/h2oai/h2o-dev/commit/d8845e3245491a85c2cc6c932d5fad2c260c19d3)
- GLM: On airlines(40g) get ArrayIndexOutOfBoundsException [(PUBDEV-967)](https://0xdata.atlassian.net/browse/PUBDEV-967)
- GLM: Build model => Predict => Residual deviance/Null deviance different from training/validation metrics [(PUBDEV-991)](https://0xdata.atlassian.net/browse/PUBDEV-991)
- Domains returned by GLM for binomial classification problem are integers, but should be mapped to their label [(PUBDEV-999)](https://0xdata.atlassian.net/browse/PUBDEV-999)
- GLM: Validation on non training data gives NaN Res Deviance and AIC [(PUBDEV-1005)](https://0xdata.atlassian.net/browse/PUBDEV-1005)
- Confusion matrix has nan's in it [(PUBDEV-1000)](https://0xdata.atlassian.net/browse/PUBDEV-1000)
- glm fix: pass `model_id` from R (was being dropped) [(github)](https://github.com/h2oai/h2o-dev/commit/9d8698177a9d0a70668d2d51005947d0adda0292)

#####Python

- H2OPy: warns about version mismatch even when installed the latest from master [(PUBDEV-980)](https://0xdata.atlassian.net/browse/PUBDEV-980)
- Columns of type enum lose string label in Python H2OFrame.show() [(PUBDEV-965)](https://0xdata.atlassian.net/browse/PUBDEV-965)
- Bug in H2OFrame.show() [(HEXDEV-295)](https://0xdata.atlassian.net/browse/HEXDEV-295) [(github)](https://github.com/h2oai/h2o-dev/commit/b319969cff0f0e7a805e49563e863a1dbb0e1aa0)


#####R

- h2o.confusionMatrix for binary response gives not-found thresholds [(PUBDEV-957)](https://0xdata.atlassian.net/browse/PUBDEV-957)
- GLM: model_id param is ignored in R [(PUBDEV-1007)](https://0xdata.atlassian.net/browse/PUBDEV-1007)
- h2o.confusionmatrix: mixing cases(letter) for categorical labels while printing multinomial cm [(PUBDEV-996)](https://0xdata.atlassian.net/browse/PUBDEV-996)
- fix the dupe thresholds error [(github)](https://github.com/h2oai/h2o-dev/commit/e40d4fd50cfd9438b2f693228ca20ad4d6648b46)
- extra arg in impute example [(github)](https://github.com/h2oai/h2o-dev/commit/5a41e7672fa30b2e66a1261df8976d18e89f0057)
- fix missing param data [(github)](https://github.com/h2oai/h2o-dev/commit/6719d94b30caf214fac2c61759905c7d5d57a9ac)


#####System

- Builds : Failing intermittently due to java.lang.StackOverflowError [(PUBDEV-972)](https://0xdata.atlassian.net/browse/PUBDEV-972)
- Get H2O cloud hang with NPE and roll up stats problem, when click on build model glm from flow, on laptop after running a few python demos and R scripts [(PUBDEV-963)](https://0xdata.atlassian.net/browse/PUBDEV-963)

#####Web UI

- Flow :=> Airlines dataset => Build models glm/gbm/dl => water.DException$DistributedException: from /172.16.2.183:54321; by class water.fvec.RollupStats$ComputeRollupsTask; class java.lang.NullPointerException: null [(PUBDEV-603)](https://0xdata.atlassian.net/browse/PUBDEV-603)
- Flow => Preview Pojo => collapse not working [(PUBDEV-977)](https://0xdata.atlassian.net/browse/PUBDEV-977)
- Flow => Any algorithm => Select response => Select Add all for ignored columns => Try to unselect some from ignored columns => Build => Response column IsDepDelayed not found in frame: allyears_1987_2013.hex. [(PUBDEV-978)](https://0xdata.atlassian.net/browse/PUBDEV-978)
- Flow => ROC curve select something on graph => Table is displayed for selection => Collapse ROC curve => Doesn't collapse table, collapses only graph [(PUBDEV-1003)](https://0xdata.atlassian.net/browse/PUBDEV-1003)



---

###Severi (0.2.2.16) - 4/29/15

####New Features 

#####Python

- Release h2o-dev to PyPi [(PUBDEV-762)](https://0xdata.atlassian.net/browse/PUBDEV-762)
- Python Documentation [(PUBDEV-901)](https://0xdata.atlassian.net/browse/PUBDEV-901)
- Python docs Wrap Up [(PUBDEV-966)](https://0xdata.atlassian.net/browse/PUBDEV-966)
- add getters for res/null dev, fix kmeans,dl getters [(github)](https://github.com/h2oai/h2o-dev/commit/3f9839c25628e44cba77b44905c38c21bee60a9c)



####Enhancements

#####Algorithms 

- Use partial-sum version of mat-vec for DL POJO [(PUBDEV-936)](https://0xdata.atlassian.net/browse/PUBDEV-936)
- Always store weights and biases for DLTest Junit [(github)](https://github.com/h2oai/h2o-dev/commit/5bcbad8e07fd592e2db701adf9b4974a5b4470b1)
- Show the DL model size in the model summary [(github)](https://github.com/h2oai/h2o-dev/commit/bdba19a99b863cd2f49ff1bdcd4ca648b60d1372)
- Remove assertion in hot loop [(github)](https://github.com/h2oai/h2o-dev/commit/9d1682e2821fc648dda02497ba5200e45bd6b6f5)
- Rename ADMM to IRLSM [(github)](https://github.com/h2oai/h2o-dev/commit/6a108d38e7b9473a792a5ba36b58a860166c84c4)
- Added no intercept option to glm [(github)](https://github.com/h2oai/h2o-dev/commit/6d99bd194cbc4500f519e306f28384d7dca407e1)
- Code cleanup. Moved ModelMetricsPCAV3 out of H2O-algos [(github)](https://github.com/h2oai/h2o-dev/commit/1f691681407b579ed0b71e4e6d452120dc3263dd)
- Improve DL model checkpoint logic [(github)](https://github.com/h2oai/h2o-dev/commit/9a13070c0de6ac2bf34b0e60c305de7358711965)
- Updated glm output [(github)](https://github.com/h2oai/h2o-dev/commit/4359a17f573bf27f0ac5e078143299de09011325)
- Renamed normalized coefficients to standardized coefficients in glm output [(github)](https://github.com/h2oai/h2o-dev/commit/39b814d37e9e161d1dd943741afcff59fd83d745)
- Use proper tie breaking for NB [(github)](https://github.com/h2oai/h2o-dev/commit/4bbbd1b6161e8d2d62f8d3d9cb600e3c6d678653)
- Add check that DL parameters aren't modified by model training [(github)](https://github.com/h2oai/h2o-dev/commit/84d4ab6bc63b314bab4f38e629e77fb8207f705f)
- Reduce tolerances [(github)](https://github.com/h2oai/h2o-dev/commit/0654d3c2d644abb9aa0d0c25e032db1a4fd219ad)
- If no observations of a response leveland prediction is numeric, assume it is drawn from standard normal distribution (mean 0, standard deviation 1). Add validation test with split frame for naive Bayes [(github)](https://github.com/h2oai/h2o-dev/commit/50a5d9cbb1f77db568a23573f6cff0cf45cb36af)



#####Python

- replaced H2OFrame.send_frame() calls with cbind Exprs so that lazy evaluation is enforced [(github)](https://github.com/h2oai/h2o-dev/commit/2799b8cb2d01270556d4481a40af4a8da6f0519f)
- change default xmx/s behavior of h2o.init() [(github)](https://github.com/h2oai/h2o-dev/commit/843a232c52e6b357dbd84db3253b3e33b8297803)
- better handling of single row return and print [(github)](https://github.com/h2oai/h2o-dev/commit/b2e782bf17352009992ad1252762f43977f95c8b)


#####R

- Added interpolation to quantile to match R type 7 [(github)](https://github.com/h2oai/h2o-dev/commit/a330ffb6ff30c5500e3fb6a80fe92ac8b123a4be)
- Removed and tidied if's in quantile.H2OFrame since it now uses match.arg [(github)](https://github.com/h2oai/h2o-dev/commit/237306039a3e2483c92ac310e157ec515b885530)
- Connected validation dataset to glm in R [(github)](https://github.com/h2oai/h2o-dev/commit/e71895bd3fc7507092f65cbde6a914f74dacf85d)
- Removing h2o.aic from seealso link (doesn't exist) and updating documentation [(github)](https://github.com/h2oai/h2o-dev/commit/8fa994efea831722dd333327789a858ed902bc79)


#####System

- Add number of rows (per node) to ChunkSummary [(PUBDEV-938)](https://0xdata.atlassian.net/browse/PUBDEV-938) [(github)](https://github.com/h2oai/h2o-dev/commit/06d33469e0fabb0ae452f29dc633647aef8c9bb3)
- allow nrow as alias for count in groupby [(github)](https://github.com/h2oai/h2o-dev/commit/fbeef36b9dfea422dfed7f209a196731d9312e8b)
- Only launches task to fill in SVM zeros if the file is SVM [(github)](https://github.com/h2oai/h2o-dev/commit/d816c52a34f2e8f549f8a3b0bf7d976333366553)
- Adds more log traces to track progress of post-ingest actions [(github)](https://github.com/h2oai/h2o-dev/commit/c0073164d8392fd2d079db840b84e6330bebe2e6)
- Adds svm as a file extension to the hex name cleanup [(github)](https://github.com/h2oai/h2o-dev/commit/0ad9eec48650491f5ec2e01c010be9987dac0a21)

#####Web UI

- Flow: Inspect data => Round decimal points to 1 to be consistent with h2o1 [(PUBDEV-453)](https://0xdata.atlassian.net/browse/PUBDEV-453)
- Setup POJO download method for Flow [(PUBDEV-909)](https://0xdata.atlassian.net/browse/PUBDEV-909)
- Pretty-print POJO preview in flow [(PUBDEV-940)](https://0xdata.atlassian.net/browse/PUBDEV-940)
- Flow: It would be good if 'get predictions' also shows the data [(PUBDEV-883)](https://0xdata.atlassian.net/browse/PUBDEV-883)
- GBM model output, details in Flow [(HEXDEV-103)](https://0xdata.atlassian.net/browse/HEXDEV-103)
- Display a linked data table for each visualization in Flow [(PUBDEV-318)](https://0xdata.atlassian.net/browse/PUBDEV-318)
- Run GBM binomial from Flow (needs proper CM) [(PUBDEV-943)](https://0xdata.atlassian.net/browse/PUBDEV-943)



####Bug Fixes


#####Algorithms

- GLM: results from model and prediction on the same dataset do not match [(PUBDEV-922)](https://0xdata.atlassian.net/browse/PUBDEV-922) 
- GLM: when select AUTO as solver, for prostate, glm gives all zero coefficients [(PUBDEV-916)](https://0xdata.atlassian.net/browse/PUBDEV-916)
- Large (DL) models cause oversize issues during serialization [(PUBDEV-941)](https://0xdata.atlassian.net/browse/PUBDEV-941)
- Fixed name change for ADMM [(github)](https://github.com/h2oai/h2o-dev/commit/bc126aa8d4d7c5901ef90120c7997c67466922ae)

#####API

- Fix schema warning on startup [(PUBDEV-946)](https://0xdata.atlassian.net/browse/PUBDEV-946) [(github)](https://github.com/h2oai/h2o-dev/commit/bd9ae8013bc0de261e7258af85784e9e6f20df5e)


#####Python

- H2OVec.row_select(H2OVec) fails on case where only 1 row is selected [(PUBDEV-948)](https://0xdata.atlassian.net/browse/PUBDEV-948)
- fix pyunit [(github)](https://github.com/h2oai/h2o-dev/commit/79344be836d9111fee77ddebe034234662d7064f)

#####R 

- R: Parse of zip file fails, Summary fails on citibike data [(PUBDEV-835)](https://0xdata.atlassian.net/browse/PUBDEV-835)
- h2o. performance reports a different Null Deviance than the model object for the same dataset [(PUBDEV-816)](https://0xdata.atlassian.net/browse/PUBDEV-816)
- h2o.glm: no example on h2o.glm help page [(PUBDEV-962)](https://0xdata.atlassian.net/browse/PUBDEV-962)
- H2O R: Confusion matrices from R still confused [(PUBDEV-904)](https://0xdata.atlassian.net/browse/PUBDEV-904) [(github)](https://github.com/h2oai/h2o-dev/commit/36c887ddadd47682745b64812e081dcb2fa36659)
- R: h2o.confusionMatrix("H2OModel", ...) extra parameters not working [(PUBDEV-953)](https://0xdata.atlassian.net/browse/PUBDEV-953) [(github)](https://github.com/h2oai/h2o-dev/commit/ca59b2be46dd07caad60882b5c1daed0ee4837c6)
- h2o.confusionMatrix for binomial gives not-found thresholds on S3 -airlines 43g [(PUBDEV-957)](https://0xdata.atlassian.net/browse/PUBDEV-957)
- H2O summary quartiles outside tolerance of (max-min)/1000 [(PUBDEV-671)](https://0xdata.atlassian.net/browse/PUBDEV-671)
- fix space headers issue from R (was not url-encoding the column strings) [(github)](https://github.com/h2oai/h2o-dev/commit/f121b0324e981e229cd2704df11a0a946d4b2aeb)
- R CMD fixes [(github)](https://github.com/h2oai/h2o-dev/commit/62a1d7df8bceeea181b87d83f922db854f28b6db)
- Fixed broken R interface - make `validation_frame` non-mandatory [(github)](https://github.com/h2oai/h2o-dev/commit/18fba95392f94e566b80797839e5eb2899057333)

#####Sparkling Water

- Sparkling water : #UDP-Recv ERRR: UDP Receiver error on port 54322java.lang.ArrayIndexOutOfBoundsException:[(PUBDEV-311)](https://0xdata.atlassian.net/browse/PUBDEV-311)


#####System

- Mapr 3.1.1 : Memory is not being allocated for what is asked for instead the default is what cluster gets [(PUBDEV-937)](https://0xdata.atlassian.net/browse/PUBDEV-937)
- GLM: AIOOBwith msg '-14' at water.RPC$2.compute2(RPC.java:593) [(PUBDEV-917)](https://0xdata.atlassian.net/browse/PUBDEV-917)
- h2o.glm: model summary listing same info twice [(PUBDEV-915)](https://0xdata.atlassian.net/browse/PUBDEV-915)
- Parse: Detect and reject UTF-16 encoded files [(HEXDEV-285)](https://0xdata.atlassian.net/browse/HEXDEV-285)
- DataInfo Row categorical encoding AIOOBE [(HEXDEV-283)](https://0xdata.atlassian.net/browse/HEXDEV-283)
- Fix POJO Preview exception [(github)](https://github.com/h2oai/h2o-dev/commit/d553710f66ef989dc33a86608c5cf352a7d98168)
- Fix NPE in ChunkSummary [(github)](https://github.com/h2oai/h2o-dev/commit/cd113515257ee1c493fe84616deb0643400ef32c)
- fix global name collision [(github)](https://github.com/h2oai/h2o-dev/commit/bde0b6d8fed4009367b2e2ddf999bd71cbda3b3f)
 

###Severi (0.2.2.15) - 4/25/15

####New Features 


#####Python

- added min, max, sum, median for H2OVecs and respective pyunit [(github)](https://github.com/h2oai/h2o-dev/commit/3ec14f0bfe2d045ac57b3133a7ae12ea8e70aa3c)
- added min(), max(), and sum() functionality on H2OFrames and respective pyunits [(github)](https://github.com/h2oai/h2o-dev/commit/c86cf2bfa396f38b2a035405553a1f4bb34f55c0)


#####Web UI

- View POJO in Flow [(PUBDEV-781)](https://0xdata.atlassian.net/browse/PUBDEV-781)
- help > about page or add version on main page for easy bug reporting. [(PUBDEV-804)](https://0xdata.atlassian.net/browse/PUBDEV-804)
- POJO generation: GLM [(PUBDEV-712)](https://0xdata.atlassian.net/browse/PUBDEV-712) [(github)](https://github.com/h2oai/h2o-dev/commit/35683e29e39489bc2349461e78524328e4b24e63)
- GLM model output, details in Flow [(HEXDEV-96)](https://0xdata.atlassian.net/browse/HEXDEV-96)


####Enhancements

#####Algorithms 

- K means output clean up [(HEXDEV-187)](https://0xdata.atlassian.net/browse/HEXDEV-187)
- Add FNR/TNR/FPR/TPR to threshold tables, remove recall, specificity [(github)](https://github.com/h2oai/h2o-dev/commit/1de4910b8d295b2eaa79b8e96422f45746458d92)
- Add accessor for variable importances for DL [(github)](https://github.com/h2oai/h2o-dev/commit/e11323bca7cc4e58fb2d899a3c307f42f4a8624e)
- Relax CM error tolerance for F1-optimal threshold now that AUC2 doesn't necessarily create consistent thresholds with its own CMs. [(github)](https://github.com/h2oai/h2o-dev/commit/3ab3af08e28a64acc9a406ef5ff19bf6b1c7855a)
- Added scoring history to glm [(github)](https://github.com/h2oai/h2o-dev/commit/a652ba0388784bb54f0a69f524d21f08d66eabc5)
- Added model summary to glm [(github)](https://github.com/h2oai/h2o-dev/commit/c0d221cb964a072358602b2c13fd2c33b9fa9f4b)
- Add flag to support reading data from S3N [(github)](https://github.com/h2oai/h2o-dev/commit/b4efd2c9802a8e39bc5d24ea6593e420ecfbaea9)
- Added degrees of freedom to GLM metrics schemas [(github)](https://github.com/h2oai/h2o-dev/commit/6f153381b085e94358cc0e5e317d36dce3072131)
- Allow DL scoring_history to be unlimited in length [(github)](https://github.com/h2oai/h2o-dev/commit/5485b46d240415afa3ff3e7bc8a532791ae12419)
- add plotting for binomial models [(github)](https://github.com/h2oai/h2o-dev/commit/d332e98a12bcd40ceb9714067eefce64dad97125)
- Ignore certain parameters that are not applicable (class balancing, max CM size, etc.) [(github)](https://github.com/h2oai/h2o-dev/commit/5c70787a6e43697f57c0df918bb4cdbf93d18018)
- Updated glm scoring, fill training/validation metrics in model output [(github)](https://github.com/h2oai/h2o-dev/commit/9b3cc3ec2a8f81771e0eddaf663dbfd6690dbd04)
- Rename gbm loss parameter to distribution [(github)](https://github.com/h2oai/h2o-dev/commit/d9a1e9730f3296bc125965647e5aef2ae114368c)
- Fix GBM naming: loss -> distribution [(github)](https://github.com/h2oai/h2o-dev/commit/ef93923dc83f03a9ef16ed23bb1c411bd26e067e)
- GLM LBFGS update [(github)](https://github.com/h2oai/h2o-dev/commit/3c75a2edc20b7abc9a17b9732a0bac9c7f194feb)
- na.rm for quantile is default behavior [(github)](https://github.com/h2oai/h2o-dev/commit/3ac19b6f1cb7e2a64fa6b783a19e8ddb42713caf)
- GLM update: enabled `max_predictors` in REST, updated lbfgs [(github)](https://github.com/h2oai/h2o-dev/commit/a58d515364e749b1147452a98399eb8dfadd11af)
- Remove `keep_cross_validation_splits` for now from DL [(github)](https://github.com/h2oai/h2o-dev/commit/569ae442a4905a3dbbf47a3d5c03461ce68be36a)
- Get rid of sigma in the model metrics, instead show r2 [(github)](https://github.com/h2oai/h2o-dev/commit/b12bf9496a46f25f066f3bab512cd7d81795f0f4)
- Don't show `score_every_iteration` for DL [(github)](https://github.com/h2oai/h2o-dev/commit/089aedfed90ca30e715a58363c19f3f1fe47318c)
- Don't print too large confusion matrices in Tree models [(github)](https://github.com/h2oai/h2o-dev/commit/56d51f51e5fdc5f9f25d8838003236909637b272)

#####API

- publish h2o-model.jar via REST API [(PUBDEV-779)](https://0xdata.atlassian.net/browse/PUBDEV-779)
- move all schemas and endpoints to v3 [(PUBDEV-471)](https://0xdata.atlassian.net/browse/PUBDEV-471)
- clean up routes (remove AddToNavbar, fix /Quantiles, etc) [(PUBDEV-618)](https://0xdata.atlassian.net/browse/PUBDEV-618) [(github)](https://github.com/h2oai/h2o-dev/commit/7f6eff5b47aa1e273de4710a3b26408e3516f5af)
- More data in chunk_homes call. Add num_chunks_per_vec. Add num_vec. [(github)](https://github.com/h2oai/h2o-dev/commit/635d020b2dfc45364331903c282e82e3f20d028d)
- Added chunk_homes route for frames [(github)](https://github.com/h2oai/h2o-dev/commit/1ae94079762fdbfcdd1e39d65578752860c278c6)
- Update to use /3 routes [(github)](https://github.com/h2oai/h2o-dev/commit/be422ff963bb47daf9c8e7cbcb478e6a6dbbaea5)

#####Python

- Python client should check that version number == server version number [(PUBDEV-799)](https://0xdata.atlassian.net/browse/PUBDEV-799)
- Add asfactor for month [(github)](https://github.com/h2oai/h2o-dev/commit/43c9b82ab463e712910d1353013d499684021858) 
- in Expr.show() only show 10 or less rows. remove locate from runit test because full path used [(github)](https://github.com/h2oai/h2o-dev/commit/51f4f69deba9b76837b35bf2a0b85ee2e4b20db7)
- change nulls to () [(github)](https://github.com/h2oai/h2o-dev/commit/a138cc25edc9f948d263732f665d352e44ee39c1)
- sigma is no longer part of ModelMetricsRegressionV3 [(github)](https://github.com/h2oai/h2o-dev/commit/6f2a7390ce0feb0a3d880f1bb42168642a665bb0)


#####R

- Fix integer -> int in R [(github)](https://github.com/h2oai/h2o-dev/commit/ce05247e29b5756108999689d0b10fa17edb84a8)
- add autoencoder show method [(github)](https://github.com/h2oai/h2o-dev/commit/31d70f3ddb4bad63b42ec12c8fd70b9d5745a7d1)
- accessor is $ not @ [(github)](https://github.com/h2oai/h2o-dev/commit/a43e3d6924004e34aa7b5400d149c7dab26afe70)
- add `hit_ratio_table` and `varimp` calls to R [(github)](https://github.com/h2oai/h2o-dev/commit/caa7dc001edc63928ca7a8dadba773dd25983f1d)
- add h2o.predict as alternative [(github)](https://github.com/h2oai/h2o-dev/commit/e5a48f8faaededa3fd445d4b1415665c96f1291c)
- update model output in R [(github)](https://github.com/h2oai/h2o-dev/commit/e5d101ad60c12513f2e4c7b1d16534962eb86291)


#####System

- Port MissingValueInserter EndPoint to h2o-dev. [(PUBDEV-465)](https://0xdata.atlassian.net/browse/PUBDEV-465)
- Rapids: require a (put "key" %frame) [(PUBDEV-868)](https://0xdata.atlassian.net/browse/PUBDEV-868)
- Need pojo base model jar file embedded in h2o-dev via build process [(PUBDEV-780)](https://0xdata.atlassian.net/browse/PUBDEV-780) [(github)](https://github.com/h2oai/h2o-dev/commit/85f73202157f0ab4ee3487de8fc095951e761196)
- Make .json the default [(PUBDEV-619)](https://0xdata.atlassian.net/browse/PUBDEV-619) [(github)](https://github.com/h2oai/h2o-dev/commit/f3e88060da1a6af73940587c16fef669b1d5bbd5)
- Rename class for clarification [(github)](https://github.com/h2oai/h2o-dev/commit/89c4fe32d333940865112d8922249fc48eebe096)
- Classifies all NA columns as numeric. Also improves preview sampling accuracy by trimming partial lines at end of chunk. [(github)](https://github.com/h2oai/h2o-dev/commit/6b1cf7a180428c04cdd445974a318f5777c7f607)
- Implements sampling of files within the ParseSetup preview. This prevents poor column type guesses from only sampling the beginning of a file. [(github)](https://github.com/h2oai/h2o-dev/commit/038da7398941558656c1bda52b8429f4022c449e). 
- Rename fields `drop_na20_col` [(github)](https://github.com/h2oai/h2o-dev/commit/75131e9f1e6d1cd6788f239d72e11cf104028c3f)
- allow for many deletes as final statements in a block [(github)](https://github.com/h2oai/h2o-dev/commit/aa3e2d3ef00761ca4a4c942f33ffaf80951abc7b)
- rename initF -> init_f, dropNA20Cols -> drop_na20_cols [(github)](https://github.com/h2oai/h2o-dev/commit/e81eae78267d4981c74d866e40a48015d2086371)
- Removed tweedie param [(github)](https://github.com/h2oai/h2o-dev/commit/03902225aa912473ceb01e9cce045846949faecf)
- thresholds -> threshold [(github)](https://github.com/h2oai/h2o-dev/commit/69adcc8639c889b68ca0c97b7385a45c41d93401)
- JSON of TwoDimTable with all null values in the first column (no row headers) now doesn't have an empty column for of "" or nulls. [(github)](https://github.com/h2oai/h2o-dev/commit/de54085fe94aaa1e23aa74254fc5b8b64b85f76d)
- move H2O_Load, fix all the timezone functions [(github)](https://github.com/h2oai/h2o-dev/commit/871959887825aec1e246ae8e19e11d03db9637c5)
- Add extra verbose printout in case Frames don't match identically [(github)](https://github.com/h2oai/h2o-dev/commit/b8943f9228fe996887377f521ec135745d957033)
- allow delayed column lookup [(github)](https://github.com/h2oai/h2o-dev/commit/5060436d4d7ea7363dc74b9c0850258a38b2715a)
- add mixed type list [(github)](https://github.com/h2oai/h2o-dev/commit/99eb7106eadb0fcbe815752b181e085ba57349db)
- Added WaterMeterIo to count persist info [(github)](https://github.com/h2oai/h2o-dev/commit/2fa38aaff08584bcbf92ee2287343c2c40765d76)
- Remove special setChunkSize code in HDFS and NFS file vec [(github)](https://github.com/h2oai/h2o-dev/commit/136e7667a438a856ff06478b8ba7f6b716aced7b)
- add check for Frame on string parse [(github)](https://github.com/h2oai/h2o-dev/commit/f835768b080df1bc395bdbe0f60c2d35db8da0d8)
- Disable Memory Cleaner [(github)](https://github.com/h2oai/h2o-dev/commit/644f38f38c9f75a0008cb012c25c399a06805786)
- Handle '<' chars in Keys when swapping [(github)](https://github.com/h2oai/h2o-dev/commit/65e936912f236cacacd706bc30406f13b46acf7e)
- allow for colnames in slicing [(github)](https://github.com/h2oai/h2o-dev/commit/947e6cc1f0becb58a5d36387a6500b303293c6a8)
- Adjusts parse type detection. If column is all one string value, declare it an enum [(github)](https://github.com/h2oai/h2o-dev/commit/08e7845b786c445862554d4f4c5dac7c78204284)

#####Web UI

- nice algo names in the Flow dropdown (full word names) [(PUBDEV-707)](https://0xdata.atlassian.net/browse/PUBDEV-707)
- Compute and Display Hit Ratios [(PUBDEV-630)](https://0xdata.atlassian.net/browse/PUBDEV-630)
- Limit POJO preview to 1000 lines [(github)](https://github.com/h2oai/h2o-dev/commit/ce82fe74da9641d72c47dabd03514c7402998f76) 


####Bug Fixes


#####Algorithms

- GLM: lasso i.e alpha =1 seems to be giving wrong answers [(PUBDEV-769)](https://0xdata.atlassian.net/browse/PUBDEV-769)
- AUC: h2o reports .5 auc when actual auc is 1 [(PUBDEV-879)](https://0xdata.atlassian.net/browse/PUBDEV-879)
- h2o.glm: No output displayed for the model [(PUBDEV-858)](https://0xdata.atlassian.net/browse/PUBDEV-858)
- h2o.glm model object output needs a fix [(PUBDEV-815)](https://0xdata.atlassian.net/browse/PUBDEV-815)
- h2o.glm model object says : fill me in GLMModelOutputV2; I think I'm redundant [1] FALSE [(PUBDEV-765)](https://0xdata.atlassian.net/browse/PUBDEV-765)
- GLM : Build GLM Model => Java Assertion error [(PUBDEV-686)](https://0xdata.atlassian.net/browse/PUBDEV-686)
- GLM :=> Progress shows -100% [(PUBDEV-861)](https://0xdata.atlassian.net/browse/PUBDEV-861)
- GBM: Negative sign missing in initF value for ad dataset [(PUBDEV-880)](https://0xdata.atlassian.net/browse/PUBDEV-880)
- K-Means takes a validation set but doesn't use it [(PUBDEV-826)](https://0xdata.atlassian.net/browse/PUBDEV-826)
- Absolute_MCC is NaN (sometimes) [(PUBDEV-848)](https://0xdata.atlassian.net/browse/PUBDEV-848) [(github)](https://github.com/h2oai/h2o-dev/commit/4480f22b6b3a38abb776339bee506b356f589c90)
- GBM: A proper error msg should be thrown when the user sets the max depth =0 [(PUBDEV-838)](https://0xdata.atlassian.net/browse/PUBDEV-838) [(github)](https://github.com/h2oai/h2o-dev/commit/df77f3de5e8940f3598af67d520f185d1e478ec4)
- DRF Regression Assertion Error [(PUBDEV-824)](https://0xdata.atlassian.net/browse/PUBDEV-824)
- h2o.randomForest: if h2o is not returning the mse for the 0th tree then it should not be reported in the model object [(PUBDEV-811)](https://0xdata.atlassian.net/browse/PUBDEV-811)
- GBM: Got exception `class java.lang.AssertionError` with msg `null` java.lang.AssertionError at hex.tree.gbm.GBM$GBMDriver$GammaPass.map [(PUBDEV-693)](https://0xdata.atlassian.net/browse/PUBDEV-693)
- GBM: Got exception `class java.lang.AssertionError` with msg `null` java.lang.AssertionError at hex.ModelMetricsMultinomial$MetricBuildMultinomial.perRow [(HEXDEV-248)](https://0xdata.atlassian.net/browse/HEXDEV-248)
- GBM get java.lang.AssertionError: Coldata 2199.0 out of range C17:5086.0-19733.0 step=57.214844 nbins=256 isInt=1 [(HEXDEV-241)](https://0xdata.atlassian.net/browse/HEXDEV-241)
- GLM: glmnet objective function better than h2o.glm [(PUBDEV-749)](https://0xdata.atlassian.net/browse/PUBDEV-749)
- GLM: get AIOOB:-36 at hex.glm.GLMTask$GLMIterationTask.postGlobal(GLMTask.java:733) [(PUBDEV-894)](https://0xdata.atlassian.net/browse/PUBDEV-894) [(github)](https://github.com/h2oai/h2o-dev/commit/5bba2df2e208a0a7c7fd19732971575eb9dc2259)
- Fixed glm behavior in case no rows are left after filtering out NAs [(github)](https://github.com/h2oai/h2o-dev/commit/57dc0f3a168ed835c48aa29f6e0d6322c6a5523a)
- Fix memory leak in validation scoring in K-Means [(github)](https://github.com/h2oai/h2o-dev/commit/f3f01e4dfe66e0181df0ff85a2a9a108295df94c)

#####API

- API unification: DataFrame should be able to accept URI referencing file on local filesystem [(PUBDEV-709)](https://0xdata.atlassian.net/browse/PUBDEV-709) [(github)](https://github.com/h2oai/h2o-dev/commit/a72e77388c0f7b17e4595482f9afe42f14055ce9)


#####Python 

- Python: describe returning all zeros [(PUBDEV-875)](https://0xdata.atlassian.net/browse/PUBDEV-875)
- python/R & merge() [(PUBDEV-834)](https://0xdata.atlassian.net/browse/PUBDEV-834)
- python Expr min, max, median, sum bug [(PUBDEV-845)](https://0xdata.atlassian.net/browse/PUBDEV-845) [(github)](https://github.com/h2oai/h2o-dev/commit/7839efd5899366a3b51ef79156717a718ab01c38)




#####R

- (R and Python) clients must not pass response to DL AutoEncoder model builder [(PUBDEV-897)](https://0xdata.atlassian.net/browse/PUBDEV-897) [(github)](https://github.com/h2oai/h2o-dev/commit/bc78ecfa5e0c37cebd55ed9ba7b3ae6163ebdc66)
- h2o.varimp, h2o.hit_ratio_table missing in R [(PUBDEV-842)](https://0xdata.atlassian.net/browse/PUBDEV-842)
- GLM: No help for h2o.glm from R [(PUBDEV-732)](https://0xdata.atlassian.net/browse/PUBDEV-732)
- h2o.confusionMatrix not working for binary response [(PUBDEV-782)](https://0xdata.atlassian.net/browse/PUBDEV-782) [(github)](https://github.com/h2oai/h2o-dev/commit/a834cbc80a62062c55456233ce27ba5e9c3a87a3)
- h2o.splitframe complains about destination keys [(PUBDEV-783)](https://0xdata.atlassian.net/browse/PUBDEV-783)
- h2o.assign does not work [(PUBDEV-784)](https://0xdata.atlassian.net/browse/PUBDEV-784) [(github)](https://github.com/h2oai/h2o-dev/commit/b007c0b59dbb03716571384adb3271fbe8385a55)
- H2oR: should display only first few entries of the variable importance in model object [(PUBDEV-850)](https://0xdata.atlassian.net/browse/PUBDEV-850)
- R: h2o.confusion matrix needs formatting [(PUBDEV-764)](https://0xdata.atlassian.net/browse/PUBDEV-764)
- R: h2o.confusionMatrix => No Confusion Matrices for H2ORegressionMetrics [(PUBDEV-710)](https://0xdata.atlassian.net/browse/PUBDEV-710)
- h2o.deeplearning: model object output needs a fix [(PUBDEV-821)](https://0xdata.atlassian.net/browse/PUBDEV-821)
- h2o.varimp, h2o.hit_ratio_table missing in R [(PUBDEV-842)](https://0xdata.atlassian.net/browse/PUBDEV-842) 
- force gc more frequently [(github)](https://github.com/h2oai/h2o-dev/commit/0db9a3716ecf573ef4b3c71ec1116cc8b27e62c6)

#####System

- MapR FS loads are too slow [(PUBDEV-927)](https://0xdata.atlassian.net/browse/PUBDEV-927)
- ensure that HDFS works from Windows [(PUBDEV-812)](https://0xdata.atlassian.net/browse/PUBDEV-812)
- Summary: on a time column throws,'null' is not an object (evaluating 'column.domain[level.index]') in Flow [(PUBDEV-867)](https://0xdata.atlassian.net/browse/PUBDEV-867)
- Parse: An enum column gets parsed as int for the attached file [(PUBDEV-606)](https://0xdata.atlassian.net/browse/PUBDEV-606)
- Parse => 40Mx1_uniques => class java.lang.RuntimeException [(PUBDEV-729)](https://0xdata.atlassian.net/browse/PUBDEV-729)
- if there are fewer than 5 unique values in a dataset column, mins/maxs reports e+308 values [(PUBDEV-150)](https://0xdata.atlassian.net/browse/PUBDEV-150) [(github)](https://github.com/h2oai/h2o-dev/commit/49c966791a146687039350689bc09cee10f38820)
- Sparkling water - `DataFrame[T_UUID]` to `SchemaRDD[StringType]` [(PUDEV-771)](https://0xdata.atlassian.net/browse/PUBDEV-771) 
- Sparkling water - `DataFrame[T_NUM(Long)]` to `SchemaRDD[LongType]` [(PUBDEV-767)](https://0xdata.atlassian.net/browse/PUBDEV-767)
- Sparkling water - `DataFrame[T_ENUM]` to `SchemaRDD[StringType]` [(PUBDEV-766)](https://0xdata.atlassian.net/browse/PUBDEV-766)
- Inconsistency in row and col slicing [(HEXDEV-265)](https://0xdata.atlassian.net/browse/HEXDEV-265) [(github)](https://github.com/h2oai/h2o-dev/commit/edd8923a438282e3c24d086e1a03b88471d58114)
- rep_len expects literal length only [(HEXDEV-268)](https://0xdata.atlassian.net/browse/HEXDEV-268) [(github)](https://github.com/h2oai/h2o-dev/commit/1783a889a54d2b23da8bd8ec42774f52efbebc60)
- cbind and = don't work within a single rapids block [(HEXDEV-237)](https://0xdata.atlassian.net/browse/HEXDEV-237)
- Rapids response for c(value) does not have frame key [(HEXDEV-252)](https://0xdata.atlassian.net/browse/HEXDEV-252)
- S3 parse takes forever [(PUBDEV-876)](https://0xdata.atlassian.net/browse/PUBDEV-876)
- Parse => Enum unification fails in multi-node parse [(PUBDEV-718)](https://0xdata.atlassian.net/browse/PUBDEV-718) [(github)](https://github.com/h2oai/h2o-dev/commit/0db8c392070583f32849447b65784da18197c14d)
- All nodes are not getting updated with latest status of each other nodes info [(PUBDEV-768)](https://0xdata.atlassian.net/browse/PUBDEV-768)
- Cluster creation is sometimes rejecting new nodes (post jenkins-master-1128+) [(PUBDEV-807)](https://0xdata.atlassian.net/browse/PUBDEV-807)
- Parse => Multiple files 1 zip/ 1 csv gives Array index out of bounds [(PUBDEV-840)](https://0xdata.atlassian.net/browse/PUBDEV-840)
- Parse => failed for X5MRows6KCols ==> OOM => Cluster dies [(PUBDEV-836)](https://0xdata.atlassian.net/browse/PUBDEV-836)
- /frame/foo pagination weirded out [(HEXDEV-277)](https://0xdata.atlassian.net/browse/HEXDEV-277) [(github)](https://github.com/h2oai/h2o-dev/commit/c40da923d97720466fb372758d66509aa628e97c)
- Removed code that flipped enums to strings [(github)](https://github.com/h2oai/h2o-dev/commit/7d56bcee73cf3c90b498cadf8601610e5f145dbc)




#####Web UI

- Flow: It would be really useful to have the mse plots back in GBM [(PUBDEV-889)](https://0xdata.atlassian.net/browse/PUBDEV-889)
- State change in Flow is not fully validated [(PUBDEV-919)](https://0xdata.atlassian.net/browse/PUBDEV-919)
- Flows : Not able to load saved flows from hdfs [(PUBDEV-872)](https://0xdata.atlassian.net/browse/PUBDEV-872)
- Save Function in Flow crashes [(PUBDEV-791)](https://0xdata.atlassian.net/browse/PUBDEV-791) [(github)](https://github.com/h2oai/h2o-dev/commit/ad724bf7af86180d7045a99790602bd52908945f)
- Flow: should throw a proper error msg when user supplied response have more categories than algo can handle [(PUBDEV-866)](https://0xdata.atlassian.net/browse/PUBDEV-866)
- Flow display of a summary of a column with all missing values fails. [(HEXDEV-230)](https://0xdata.atlassian.net/browse/HEXDEV-230)
- Split frame UI improvements [(HEXDEV-275)](https://0xdata.atlassian.net/browse/HEXDEV-275)
- Flow : Decimal point precisions to be consistent to 4 as in h2o1 [(PUBDEV-844)](https://0xdata.atlassian.net/browse/PUBDEV-844)
- Flow: Prediction frame is outputing junk info [(PUBDEV-825)](https://0xdata.atlassian.net/browse/PUBDEV-825)
- EC2 => Cluster of 16 nodes => Water Meter => shows blank page [(PUBDEV-831)](https://0xdata.atlassian.net/browse/PUBDEV-831)
- Flow: Predict - "undefined is not an object (evaluating `prediction.thresholds_and_metric_scores.name`) [(PUBDEV-559)](https://0xdata.atlassian.net/browse/PUBDEV-559)
- Flow: inspect getModel for PCA returns error [(PUBDEV-610)](https://0xdata.atlassian.net/browse/PUBDEV-610)
- Flow, RF: Can't get Predict results; "undefined is not an object (evaluating `prediction.confusion_matrices.length`)" [(PUBDEV-695)](https://0xdata.atlassian.net/browse/PUBDEV-695)
- Flow, GBM: getModel is broken -Error processing GET /3/Models.json/gbm-b1641e2dc3-4bad-9f69-a5f4b67051ba null is not an object (evaluating `source.length`) [(PUBDEV-800)](https://0xdata.atlassian.net/browse/PUBDEV-800) 






###Severi (0.2.2.1) - 4/10/15

####New Features 

#####R

- Implement /3/Frames/<my_frame>/summary [(PUBDEV-6)](https://0xdata.atlassian.net/browse/PUBDEV-6) [(github)](https://github.com/h2oai/h2o-dev/commit/07bc295e1687d88e40d8391ea78f91aff4183a6f)
- add allparameters slot to allow default values to be shown [(github)](https://github.com/h2oai/h2o-dev/commit/9699a4c43ce4936dbc3019c75b2a36bd1ef22b45)
- add log loss accessor [(github)](https://github.com/h2oai/h2o-dev/commit/22ace748ae4004305ae9edb04f17141d0dbd87d4)


####Enhancements

#####Algorithms

- POJO generation: GBM [(PUBDEV-713)](https://0xdata.atlassian.net/browse/PUBDEV-713)
- POJO generation: DRF [(PUBDEV-714)](https://0xdata.atlassian.net/browse/PUBDEV-714)
- Compute and Display Hit Ratios [(PUBDEV-630)](https://0xdata.atlassian.net/browse/PUBDEV-630) [(github)](https://github.com/h2oai/h2o-dev/commit/04b13f2fb05b752dbd04121f50845bebcb6f9955)
- Add DL POJO scoring [(PUBDEV-585)](https://0xdata.atlassian.net/browse/PUBDEV-585)
- Allow validation dataset for AutoEncoder [(PUDEV-581)](https://0xdata.atlassian.net/browse/PUBDEV-581)
- PUBDEV-580: Add log loss to binomial and multinomial model metric [(github)](https://github.com/h2oai/h2o-dev/commit/8982a0a1ba575bd5ca6ca3e854382e03146743cd)
- Port MissingValueInserter EndPoint to h2o-dev [(PUBDEV-465)](https://0xdata.atlassian.net/browse/PUBDEV-465)
- increase tolerance to 2e-3 (was 1e-3 ..failed with 0.001647 relative difference [(github)](https://github.com/h2oai/h2o-dev/commit/9ce26530cc7d4d4aef55b5e0debc978bacc8ac78)
- change tolerance to 1e-3 [(github)](https://github.com/h2oai/h2o-dev/commit/bb5aa7806d37e1148029ef848a8df0d7a28cba2a)
- Add option to export weights and biases to REST API / Flow. [(github)](https://github.com/h2oai/h2o-dev/commit/2f711045f2678622a7d6d44f7210adb74a513ce6)
- Add scree plot for H2O PCA models and fix Runit test. [(github)](https://github.com/h2oai/h2o-dev/commit/5743019075e023590019fab9a4da8c09500643a0)
- Remove quantiles from the model builders list. [(github)](https://github.com/h2oai/h2o-dev/commit/6283dfbc626cb2b9a65df2f4b90a87371ef5c752)
- GLM update: added row filtering argument to line search task, fixed issues with dfork/asyncExec [(github)](https://github.com/h2oai/h2o-dev/commit/7492ed95915a85121f0042b5800d58bda2805a87)
- Updated rho-setting in GLM. [(github)](https://github.com/h2oai/h2o-dev/commit/a130fd6abbd13fff44e0eb813d31cc04afcedef7)
- No threshold 0.5; use the default (max F1) instead [(github)](https://github.com/h2oai/h2o-dev/commit/e56425d6f83aa0e1dc523acc3ed4b5a49d0223fc)
- GLM update: updated initilization, NA row filtering, default lambda is now empty, will be picked based on the fraction of lambda_max. [(github)](https://github.com/h2oai/h2o-dev/commit/04a3f8e496c00de9e35c8ee33a6d3ddb8466a3d8)
- Updated ADMM solver. [(github)](https://github.com/h2oai/h2o-dev/commit/1a6ef44a24463b2538731065fc39eef4531e062e)
- Added makeGLMModel call. [(github)](https://github.com/h2oai/h2o-dev/commit/9792ff032356982915d814c7918c48582bf3ffea)
- Start with classification error NaN at t=0 for DL, not with 1. [(github)](https://github.com/h2oai/h2o-dev/commit/c33ca1f385844c90c473fe2941bbb8b2c2ab663f)
- Relax DL POJO relative tolerance to 1e-2. [(github)](https://github.com/h2oai/h2o-dev/commit/f7a2fe37845c00980a23f8e68b34ad044fa647e2)
- Override nfeatures() method in DLModelOutput. [(github)](https://github.com/h2oai/h2o-dev/commit/7c6bcf844c8e162b8fb16ee1f7e208717b82d606)
- Renaming of fields in GLM [(github)](https://github.com/h2oai/h2o-dev/commit/d21180ab5ea973848d4cdcb896c32400c3d77d38)
- GLM: Take out Balance Classes [(PUBDEV-795)](https://0xdata.atlassian.net/browse/PUBDEV-795)



#####API

- schema metadata for Map fields should include the key and value types [(PUBDEV-753)](https://0xdata.atlassian.net/browse/PUBDEV-753) [(github)](https://github.com/h2oai/h2o-dev/commit/4b55db36f259740043b8418e23e298fb0ed5a43d)
- schema metadata should include the superclass [(PUBDEV-754)](https://0xdata.atlassian.net/browse/PUBDEV-754)
- rest api naming convention: n_folds vs ntrees [(PUBDEV-737)](https://0xdata.atlassian.net/browse/PUBDEV-737)
- schema metadata for Map fields should include the key and value types [(PUBDEV-753)](https://0xdata.atlassian.net/browse/PUBDEV-753)
- Create REST Endpoint for exposing .java pojo models [(PUBDEV-778)](https://0xdata.atlassian.net/browse/PUBDEV-778)






#####Python

- Run GLM from Python (including LBFGS) [(HEXDEV-92)](https://0xdata.atlassian.net/browse/HEXDEV-92)
- added H2OFrame show(), as_list(), and slicing pyunits [(github)](https://github.com/h2oai/h2o-dev/commit/b1febc33faa336924ffdb416d8d4a3cb8bba37fa)
- changed solver parameter to "L_BFGS" [(github)](https://github.com/h2oai/h2o-dev/commit/93e71509bcfa0e76d344819214a08b944ccbfb89)
- added multidimensional slicing of H2OFrames and Exprs. [(github)](https://github.com/h2oai/h2o-dev/commit/7d9be09ff0b68f92e46a0c7336dcf8134d026b88)
- add h2o.groupby to python interface [(github)](https://github.com/h2oai/h2o-dev/commit/aee9522f0c7edbd960ded78f5ba01daf6d54925b)
- added H2OModel.confusionMatrix() to return confusion matrix of a prediction [(github)](https://github.com/h2oai/h2o-dev/commit/6e6bc378f3a10c094752470de786be600a0a98b3)





#####R

- PUBDEV-578, PUBDEV-541, PUBDEV-566.
	-R client now sends the data frame column names and data types to ParseSetup.
	-R client can get column names from a parsed frame or a list.
	-Respects client request for column data types [(github)](https://github.com/h2oai/h2o-dev/commit/ba063be25d3fbb658b016ff514083284e2d95d78)
- R: Cannot create new columns through R [(PUBDEV-571)](https://0xdata.atlassian.net/browse/PUBDEV-571)
- H2O-R: it would be more useful if h2o.confusion matrix reports the actual class labels instead of [,1] and [,2] [(PUBDEV-553)](https://0xdata.atlassian.net/browse/PUBDEV-553)
- Support both multinomial and binomial CM [(github)](https://github.com/h2oai/h2o-dev/commit/4ad2ed007635a7e8c2fd4fb0ae985cf00a81df15)



#####System

- Flow: Standardize `max_iters`/`max_iterations` parameters [(PUBDEV-447)](https://0xdata.atlassian.net/browse/PUBDEV-447) [(github)](https://github.com/h2oai/h2o-dev/commit/6586f1f2f233518a7ee6179ec2bc19d9d7b61d15)
- Add ERROR logging level for too-many-retries case [(PUBDEV-146)](https://0xdata.atlassian.net/browse/PUBDEV-146) [(github)](https://github.com/h2oai/h2o-dev/commit/ae5bdf26453643b58403a6a4fb136259ac9acd6b)
- Simplify checking of cluster health. Just report the status immediately. [(github)](https://github.com/h2oai/h2o-dev/commit/25fde3914460e7572cf3500f236d43e50a502aab)
- reduce timeout [(github)](https://github.com/h2oai/h2o-dev/commit/4c93ddfd92801fdef60961d44ccb7cf512f37a90)
- strings can have ' or " beginning [(github)](https://github.com/h2oai/h2o-dev/commit/034243f094ae67fb15e8d575146f6e64c8727d39)
- Throw a validation error in flow if any training data cols are non-numeric [(github)](https://github.com/h2oai/h2o-dev/commit/091c18331f19a5a1db8b3eb0b000ca72abd29f81)
- Add getHdfsHomeDirectory(). [(github)](https://github.com/h2oai/h2o-dev/commit/68c3f730576c21bd1191f8af9dd7fd9445b89f83)
- Added --verbose.  [(github)](https://github.com/h2oai/h2o-dev/commit/5e772f8314a340666e4e80b3480b2105ceb91251)


#####Web UI

- PUBDEV-707: nice algo names in the Flow dropdown (full word names) [(github)](https://github.com/h2oai/h2o-dev/commit/ab87c26ae8ac17691034f4d9014ee17ba2168d89)
- Unbreak Flow's ConfusionMatrix display. [(github)](https://github.com/h2oai/h2o-dev/commit/45911f2ff28e2357d5545ac23135f090c10f13e0)
- POJO generation: DL [(PUBDEV-715)](https://0xdata.atlassian.net/browse/PUBDEV-715)



####Bug Fixes


#####Algorithms

- GLM : Build GLM model with nfolds brings down the cloud => FATAL: unimplemented [(PUBDEV-731)](https://0xdata.atlassian.net/browse/PUBDEV-731) [(github)](https://github.com/h2oai/h2o-dev/commit/79123971fdea5660355f57de4e9a02d3712250b1)
- DL : Build DL Model => FATAL: unimplemented: n_folds >= 2 is not (yet) implemented => SHUTSDOWN CLOUD [(PUBDEV-727)](https://0xdata.atlassian.net/browse/PUBDEV-727) [(github)](https://github.com/h2oai/h2o-dev/commit/6f59755f28c3fc3cee549630bb5e22a985d185ab)
- GBM => Build GBM model => No enum constant  hex.tree.gbm.GBMModel.GBMParameters.Family.AUTO [(PUBDEV-723)](https://0xdata.atlassian.net/browse/PUBDEV-723)
- GBM: When run with loss = auto with a numeric column get- error :No enum constant hex.tree.gbm.GBMModel.GBMParameters.Family.AUTO
 [(PUBDEV-708)](https://0xdata.atlassian.net/browse/PUBDEV-708) [(github)](https://github.com/h2oai/h2o-dev/commit/15d5b5a6108d165f230a856aa3c38a4eb158ee93)
- gbm: does not complain when min_row >dataset size [(PUBDEV-694)](https://0xdata.atlassian.net/browse/PUBDEV-694) [(github)](https://github.com/h2oai/h2o-dev/commit/a3d9d1cca2aa070c536084ca1bb90eecfbf609e7)
- GLM: reports wrong residual degrees of freedom [(PUBDEV-668)](https://0xdata.atlassian.net/browse/PUBDEV-668) 
- H2O dev reports less accurate aucs than H2O [(PUBDEV-602)](https://0xdata.atlassian.net/browse/PUBDEV-602)
- GLM : Build GLM model fails => ArrayIndexOutOfBoundsException [(PUBDEV-601)](https://0xdata.atlassian.net/browse/PUBDEV-601)
- divide by zero in modelmetrics for deep learning [(PUBDEV-568)](https://0xdata.atlassian.net/browse/PUBDEV-568)
- GBM: reports 0th tree mse value for the validation set, different than the train set ,When only train sets is provided [(PUDEV-561)](https://0xdata.atlassian.net/browse/PUBDEV-561)
- GBM: Initial mse in bernoulli seems to be off [(PUBDEV-515)](https://0xdata.atlassian.net/browse/PUBDEV-515) 
- GLM : Build Model fails with Array Index Out of Bound exception [(PUBDEV-454)](https://0xdata.atlassian.net/browse/PUBDEV-454) [(github)](https://github.com/h2oai/h2o-dev/commit/78773be9f40e1403457e42378baf0d1aeaf3e32d)
- Custom Functions don't work in apply() in R [(PUBDEV-436)](https://0xdata.atlassian.net/browse/PUBDEV-436)
- GLM failure: got NaNs and/or Infs in beta on airlines [(PUBDEV-362)](https://0xdata.atlassian.net/browse/PUBDEV-362)
- MetricBuilderMultinomial.perRow AssertionError while running GBM [(HEXDEV-240)](https://0xdata.atlassian.net/browse/HEXDEV-240)
- Problems during Train/Test adaptation between Enum/Numeric [(HEXDEV-229)](https://0xdata.atlassian.net/browse/HEXDEV-229)
- DRF/GBM balance_classes=True throws unimplemented exception [(HEXDEV-226)](https://0xdata.atlassian.net/browse/HEXDEV-226) [(github)](https://github.com/h2oai/h2o-dev/commit/3a4f7ee3fdb159187b5ae1789d55752192d893e6)
- AUC reported on training data is 0, but should be 1 [(HEXDEV-223)](https://0xdata.atlassian.net/browse/HEXDEV-223) [(github)](https://github.com/h2oai/h2o-dev/commit/312558524749a0b28bf22ffd8c34ebcd6996b350)
- glm pyunit intermittent failure [(HEXDEV-199)](https://0xdata.atlassian.net/browse/HEXDEV-199)
- Inconsistency in GBM results:Gives different results even when run with the same set of params [(HEXDEV-194)](https://0xdata.atlassian.net/browse/HEXDEV-194)
- get rid of nfolds= param since it's not supported in GLM yet [(￼github)](https://github.com/h2oai/h2o-dev/commit/8603ad35d4243ef598acadbfaa084c6852acd7ce)
- Fixed degrees of freedom (off by 1) in glm, added test. [(github)](https://github.com/h2oai/h2o-dev/commit/09e6d6f5222c40cb73f28c6df4e30d92b98f8361)
- GLM fix: fix filtering of rows with NAs and fix in sparse handling. [(github)](https://github.com/h2oai/h2o-dev/commit/5bad9b5c7bc2a3a4d4a2496ade7194a0438f17d9)
- Fix GLM job fail path to call Job.fail(). [(github)](https://github.com/h2oai/h2o-dev/commit/912663fb0e05b4670d014a0a4c7bff03410c467e)
- Full AUC computation, bug fixes [(github)](https://github.com/h2oai/h2o-dev/commit/9124cc321defb0b4defba7bef02cf387ff238c28)
- Fix ADMM for upper/lower bounds. (updated rho settings + update u-vector in ADMM for intercept) [(github)](https://github.com/h2oai/h2o-dev/commit/47a09ffe2271db050bd6d8042dfeaa40c4874b8a)
- Few glm fixes [(github)](https://github.com/h2oai/h2o-dev/commit/04a344ebede1f34b58e9aa82889bac1af9bd5f47)
- DL : KDD Algebra data set => Build DL model => ArrayIndexOutOfBoundsException [(PUBDEV-696)](https://0xdata.atlassian.net/browse/PUBDEV-696)
- GBm: Dev vs H2O for depth 5, minrow=10, on prostate, give different trees [(PUBDEV-759)](https://0xdata.atlassian.net/browse/PUBDEV-759)
- GBM param min_rows doesn't throw exception for negative values [(PUBDEV-697)](https://0xdata.atlassian.net/browse/PUBDEV-697)
- GBM : Build GBM Model => Too many levels in response column! (java.lang.IllegalArgumentException) => Should display proper error message [(PUBDEV-698)](https://0xdata.atlassian.net/browse/PUBDEV-698)
- GBM:Got exception 'class java.lang.AssertionError', with msg 'Something is wrong with GBM trees since returned prediction is Infinity [(PUBDEV-722)](https://0xdata.atlassian.net/browse/PUBDEV-722)






#####API

- Cannot adapt numeric response to factors made from numbers [(PUBDEV-620)](https://0xdata.atlassian.net/browse/PUBDEV-620)
- not specifying response\_column gets NPE (deep learning build_model()) I think other algos might have same thing [(PUBDEV-131)](https://0xdata.atlassian.net/browse/PUBDEV-131)
- NPE response has null msg, exception\_msg and dev\_msg [(HEXDEV-225)](https://0xdata.atlassian.net/browse/HEXDEV-225)
- Flow :=> Save Flow => On Mac and Windows 8.1 => NodePersistentStorage failure while attempting to overwrite (?) a flow [(HEXDEV-202)](https://0xdata.atlassian.net/browse/HEXDEV-202) [(github)](https://github.com/h2oai/h2o-dev/commit/db710a4dc7dda4570f5b87cb9e386be6c76f001e)
- the can_build field in ModelBuilderSchema needs values[] to be set [(PUBDEV-755)](https://0xdata.atlassian.net/browse/PUBDEV-755)
- value field in the field metadata isn't getting serialized as its native type [(PUBDEV-756)](https://0xdata.atlassian.net/browse/PUBDEV-756)


#####Python
- python api asfactor() on -1/1 column issue [(HEXDEV-203)](https://0xdata.atlassian.net/browse/HEXDEV-203)


#####R
- Rapids: Operations %/% and %% returns Illegal Argument Exception in R [(PUBDEV-736)](https://0xdata.atlassian.net/browse/PUBDEV-736)
- quantile: H2oR displays wrong quantile values when call the default quantile without specifying the probs [(PUBDEV-689)](https://0xdata.atlassian.net/browse/PUBDEV-689)[(github)](https://github.com/h2oai/h2o-dev/commit/9ef5e2befe08a5ff7ce13e8b4b39acf7171e8a1f)
- as.factor: If a user reruns as.factor on an already factor column, h2o should not show an exception [(PUBDEV-622)](https://0xdata.atlassian.net/browse/PUBDEV-622)
- as.factor works only on positive integers [(PUBDEV-617)](https://0xdata.atlassian.net/browse/PUBDEV-617) [(github)](https://github.com/h2oai/h2o-dev/commit/08f3acb62bec0f2c3808841d6b7f8d1382f616f0)
- H2O-R: model detail lists three mses, the first MSE slot does not contain any info about the model and hence, should be removed from the model details [(PUBDEV-605)](https://0xdata.atlassian.net/browse/PUBDEV-605) [(github)](https://github.com/h2oai/h2o-dev/commit/55f975d551432114a0088d19bd2397894410dd94)
- H2O-R: Strings: While slicing get Error From H2O: water.DException$DistributedException [(PUBDEV-592)](https://0xdata.atlassian.net/browse/PUBDEV-592)
- R: h2o.confusionMatrix should handle both models and model metric objects [(PUBDEV-590)](https://0xdata.atlassian.net/browse/PUBDEV-590)
- R: as.Date not functional with H2O objects [(PUBDEV-583)](https://0xdata.atlassian.net/browse/PUBDEV-583) [(github)](https://github.com/h2oai/h2o-dev/commit/f2f64b1ed29c8d7ab47252d84d8634240b3889d0)
- R: some apply functions don't work on H2OFrame objects [(PUBDEV-579)](https://0xdata.atlassian.net/browse/PUBDEV-579) [(github)](https://github.com/h2oai/h2o-dev/commit/10f1245dbbc5ac36024e8ce51932dd991ff50688)
- h2o.confusionMatrices for multinomial does not work [(PUBDEV-577)](https://0xdata.atlassian.net/browse/PUBDEV-577)
- R: slicing issues [(PUBDEV-573)](https://0xdata.atlassian.net/browse/PUBDEV-573)
- R: length and is.factor don't work in h2o.ddply [(PUBDEV-572)](https://0xdata.atlassian.net/browse/PUBDEV-572) [(github)](https://github.com/h2oai/h2o-dev/commit/bdc55a95a91af784a8b4497bbc8e4835fa1049bf)
- R: apply(hex, c(1,2), ...) doesn't properly raise an error [(PUBDEV-570)](https://0xdata.atlassian.net/browse/PUBDEV-570) [(github)](https://github.com/h2oai/h2o-dev/commit/75ddf7f82b4acabe77d0928b66ea7a51dbc5a8b4)
- R: Slicing negative indices to negative indices fails [(PUBDEV-569)](https://0xdata.atlassian.net/browse/PUBDEV-569) [(github)](https://github.com/h2oai/h2o-dev/commit/bf6620f70a3f09a8a57d2da563188c342d67aeb7)
- h2o.ddply: doesn't accept anonymous functions [(PUBDEV-567)](https://0xdata.atlassian.net/browse/PUBDEV-567) [(github)](https://github.com/h2oai/h2o-dev/commit/3c3c4e7134fe03e5a8a5cdd8530f59094264b7f3)
- ifelse() cannot return H2OFrames in R [(PUBDEV-543)](https://0xdata.atlassian.net/browse/PUBDEV-543)
- as.h2o loses track of headers [(PUBDEV-541)](https://0xdata.atlassian.net/browse/PUBDEV-541)
- H2O-R not showing meaningful error msg [(PUBDEV-502)](https://0xdata.atlassian.net/browse/PUBDEV-502)
- H2O.fail() had better fail [(PUBDEV-470)](https://0xdata.atlassian.net/browse/PUBDEV-470) [(github)](https://github.com/h2oai/h2o-dev/commit/16939a831a315c5f7ec221bc15fad5826fd4c677)
- fix issue in toEnum [(github)](https://github.com/h2oai/h2o-dev/commit/99fe517a00f54dea9ca4e64054c06a6e8cd1ea8c)
- fix colnames and new col creation [(github)](https://github.com/h2oai/h2o-dev/commit/61000a75eaa3b9a92dced1c66ecdce687cef64b2)
- R: h2o.init() is posting warning messages of an unhealthy cluster when the cluster is fine. [(PUBDEV-734)](https://0xdata.atlassian.net/browse/PUBDEV-734)
- h2o.split frame is failing [(PUBDEV-560)](https://0xdata.atlassian.net/browse/PUBDEV-560)





#####System

- key type failure should fail the request, not the cloud [(PUBDEV-739)](https://0xdata.atlassian.net/browse/PUBDEV-739) [(github)](https://github.com/h2oai/h2o-dev/commit/52ebdf0cd6d972acb15c8cf315e2d1105c5b1703)
- Parse => Import Medicare supplier file => Parse = > Illegal argument for field: column_names of schema: ParseV2: string and key arrays' values must be quoted, but the client sent: " [(PUBDEV-719)](https://0xdata.atlassian.net/browse/PUBDEV-719)
- Overwriting a constant vector with strings fails [(PUBDEV-702)](https://0xdata.atlassian.net/browse/PUBDEV-702)
- H2O - gets stuck while calculating quantile,no error msg, just keeps running a job that normally takes less than a sec [(PUBDEV-685)](https://0xdata.atlassian.net/browse/PUBDEV-685)
- Summary and quantile on a column with all missing values should not throw an exception [(PUBDEV-673)](https://0xdata.atlassian.net/browse/PUBDEV-673) [(github)](https://github.com/h2oai/h2o-dev/commit/7acd14a7d6bbdfa5ab6a7c2e8c2987622b229603)
- View Logs => class java.lang.RuntimeException: java.lang.IllegalArgumentException: File /home2/hdp/yarn/usercache/neeraja/appcache/application_1427144101512_0039/h2ologs/h2o_172.16.2.185_54321-3-info.log does not exist [(PUBDEV-600)](https://0xdata.atlassian.net/browse/PUBDEV-600)
- Parse: After parsing Chicago crime dataset => Not able to build models or Get frames [(PUBDEV-576)](https://0xdata.atlassian.net/browse/PUBDEV-576)
- Parse: Numbers completely parsed wrong [(PUBDEV-574)](https://0xdata.atlassian.net/browse/PUBDEV-574)
- Flow: converting a column to enum while parsing does not work [(PUBDEV-566)](https://0xdata.atlassian.net/browse/PUBDEV-566)
- Parse: Fail gracefully when asked to parse a zip file with different files in it [(PUBDEV-540)](https://0xdata.atlassian.net/browse/PUBDEV-540)[(github)](https://github.com/h2oai/h2o-dev/commit/23a60d68e9d77fe07ae9d940b0ebb6636ef40ee3)
- toDataFrame doesn't support sequence format schema (array, vectorUDT) [(PUBDEV-457)](https://0xdata.atlassian.net/browse/PUBDEV-457)
- Parse : Parsing random crap gives java.lang.ArrayIndexOutOfBoundsException: 13 [(PUBDEV-428)](https://0xdata.atlassian.net/browse/PUBDEV-428)
- The quote stripper for column names should report when the stripped chars are not the expected quotes [(￼PUBDEV-424)](https://0xdata.atlassian.net/browse/PUBDEV-424)
- import directory with large files,then Frames..really slow and disk grinds. Files are unparsed. Shouldn't be grinding [(PUBDEV-98)](https://0xdata.atlassian.net/browse/PUBDEV-98)
- NodePersistentStorage gets wiped out when hadoop cluster is restarted [(HEXDEV-185)](https://0xdata.atlassian.net/browse/HEXDEV-185)
- h2o.exec won't be supported [(github)](https://github.com/h2oai/h2o-dev/commit/81f685e5abb990d7f7669b137cfb07d7b01ea471)
- fixed import issue [(github)](https://github.com/h2oai/h2o-dev/commit/addf5b85b91b77366bca0a8c900ca2d308f29a09)
- fixed init param [(github)](https://github.com/h2oai/h2o-dev/commit/d459d1a7fb405f8a1f7b466caae99281feae370c)
- fix repeat as.factor NPE [(github)](https://github.com/h2oai/h2o-dev/commit/49fb24417ecfe26975fbff14bef084da50a034c7)
- startH2O set to False in init [(github)](https://github.com/h2oai/h2o-dev/commit/53ca9baf1bd70cd04b2ad03243eb9c7053300c52)
- hang on glm job removal [(PUBDEV-726)](https://0xdata.atlassian.net/browse/PUBDEV-726)
- Flow - changed column types need to be reflected in parsed data [(HEXDEV-189)](https://0xdata.atlassian.net/browse/HEXDEV-189)
- water.DException$DistributedException while running kmeans in multinode cluster [(PUBDEV-691)](https://0xdata.atlassian.net/browse/PUBDEV-691)
- Frame inspection prior to file parsing, corrupts parsing [(PUBDEV-425)](https://0xdata.atlassian.net/browse/PUBDEV-425)






#####Web UI

- Flow, DL: Need better fail message if "Autoencoder" and "use_all_factor_levels" are both selected [(PUBDEV-724)](https://0xdata.atlassian.net/browse/PUBDEV-724)
- When select AUTO while building a gbm model get ERROR FETCHING INITIAL MODEL BUILDER STATE [(PUBDEV-595)](https://0xdata.atlassian.net/browse/PUBDEV-595)
- Flow : Build h2o-dev-0.1.17.1009 : Building GLM model gives java.lang.ArrayIndexOutOfBoundsException: [(￼PUBDEV-205](https://0xdata.atlassian.net/browse/PUBDEV-205) [(￼github)](https://github.com/h2oai/h2o-dev/commit/fe3cdad806750f6add0fc4c03bee9e66d61c59fa)
- Flow:Summary on flow broken for a long time [(PUBDEV-785)](https://0xdata.atlassian.net/browse/PUBDEV-785)

---

### Serre (0.2.1.1) - 3/18/15

####New Features


#####Algorithms
- Naive Bayes in H2O-dev [(PUBDEV-158)](https://0xdata.atlassian.net/browse/PUBDEV-158)
- GLM model output, details from R [(HEXDEV-94)](https://0xdata.atlassian.net/browse/HEXDEV-94)
- Run GLM Regression from Flow (including LBFGS) [(HEXDEV-110)](https://0xdata.atlassian.net/browse/HEXDEV-110)
- PCA [(PUBDEV-157)](https://0xdata.atlassian.net/browse/PUBDEV-157)
- Port Random Forest to h2o-dev [(PUBDEV-455)](https://0xdata.atlassian.net/browse/PUBDEV-455)
- Enable DRF model output [(github)](https://github.com/h2oai/h2o-flow/commit/44ee1bf98dd69f33251a7a959b1000cc7f290427)
- Add DRF to Flow (Model Output) [(PUBDEV-533)](https://0xdata.atlassian.net/browse/PUBDEV-533)
- Grid for GBM [(github)](https://github.com/h2oai/h2o-dev/commit/ce96d2859aa86e4df393a13e00fbb7fcf603c166)
- Run Deep Learning Regression from Flow [(HEXDEV-109)](https://0xdata.atlassian.net/browse/HEXDEV-109)

#####Python
- Add Python wrapper for DRF [(PUBDEV-534)](https://0xdata.atlassian.net/browse/PUBDEV-534)


#####R
- Add R wrapper for DRF [(PUBDEV-530)](https://0xdata.atlassian.net/browse/PUBDEV-530)


#####System
- Include uploadFile [(PUBDEV-299)](https://0xdata.atlassian.net/browse/PUBDEV-299) [(github)](https://github.com/h2oai/h2o-flow/commit/3f8fb91cf6d81aefdb0ad6deee801084e0cf864f)
- Added -flow_dir to hadoop driver [(github)](https://github.com/h2oai/h2o-dev/commit/9883b4d98ae0056e88db449ce1ebd20394d191ac)



#####Web UI

- Add Flow packs [(HEXDEV-190)](https://0xdata.atlassian.net/browse/HEXDEV-190) [(PUBDEV-247)](https://0xdata.atlassian.net/browse/PUBDEV-247)
- Integrate H2O Help inside Help panel [(PUBDEV-108)](https://0xdata.atlassian.net/browse/PUBDEV-108) [(github)](https://github.com/h2oai/h2o-flow/commit/62e3c06e91bc0576e15516381bb59f31dbdf38ca)
- Add quick toggle button to show/hide the sidebar [(github)](https://github.com/h2oai/h2o-flow/commit/b5fb2b54a04850c9b24bb0eb03769cb519039de6)
- Add New, Open toolbar buttons [(github)](https://github.com/h2oai/h2o-flow/commit/b6efd33c9c8c2f5fe73e9ba83c1441d768ec47f7)
- Auto-refresh data preview when parse setup input parameters are changed [(PUBDEV-532)](https://0xdata.atlassian.net/browse/PUBDEV-532)
- Flow: Add playbar with Run, Continue, Pause, Progress controls [(HEXDEV-192)](https://0xdata.atlassian.net/browse/HEXDEV-192)
- You can now stop/cancel a running flow 


####Enhancements


#####Algorithms

- Display GLM coefficients only if available [(PUBDEV-466)](https://0xdata.atlassian.net/browse/PUBDEV-466)
- Add random chance line to RoC chart [(HEXDEV-168)](https://0xdata.atlassian.net/browse/HEXDEV-168)
- Speed up DLSpiral test. Ignore Neurons test (MatVec) [(github)](https://github.com/h2oai/h2o-dev/commit/822862aa29fb63e52703ce91794a64e49bb96aed)
- Use getRNG for Dropout [(github)](https://github.com/h2oai/h2o-dev/commit/94a5b4e46a4501e85fb4889e5c8b196c46f74525)
- PUBDEV-598: Add tests for determinism of RNGs [(github)](https://github.com/h2oai/h2o-dev/commit/e77c3ead2151a1202ec0b9c467641bc1c787e122)
- PUBDEV-598: Implement Chi-Square test for RNGs [(github)](https://github.com/h2oai/h2o-dev/commit/690dd333c6bf51ff4e223cd15ef9dab004ed8904)
- Add DL model output toString() [(github)](https://github.com/h2oai/h2o-dev/commit/d206bb5b9996e87e8c0058dd8f1d7580d1ea0bb1)
- Add LogLoss to MultiNomial ModelMetrics [(PUBDEV-580)](https://0xdata.atlassian.net/browse/PUBDEV-580)
- Print number of categorical levels once we hit >1000 input neurons. [(github)](https://github.com/h2oai/h2o-dev/commit/ccf645af908d4964db3bc36a98c4ff9868838dc6)
- Updated the loss behavior for GBM. When loss is set to AUTO, if the response is an integer with 2 levels, then bernoullli (rather than gaussian) behavior is chosen. As a result, the `do_classification` flag is no longer necessary in Flow, since the loss completely specifies the desired behavior, and R users no longer to use `as.factor()` in their response to get the desired bernoulli behavior. The `score_each_iteration` flag has been removed as well. [(github)](https://github.com/h2oai/h2o-dev/commit/cc971e00869197625fefec894ab705c79db05fbb)
- Fully remove `_convert_to_enum` in all algos [(github)](https://github.com/h2oai/h2o-dev/commit/7fdf5d98c1f7caf88a3a928a28b2f86b06c5b2eb)
- Port MissingValueInserter EndPoint to h2o-dev. [(PUBDEV-465)](https://0xdata.atlassian.net/browse/PUBDEV-465)





#####API 
- Display point layer for tree vs mse plots in GBM output [(PUBDEV-504)](https://0xdata.atlassian.net/browse/PUBDEV-504)
- Rename API inputs/outputs [(github)](https://github.com/h2oai/h2o-flow/commit/c7fc17afd3ff0a176e80d9d07d71c0bdd8f165eb)
- Rename Inf to Infinity [(github)](https://github.com/h2oai/h2o-flow/commit/ef5f5997d044dac9ab676b65174f09aa8785cfb6)


#####Python
- added H2OFrame.setNames(), H2OFrame.cbind(), H2OVec.cbind(), h2o.cbind(), and pyunit_cbind.py [(github)](https://github.com/h2oai/h2o-dev/commit/84a3ea920f2ea9ee76985f7ccadb1e9d3f935025)
- Make H2OVec.levels() return the levels [(github)](https://github.com/h2oai/h2o-dev/commit/ab07275a55930b574407d8c4ea8e2b29cd6acd77)
- H2OFrame.dim(), H2OFrame.append(), H2OVec.setName(), H2OVec.isna() additions. demo pyunit addition [(github)](https://github.com/h2oai/h2o-dev/commit/41e6668ca05c59e614e54477a6082345366c75c8)


#####System

- Customize H2O web UI port [(PUBDEV-483)](https://0xdata.atlassian.net/browse/PUBDEV-483)
- Make parse setup interactive [(PUBDEV-532)](https://0xdata.atlassian.net/browse/PUBDEV-532)
- Added --verbose [(github)](https://github.com/h2oai/h2o-dev/commit/5e772f8314a340666e4e80b3480b2105ceb91251)
- Adds some H2OParseExceptions. Removes all H2O.fail in parse (no parse issues should cause a fail)[(github)](https://github.com/h2oai/h2o-dev/commit/687b674d1dfb37f13542d15d1f04fe1b7c181f71)
- Allows parse to specify check_headers=HAS_HEADERS, but not provide column names [(github)](https://github.com/h2oai/h2o-dev/commit/ba48c0af1253d4bd6b05024991241fc6f7f8532a)
- Port MissingValueInserter EndPoint to h2o-dev [(PUBDEV-465)](https://0xdata.atlassian.net/browse/PUBDEV-465)



#####Web UI 
- Add 'Clear cell' and 'Run all cells' toolbar buttons [(github)](https://github.com/h2oai/h2o-flow/commit/802b3a31ed8171a43cd1e566e5f77ba7fbf33549)
- Add 'Clear cell' and 'Clear all cells' commands [(PUBDEV-493)](https://0xdata.atlassian.net/browse/PUBDEV-493) [(github)](https://github.com/h2oai/h2o-flow/commit/2ecbe04325c865d0f5d8b2cb753ca15036ea2321)
- 'Run' button selects next cell after running
- ModelMetrics by model category: Clustering [(PUBDEV-416)](https://0xdata.atlassian.net/browse/PUBDEV-416)
- ModelMetrics by model category: Regression [(PUBDEV-415)](https://0xdata.atlassian.net/browse/PUBDEV-415)
- ModelMetrics by model category: Multinomial [(PUBDEV-414)](https://0xdata.atlassian.net/browse/PUBDEV-414)
- ModelMetrics by model category: Binomial [(PUBDEV-413)](https://0xdata.atlassian.net/browse/PUBDEV-413)
- Add ability to select and delete multiple models [(github)](https://github.com/h2oai/h2o-flow/commit/8a9d033deba68292347c1e027b461a4c9ba7f1e5)
- Add ability to select and delete multiple frames [(github)](https://github.com/h2oai/h2o-flow/commit/6d5455b041f5af6b6213694ee1aae8d4e4d57d2b)
- Flows now stop running when an error occurs
- Print full number of mismatches during POJO comparison check. [(github)](https://github.com/h2oai/h2o-dev/commit/e8b599b59f2117083d2f7979cd1a0ca957a41605)
- Make Grid multi-node safe [(github)](https://github.com/h2oai/h2o-dev/commit/915cf0bd4fa589c6d819ba1eba85811e30f87399)
- Beautify the vertical axis labels for Flow charts/visualization (more) [(PUBDEV-329)](https://0xdata.atlassian.net/browse/PUBDEV-329)

####Bug Fixes

#####Algorithms

- GBM only populates either MSE_train or MSE_valid but displays both [(PUBDEV-350)](https://0xdata.atlassian.net/browse/PUBDEV-350)
- GBM: train error increases after hitting zero on prostate dataset [(PUBDEV-513)](https://0xdata.atlassian.net/browse/PUBDEV-513)
- GBM : Variable importance displays 0's for response param => should not display response in table at all [(PUBDEV-430)](https://0xdata.atlassian.net/browse/PUBDEV-430) 
- GLM : R/Flow ==> Build GLM Model hangs at 4% [(PUBDEV-456)](https://0xdata.atlassian.net/browse/PUBDEV-456)
- Import file from R hangs at 75% for 15M Rows/2.2 K Columns [(HEXDEV-179)](https://0xdata.atlassian.net/browse/HEXDEV-179)
- Flow: GLM - 'model.output.coefficients_magnitude.name' not found, so can't view model [(PUBDEV-466)](https://0xdata.atlassian.net/browse/PUBDEV-466)
- GBM predict fails without response column [(PUBDEV-478)](https://0xdata.atlassian.net/browse/PUBDEV-478)
- GBM: When validation set is provided, gbm should report both mse_valid and mse_train [(PUBDEV-499)](https://0xdata.atlassian.net/browse/PUBDEV-499)
- PCA Assertion Error during Model Metrics [(PUBDEV-548)](https://0xdata.atlassian.net/browse/PUBDEV-548) [(github)](https://github.com/h2oai/h2o-dev/commit/69690db57ed9951a57df83b2ce30be30a49ca507)
- KMeans: Size of clusters in Model Output is different from the labels generated on the training set [(PUBDEV-542)](https://0xdata.atlassian.net/browse/PUBDEV-542) [(github)](https://github.com/h2oai/h2o-dev/commit/6f8a857c8a060af0d2434cda91469ef8c23c86ae)
- Inconsistency in GBM results:Gives different results even when run with the same set of params [(HEXDEV-194)](https://0xdata.atlassian.net/browse/HEXDEV-194)
- PUBDEV-580: Fix some numerical edge cases [(github)](https://github.com/h2oai/h2o-dev/commit/4affd9baa005c08d6b1669e462ec7bfb4de5ec69)
- Fix two missing float -> double conversion changes in tree scoring. [(github)](https://github.com/h2oai/h2o-dev/commit/b2cc99822db9b59766f3293e4dbbeeea547cd81e)
- Flow: HIDDEN_DROPOUT_RATIOS for DL does not show default value [(PUBDEV-285)](https://0xdata.atlassian.net/browse/PUBDEV-285)
- Old GLM Parameters Missing [(PUBDEV-431)](https://0xdata.atlassian.net/browse/PUBDEV-431)
- GLM: R/Flow ==> Build GLM Model hangs at 4% [(PUBDEV-456)](https://0xdata.atlassian.net/browse/PUBDEV-456)

 



#####API
- SplitFrame on String column produce C0LChunk instead of CStrChunk [(PUBDEV-468)](https://0xdata.atlassian.net/browse/PUBDEV-468)
-  Error in node$h2o$node : $ operator is invalid for atomic vectors [(PUBDEV-348)](https://0xdata.atlassian.net/browse/PUBDEV-348)
-  Response from /ModelBuilders don't conform to standard error json shape when there are errors [(HEXDEV-121)](https://0xdata.atlassian.net/browse/HEXDEV-121) [(github)](https://github.com/h2oai/h2o-dev/commit/dadf385b3e3b2f68afe88096ecfd51e5bc9e01cb)

#####Python
- fix python syntax error [(github)](https://github.com/h2oai/h2o-dev/commit/a3c62f099088ac2206b83275ca096d4952f76e28)
- Fixes handling of None in python for a returned na_string. [(github)](https://github.com/h2oai/h2o-dev/commit/58c1af54b37909b8e9d06d23ed41fce4943eceb4)



#####R
- R : Inconsistency - Train set name with and without quotes work but Validation set name with quotes does not work [(PUBDEV-491)](https://0xdata.atlassian.net/browse/PUBDEV-491)
- h2o.confusionmatrices does not work [(PUBDEV-547)](https://0xdata.atlassian.net/browse/PUBDEV-547)
- How do i convert an enum column back to integer/double from R? [(PUBDEV-546)](https://0xdata.atlassian.net/browse/PUBDEV-546)
- Summary in R is faulty [(PUBDEV-539)](https://0xdata.atlassian.net/browse/PUBDEV-539)
- R: as.h2o should preserve R data types [(PUBDEV-578)](https://0xdata.atlassian.net/browse/PUBDEV-578)
- NPE in GBM Prediction with Sliced Test Data [(HEXDEV-207)](https://0xdata.atlassian.net/browse/HEXDEV-207) [(github)](https://github.com/h2oai/h2o-dev/commit/e605ab109488c7630223320fdd8bad486492050a)
- Import file from R hangs at 75% for 15M Rows/2.2 K Columns [(HEXDEV-179)](https://0xdata.atlassian.net/browse/HEXDEV-179)
- Custom Functions don't work in apply() in R [(PUBDEV-436)](https://0xdata.atlassian.net/browse/PUBDEV-436)
- got water.DException$DistributedException and then got java.lang.RuntimeException: Categorical renumber task [(HEXDEV-195)](https://0xdata.atlassian.net/browse/HEXDEV-195)
- H2O-R:  as.h2o parses column name as one of the row entries [(PUBDEV-591)](https://0xdata.atlassian.net/browse/PUBDEV-591)
- R-H2O Managing Memory in a loop [(PUB-1125)](https://0xdata.atlassian.net/browse/PUB-1125)
- h2o.confusionMatrices for multinomial does not work [(PUBDEV-577)](https://0xdata.atlassian.net/browse/PUBDEV-577)
- H2O-R not showing meaningful error msg 





#####System
- Flow: When balance class = F then flow should not show max_after_balance_size = 5 in the parameter listing [(PUBDEV-503)](https://0xdata.atlassian.net/browse/PUBDEV-503)
- 3 jvms, doing ModelMetrics on prostate, class water.KeySnapshot$GlobalUKeySetTask; class java.lang.AssertionError: *** Attempting to block on task (class water.TaskGetKey) with equal or lower priority. Can lead to deadlock! 122 <=  122 [(PUBDEV-495)](https://0xdata.atlassian.net/browse/PUBDEV-495)
- Not able to start h2o on hadoop [(PUBDEV-487)](https://0xdata.atlassian.net/browse/PUBDEV-487)
- one row (one col) dataset seems to get assertion error in parse setup request [(PUBDEV-96)](https://0xdata.atlassian.net/browse/PUBDEV-96)
- Parse : Import file (move.com) => Parse => First row contains column names => column names not selected [(HEXDEV-171)](https://0xdata.atlassian.net/browse/HEXDEV-171) [(github)](https://github.com/h2oai/h2o-dev/commit/6f6d7023f9f2bafcb5461f46cf2825f233779f4a)
- The NY0 parse rule, in summary. Doesn't look like it's counting the 0's as NAs like h2o [(PUBDEV-154)](https://0xdata.atlassian.net/browse/PUBDEV-154)
- 0 / Y / N parsing [(PUBDEV-229)](https://0xdata.atlassian.net/browse/PUBDEV-229)
- NodePersistentStorage gets wiped out when laptop is restarted. [(HEXDEV-167)](https://0xdata.atlassian.net/browse/HEXDEV-167)
- Building a model and making a prediction accepts invalid frame types [(PUBDEV-83)](https://0xdata.atlassian.net/browse/PUBDEV-83)
- Flow : Import file 15M rows 2.2 Cols => Parse => Error fetching job on UI =>Console : ERROR: Job was not successful Exiting with nonzero exit status [(HEXDEV-55)](https://0xdata.atlassian.net/browse/HEXDEV-55)
- Flow : Build GLM Model => Family tweedy => class hex.glm.LSMSolver$ADMMSolver$NonSPDMatrixException', with msg 'Matrix is not SPD, can't solve without regularization [(PUBDEV-211)](https://0xdata.atlassian.net/browse/PUBDEV-211)
- Flow : Import File : File doesn't exist on all the hdfs nodes => Fails without valid message [(PUBDEV-313)](https://0xdata.atlassian.net/browse/PUBDEV-313)
- Check reproducibility on multi-node vs single-node [(PUBDEV-557)](https://0xdata.atlassian.net/browse/PUBDEV-557)
- Parse : After parsing Chicago crime dataset => Not able to build models or Get frames [(PUBDEV-576)](https://0xdata.atlassian.net/browse/PUBDEV-576)
 




#####Web UI
- Flow : Build Model => Parameters => shows meta text for some params [(PUBDEV-505)](https://0xdata.atlassian.net/browse/PUBDEV-505)
- Flow: K-Means - "None" option should not appear in "Init" parameters [(PUBDEV-459)](https://0xdata.atlassian.net/browse/PUBDEV-459)
- Flow: PCA - "None" option appears twice in "Transform" list [(HEXDEV-186)](https://0xdata.atlassian.net/browse/HEXDEV-186)
- GBM Model : Params in flow show two times [(PUBDEV-440)](https://0xdata.atlassian.net/browse/PUBDEV-440)
- Flow multinomial confusion matrix visualization [(HEXDEV-204)](https://0xdata.atlassian.net/browse/HEXDEV-204)
- Flow: It would be good if flow can report the actual distribution, instead of just reporting "Auto" in the model parameter listing [(PUBDEV-509)](https://0xdata.atlassian.net/browse/PUBDEV-509)
- Unimplemented algos should be taken out from drop down of build model [(PUBDEV-511)](https://0xdata.atlassian.net/browse/PUBDEV-511)
- [MapR] unable to give hdfs file name from Flow [(PUBDEV-409)](https://0xdata.atlassian.net/browse/PUBDEV-409)





---

###Selberg (0.2.0.1) - 3/6/15
####New Features


#####Algorithms
- Naive Bayes in H2O-dev [(PUBDEV-158)](https://0xdata.atlassian.net/browse/PUBDEV-158)
- GLM model output, details from R [(HEXDEV-94)](https://0xdata.atlassian.net/browse/HEXDEV-94)
- Run GLM Regression from Flow (including LBFGS) [(HEXDEV-110)](https://0xdata.atlassian.net/browse/HEXDEV-110)
- PCA [(PUBDEV-157)](https://0xdata.atlassian.net/browse/PUBDEV-157)
- Port Random Forest to h2o-dev [(PUBDEV-455)](https://0xdata.atlassian.net/browse/PUBDEV-455)
- Enable DRF model output [(github)](https://github.com/h2oai/h2o-flow/commit/44ee1bf98dd69f33251a7a959b1000cc7f290427)
- Add DRF to Flow (Model Output) [(PUBDEV-533)](https://0xdata.atlassian.net/browse/PUBDEV-533)
- Grid for GBM [(github)](https://github.com/h2oai/h2o-dev/commit/ce96d2859aa86e4df393a13e00fbb7fcf603c166)
- Run Deep Learning Regression from Flow [(HEXDEV-109)](https://0xdata.atlassian.net/browse/HEXDEV-109)

#####Python
- Add Python wrapper for DRF [(PUBDEV-534)](https://0xdata.atlassian.net/browse/PUBDEV-534)


#####R
- Add R wrapper for DRF [(PUBDEV-530)](https://0xdata.atlassian.net/browse/PUBDEV-530)



#####System
- Include uploadFile [(PUBDEV-299)](https://0xdata.atlassian.net/browse/PUBDEV-299) [(github)](https://github.com/h2oai/h2o-flow/commit/3f8fb91cf6d81aefdb0ad6deee801084e0cf864f)
- Added -flow_dir to hadoop driver [(github)](https://github.com/h2oai/h2o-dev/commit/9883b4d98ae0056e88db449ce1ebd20394d191ac)



#####Web UI

- Add Flow packs [(HEXDEV-190)](https://0xdata.atlassian.net/browse/HEXDEV-190) [(PUBDEV-247)](https://0xdata.atlassian.net/browse/PUBDEV-247)
- Integrate H2O Help inside Help panel [(PUBDEV-108)](https://0xdata.atlassian.net/browse/PUBDEV-108) [(github)](https://github.com/h2oai/h2o-flow/commit/62e3c06e91bc0576e15516381bb59f31dbdf38ca)
- Add quick toggle button to show/hide the sidebar [(github)](https://github.com/h2oai/h2o-flow/commit/b5fb2b54a04850c9b24bb0eb03769cb519039de6)
- Add New, Open toolbar buttons [(github)](https://github.com/h2oai/h2o-flow/commit/b6efd33c9c8c2f5fe73e9ba83c1441d768ec47f7)
- Auto-refresh data preview when parse setup input parameters are changed [(PUBDEV-532)](https://0xdata.atlassian.net/browse/PUBDEV-532)
-Flow: Add playbar with Run, Continue, Pause, Progress controls [(HEXDEV-192)](https://0xdata.atlassian.net/browse/HEXDEV-192)
- You can now stop/cancel a running flow 


####Enhancements

The following changes are improvements to existing features (which includes changed default values):

#####Algorithms

- Display GLM coefficients only if available [(PUBDEV-466)](https://0xdata.atlassian.net/browse/PUBDEV-466)
- Add random chance line to RoC chart [(HEXDEV-168)](https://0xdata.atlassian.net/browse/HEXDEV-168)
- Allow validation dataset for AutoEncoder [(PUDEV-581)](https://0xdata.atlassian.net/browse/PUBDEV-581)
- Speed up DLSpiral test. Ignore Neurons test (MatVec) [(github)](https://github.com/h2oai/h2o-dev/commit/822862aa29fb63e52703ce91794a64e49bb96aed)
- Use getRNG for Dropout [(github)](https://github.com/h2oai/h2o-dev/commit/94a5b4e46a4501e85fb4889e5c8b196c46f74525)
- PUBDEV-598: Add tests for determinism of RNGs [(github)](https://github.com/h2oai/h2o-dev/commit/e77c3ead2151a1202ec0b9c467641bc1c787e122)
- PUBDEV-598: Implement Chi-Square test for RNGs [(github)](https://github.com/h2oai/h2o-dev/commit/690dd333c6bf51ff4e223cd15ef9dab004ed8904)
- PUBDEV-580: Add log loss to binomial and multinomial model metric [(github)](https://github.com/h2oai/h2o-dev/commit/8982a0a1ba575bd5ca6ca3e854382e03146743cd)
- Add DL model output toString() [(github)](https://github.com/h2oai/h2o-dev/commit/d206bb5b9996e87e8c0058dd8f1d7580d1ea0bb1)
- Add LogLoss to MultiNomial ModelMetrics [(PUBDEV-580)](https://0xdata.atlassian.net/browse/PUBDEV-580)
- Port MissingValueInserter EndPoint to h2o-dev [(PUBDEV-465)](https://0xdata.atlassian.net/browse/PUBDEV-465)
- Print number of categorical levels once we hit >1000 input neurons. [(github)](https://github.com/h2oai/h2o-dev/commit/ccf645af908d4964db3bc36a98c4ff9868838dc6)
- Updated the loss behavior for GBM. When loss is set to AUTO, if the response is an integer with 2 levels, then bernoullli (rather than gaussian) behavior is chosen. As a result, the `do_classification` flag is no longer necessary in Flow, since the loss completely specifies the desired behavior, and R users no longer to use `as.factor()` in their response to get the desired bernoulli behavior. The `score_each_iteration` flag has been removed as well. [(github)](https://github.com/h2oai/h2o-dev/commit/cc971e00869197625fefec894ab705c79db05fbb)
- Fully remove `_convert_to_enum` in all algos [(github)](https://github.com/h2oai/h2o-dev/commit/7fdf5d98c1f7caf88a3a928a28b2f86b06c5b2eb)
- Add DL POJO scoring [(PUBDEV-585)](https://0xdata.atlassian.net/browse/PUBDEV-585)





#####API 
- Display point layer for tree vs mse plots in GBM output [(PUBDEV-504)](https://0xdata.atlassian.net/browse/PUBDEV-504)
- Rename API inputs/outputs [(github)](https://github.com/h2oai/h2o-flow/commit/c7fc17afd3ff0a176e80d9d07d71c0bdd8f165eb)
- Rename Inf to Infinity [(github)](https://github.com/h2oai/h2o-flow/commit/ef5f5997d044dac9ab676b65174f09aa8785cfb6)


#####Python
- added H2OFrame.setNames(), H2OFrame.cbind(), H2OVec.cbind(), h2o.cbind(), and pyunit_cbind.py [(github)](https://github.com/h2oai/h2o-dev/commit/84a3ea920f2ea9ee76985f7ccadb1e9d3f935025)
- Make H2OVec.levels() return the levels [(github)](https://github.com/h2oai/h2o-dev/commit/ab07275a55930b574407d8c4ea8e2b29cd6acd77)
- H2OFrame.dim(), H2OFrame.append(), H2OVec.setName(), H2OVec.isna() additions. demo pyunit addition [(github)](https://github.com/h2oai/h2o-dev/commit/41e6668ca05c59e614e54477a6082345366c75c8)


#####R
- PUBDEV-578, PUBDEV-541, PUBDEV-566.
	-R client now sends the data frame column names and data types to ParseSetup.
	-R client can get column names from a parsed frame or a list.
	-Respects client request for column data types [(github)](https://github.com/h2oai/h2o-dev/commit/ba063be25d3fbb658b016ff514083284e2d95d78)

#####System

- Customize H2O web UI port [(PUBDEV-483)](https://0xdata.atlassian.net/browse/PUBDEV-483)
- Make parse setup interactive [(PUBDEV-532)](https://0xdata.atlassian.net/browse/PUBDEV-532)
- Added --verbose [(github)](https://github.com/h2oai/h2o-dev/commit/5e772f8314a340666e4e80b3480b2105ceb91251)
- Adds some H2OParseExceptions. Removes all H2O.fail in parse (no parse issues should cause a fail)[(github)](https://github.com/h2oai/h2o-dev/commit/687b674d1dfb37f13542d15d1f04fe1b7c181f71)
- Allows parse to specify check_headers=HAS_HEADERS, but not provide column names [(github)](https://github.com/h2oai/h2o-dev/commit/ba48c0af1253d4bd6b05024991241fc6f7f8532a)
- Port MissingValueInserter EndPoint to h2o-dev [(PUBDEV-465)](https://0xdata.atlassian.net/browse/PUBDEV-465)



#####Web UI 
- Add 'Clear cell' and 'Run all cells' toolbar buttons [(github)](https://github.com/h2oai/h2o-flow/commit/802b3a31ed8171a43cd1e566e5f77ba7fbf33549)
- Add 'Clear cell' and 'Clear all cells' commands [(PUBDEV-493)](https://0xdata.atlassian.net/browse/PUBDEV-493) [(github)](https://github.com/h2oai/h2o-flow/commit/2ecbe04325c865d0f5d8b2cb753ca15036ea2321)
- 'Run' button selects next cell after running
- ModelMetrics by model category: Clustering [(PUBDEV-416)](https://0xdata.atlassian.net/browse/PUBDEV-416)
- ModelMetrics by model category: Regression [(PUBDEV-415)](https://0xdata.atlassian.net/browse/PUBDEV-415)
- ModelMetrics by model category: Multinomial [(PUBDEV-414)](https://0xdata.atlassian.net/browse/PUBDEV-414)
- ModelMetrics by model category: Binomial [(PUBDEV-413)](https://0xdata.atlassian.net/browse/PUBDEV-413)
- Add ability to select and delete multiple models [(github)](https://github.com/h2oai/h2o-flow/commit/8a9d033deba68292347c1e027b461a4c9ba7f1e5)
- Add ability to select and delete multiple frames [(github)](https://github.com/h2oai/h2o-flow/commit/6d5455b041f5af6b6213694ee1aae8d4e4d57d2b)
- Flows now stop running when an error occurs
- Print full number of mismatches during POJO comparison check. [(github)](https://github.com/h2oai/h2o-dev/commit/e8b599b59f2117083d2f7979cd1a0ca957a41605)
- Make Grid multi-node safe [(github)](https://github.com/h2oai/h2o-dev/commit/915cf0bd4fa589c6d819ba1eba85811e30f87399)
- Beautify the vertical axis labels for Flow charts/visualization (more) [(PUBDEV-329)](https://0xdata.atlassian.net/browse/PUBDEV-329)

####Bug Fixes
The following changes are to resolve incorrect software behavior: 

#####Algorithms

- GBM only populates either MSE_train or MSE_valid but displays both [(PUBDEV-350)](https://0xdata.atlassian.net/browse/PUBDEV-350)
- GBM: train error increases after hitting zero on prostate dataset [(PUBDEV-513)](https://0xdata.atlassian.net/browse/PUBDEV-513)
- GBM : Variable importance displays 0's for response param => should not display response in table at all [(PUBDEV-430)](https://0xdata.atlassian.net/browse/PUBDEV-430) 
- Inconsistency in GBM results:Gives different results even when run with the same set of params [(HEXDEV-194)](https://0xdata.atlassian.net/browse/HEXDEV-194)
- GLM : R/Flow ==> Build GLM Model hangs at 4% [(PUBDEV-456)](https://0xdata.atlassian.net/browse/PUBDEV-456)
- Import file from R hangs at 75% for 15M Rows/2.2 K Columns [(HEXDEV-179)](https://0xdata.atlassian.net/browse/HEXDEV-179)
- Flow: GLM - 'model.output.coefficients_magnitude.name' not found, so can't view model [(PUBDEV-466)](https://0xdata.atlassian.net/browse/PUBDEV-466)
- GBM predict fails without response column [(PUBDEV-478)](https://0xdata.atlassian.net/browse/PUBDEV-478)
- GBM: When validation set is provided, gbm should report both mse_valid and mse_train [(PUBDEV-499)](https://0xdata.atlassian.net/browse/PUBDEV-499)
- PCA Assertion Error during Model Metrics [(PUBDEV-548)](https://0xdata.atlassian.net/browse/PUBDEV-548) [(github)](https://github.com/h2oai/h2o-dev/commit/69690db57ed9951a57df83b2ce30be30a49ca507)
- KMeans: Size of clusters in Model Output is different from the labels generated on the training set [(PUBDEV-542)](https://0xdata.atlassian.net/browse/PUBDEV-542) [(github)](https://github.com/h2oai/h2o-dev/commit/6f8a857c8a060af0d2434cda91469ef8c23c86ae)
- Inconsistency in GBM results:Gives different results even when run with the same set of params [(HEXDEV-194)](https://0xdata.atlassian.net/browse/HEXDEV-194)
- divide by zero in modelmetrics for deep learning [(PUBDEV-568)](https://0xdata.atlassian.net/browse/PUBDEV-568)
- AUC reported on training data is 0, but should be 1 [(HEXDEV-223)](https://0xdata.atlassian.net/browse/HEXDEV-223) [(github)](https://github.com/h2oai/h2o-dev/commit/312558524749a0b28bf22ffd8c34ebcd6996b350)
- GBM: reports 0th tree mse value for the validation set, different than the train set ,When only train sets is provided [(PUDEV-561)](https://0xdata.atlassian.net/browse/PUBDEV-561)
- PUBDEV-580: Fix some numerical edge cases [(github)](https://github.com/h2oai/h2o-dev/commit/4affd9baa005c08d6b1669e462ec7bfb4de5ec69)
- Fix two missing float -> double conversion changes in tree scoring. [(github)](https://github.com/h2oai/h2o-dev/commit/b2cc99822db9b59766f3293e4dbbeeea547cd81e)
- Problems during Train/Test adaptation between Enum/Numeric [(HEXDEV-229)](https://0xdata.atlassian.net/browse/HEXDEV-229)
- DRF/GBM balance_classes=True throws unimplemented exception [(HEXDEV-226)](https://0xdata.atlassian.net/browse/HEXDEV-226)
- Flow: HIDDEN_DROPOUT_RATIOS for DL does not show default value [(PUBDEV-285)](https://0xdata.atlassian.net/browse/PUBDEV-285)
- Old GLM Parameters Missing [(PUBDEV-431)](https://0xdata.atlassian.net/browse/PUBDEV-431)
- GLM: R/Flow ==> Build GLM Model hangs at 4% [(PUBDEV-456)](https://0xdata.atlassian.net/browse/PUBDEV-456)
- GBM: Initial mse in bernoulli seems to be off [(PUBDEV-515)](https://0xdata.atlassian.net/browse/PUBDEV-515) 
 



#####API
- SplitFrame on String column produce C0LChunk instead of CStrChunk [(PUBDEV-468)](https://0xdata.atlassian.net/browse/PUBDEV-468)
-  Error in node$h2o$node : $ operator is invalid for atomic vectors [(PUBDEV-348)](https://0xdata.atlassian.net/browse/PUBDEV-348)
-  Response from /ModelBuilders don't conform to standard error json shape when there are errors [(HEXDEV-121)](https://0xdata.atlassian.net/browse/HEXDEV-121)

#####Python
- fix python syntax error [(github)](https://github.com/h2oai/h2o-dev/commit/a3c62f099088ac2206b83275ca096d4952f76e28)
- Fixes handling of None in python for a returned na_string. [(github)](https://github.com/h2oai/h2o-dev/commit/58c1af54b37909b8e9d06d23ed41fce4943eceb4)


#####R
- R : Inconsistency - Train set name with and without quotes work but Validation set name with quotes does not work [(PUBDEV-491)](https://0xdata.atlassian.net/browse/PUBDEV-491)
- h2o.confusionmatrices does not work [(PUBDEV-547)](https://0xdata.atlassian.net/browse/PUBDEV-547)
- How do i convert an enum column back to integer/double from R? [(PUBDEV-546)](https://0xdata.atlassian.net/browse/PUBDEV-546)
- Summary in R is faulty [(PUBDEV-539)](https://0xdata.atlassian.net/browse/PUBDEV-539)
- Custom Functions don't work in apply() in R [(PUBDEV-436)](https://0xdata.atlassian.net/browse/PUBDEV-436)
- R: as.h2o should preserve R data types [(PUBDEV-578)](https://0xdata.atlassian.net/browse/PUBDEV-578)
- as.h2o loses track of headers [(PUBDEV-541)](https://0xdata.atlassian.net/browse/PUBDEV-541)
- NPE in GBM Prediction with Sliced Test Data [(HEXDEV-207)](https://0xdata.atlassian.net/browse/HEXDEV-207) [(github)](https://github.com/h2oai/h2o-dev/commit/e605ab109488c7630223320fdd8bad486492050a)
- Import file from R hangs at 75% for 15M Rows/2.2 K Columns [(HEXDEV-179)](https://0xdata.atlassian.net/browse/HEXDEV-179)
- Custom Functions don't work in apply() in R [(PUBDEV-436)](https://0xdata.atlassian.net/browse/PUBDEV-436)
- got water.DException$DistributedException and then got java.lang.RuntimeException: Categorical renumber task [(HEXDEV-195)](https://0xdata.atlassian.net/browse/HEXDEV-195)
- h2o.confusionMatrices for multinomial does not work [(PUBDEV-577)](https://0xdata.atlassian.net/browse/PUBDEV-577)
- R: h2o.confusionMatrix should handle both models and model metric objects [(PUBDEV-590)](https://0xdata.atlassian.net/browse/PUBDEV-590)
- H2O-R:  as.h2o parses column name as one of the row entries [(PUBDEV-591)](https://0xdata.atlassian.net/browse/PUBDEV-591)


#####System
- Flow: When balance class = F then flow should not show max_after_balance_size = 5 in the parameter listing [(PUBDEV-503)](https://0xdata.atlassian.net/browse/PUBDEV-503)
- 3 jvms, doing ModelMetrics on prostate, class water.KeySnapshot$GlobalUKeySetTask; class java.lang.AssertionError: *** Attempting to block on task (class water.TaskGetKey) with equal or lower priority. Can lead to deadlock! 122 <=  122 [(PUBDEV-495)](https://0xdata.atlassian.net/browse/PUBDEV-495)
- Not able to start h2o on hadoop [(PUBDEV-487)](https://0xdata.atlassian.net/browse/PUBDEV-487)
- one row (one col) dataset seems to get assertion error in parse setup request [(PUBDEV-96)](https://0xdata.atlassian.net/browse/PUBDEV-96)
- Parse : Import file (move.com) => Parse => First row contains column names => column names not selected [(HEXDEV-171)](https://0xdata.atlassian.net/browse/HEXDEV-171) [(github)](https://github.com/h2oai/h2o-dev/commit/6f6d7023f9f2bafcb5461f46cf2825f233779f4a)
- The NY0 parse rule, in summary. Doesn't look like it's counting the 0's as NAs like h2o [(PUBDEV-154)](https://0xdata.atlassian.net/browse/PUBDEV-154)
- 0 / Y / N parsing [(PUBDEV-229)](https://0xdata.atlassian.net/browse/PUBDEV-229)
- NodePersistentStorage gets wiped out when laptop is restarted. [(HEXDEV-167)](https://0xdata.atlassian.net/browse/HEXDEV-167)
- Parse : Parsing random crap gives java.lang.ArrayIndexOutOfBoundsException: 13 [(PUBDEV-428)](https://0xdata.atlassian.net/browse/PUBDEV-428)
- Flow: converting a column to enum while parsing does not work [(PUBDEV-566)](https://0xdata.atlassian.net/browse/PUBDEV-566)
- Parse: Numbers completely parsed wrong [(PUBDEV-574)](https://0xdata.atlassian.net/browse/PUBDEV-574)
- NodePersistentStorage gets wiped out when hadoop cluster is restarted [(HEXDEV-185)](https://0xdata.atlassian.net/browse/HEXDEV-185)
- Parse: Fail gracefully when asked to parse a zip file with different files in it [(PUBDEV-540)](https://0xdata.atlassian.net/browse/PUBDEV-540)[(github)](https://github.com/h2oai/h2o-dev/commit/23a60d68e9d77fe07ae9d940b0ebb6636ef40ee3)
- Building a model and making a prediction accepts invalid frame types [(PUBDEV-83)](https://0xdata.atlassian.net/browse/PUBDEV-83)
- Flow : Import file 15M rows 2.2 Cols => Parse => Error fetching job on UI =>Console : ERROR: Job was not successful Exiting with nonzero exit status [(HEXDEV-55)](https://0xdata.atlassian.net/browse/HEXDEV-55)
- Flow : Build GLM Model => Family tweedy => class hex.glm.LSMSolver$ADMMSolver$NonSPDMatrixException', with msg 'Matrix is not SPD, can't solve without regularization [(PUBDEV-211)](https://0xdata.atlassian.net/browse/PUBDEV-211)
- Flow : Import File : File doesn't exist on all the hdfs nodes => Fails without valid message [(PUBDEV-313)](https://0xdata.atlassian.net/browse/PUBDEV-313)
- Check reproducibility on multi-node vs single-node [(PUBDEV-557)](https://0xdata.atlassian.net/browse/PUBDEV-557)
- Parse: After parsing Chicago crime dataset => Not able to build models or Get frames [(PUBDEV-576)](https://0xdata.atlassian.net/browse/PUBDEV-576)

#####Web UI
- Flow : Build Model => Parameters => shows meta text for some params [(PUBDEV-505)](https://0xdata.atlassian.net/browse/PUBDEV-505)
- Flow: K-Means - "None" option should not appear in "Init" parameters [(PUBDEV-459)](https://0xdata.atlassian.net/browse/PUBDEV-459)
- Flow: PCA - "None" option appears twice in "Transform" list [(HEXDEV-186)](https://0xdata.atlassian.net/browse/HEXDEV-186)
- GBM Model : Params in flow show two times [(PUBDEV-440)](https://0xdata.atlassian.net/browse/PUBDEV-440)
- Flow multinomial confusion matrix visualization [(HEXDEV-204)](https://0xdata.atlassian.net/browse/HEXDEV-204)
- Flow: It would be good if flow can report the actual distribution, instead of just reporting "Auto" in the model parameter listing [(PUBDEV-509)](https://0xdata.atlassian.net/browse/PUBDEV-509)
- Unimplemented algos should be taken out from drop down of build model [(PUBDEV-511)](https://0xdata.atlassian.net/browse/PUBDEV-511)
- [MapR] unable to give hdfs file name from Flow [(PUBDEV-409)](https://0xdata.atlassian.net/browse/PUBDEV-409)




---

###Selberg (0.2.0.1) - 3/6/15
####New Features

#####Web UI

- Flow: Delete functionality to be available for import files, jobs, models, frames [(PUBDEV-241)](https://0xdata.atlassian.net/browse/PUBDEV-241)
- Implement "Download Flow" [(PUBDEV-407)](https://0xdata.atlassian.net/browse/PUBDEV-407)
- Flow: Implement "Run All Cells" [(PUBDEV-110)](https://0xdata.atlassian.net/browse/PUBDEV-110)

#####API 
- Create python package [(PUBDEV-181)](https://0xdata.atlassian.net/browse/PUBDEV-181)
- as.h2o in Python [(HEXDEV-72)](https://0xdata.atlassian.net/browse/HEXDEV-72)

#####System
- Add a README.txt to the hadoop zip files [(github)](https://github.com/h2oai/h2o-dev/commit/5a06ba8f0cfead3e30737d336f3c389ca0775b58)
- Build a cdh5.2 version of h2o [(github)](https://github.com/h2oai/h2o-dev/commit/eb8855d103e4f3aaf9dfa8c07d40d6c848141245)

####Enhancements 

#####Web UI
- Flow: Job view should have info on start and end time [(PUBDEV-267)](https://0xdata.atlassian.net/browse/PUBDEV-267)
- Flow: Implement 'File > Open' [(PUBDEV-408)](https://0xdata.atlassian.net/browse/PUBDEV-408)
- Display IP address in ADMIN -> Cluster Status [(HEXDEV-159)](https://0xdata.atlassian.net/browse/HEXDEV-159)
- Flow: Display alternate UI for splitFrames() [(PUBDEV-399)](https://0xdata.atlassian.net/browse/PUBDEV-399)


#####Algorithms
- Added K-Means scoring [(github)](https://github.com/h2oai/h2o-dev/commit/220d2b40dc36dee6975a101e2eacb56a77861194)
- Flow: Implement model output for Deep Learning [(PUBDEV-118)](https://0xdata.atlassian.net/browse/PUBDEV-118)
- Flow: Implement model output for GLM [(PUBDEV-120)](https://0xdata.atlassian.net/browse/PUBDEV-120)
- Deep Learning model output [(HEXDEV-89, Flow)](https://0xdata.atlassian.net/browse/HEXDEV-89),[(HEXDEV-88, Python)](https://0xdata.atlassian.net/browse/HEXDEV-88),[(HEXDEV-87, R)](https://0xdata.atlassian.net/browse/HEXDEV-87)
- Run GLM Binomial from Flow (including LBFGS) [(HEXDEV-90)](https://0xdata.atlassian.net/browse/HEXDEV-90)
- Flow: Display confusion matrices for multinomial models [(PUBDEV-397)](https://0xdata.atlassian.net/browse/PUBDEV-397)
- During PCA, missing values in training data will be replaced with column mean [(github)](https://github.com/h2oai/h2o-dev/commit/166efad882162f7edc5cd8d4baa189476aa72d25)
- Update parameters for best model scan [(github)](https://github.com/h2oai/h2o-dev/commit/f183de392cb45adea7af43ffa53b095c3764602f)
- Change Quantiles to match h2o-1; both Quantiles and Rollups now have the same default percentiles [(github)](https://github.com/h2oai/h2o-dev/commit/51dc2c12a4281e3a2beeed8adfdfe4b14736fead)
- Massive cleanup and removal of old PCA, replacing with quadratically regularized PCA based on alternating minimization algorithm in GLRM [(github)](https://github.com/h2oai/h2o-dev/commit/02b7f168b2efa551a60c4bf2e95b8d506b613c2d)
- Add model run time to DL Model Output [(github)](https://github.com/h2oai/h2o-dev/commit/6730cc530b7b5376dfe6a2dd71817065e1edab7d)
- Don't gather Neurons/Weights/Biases statistics [(github)](https://github.com/h2oai/h2o-dev/commit/aa1360d1bcfad3628d23211284878d80aa5a3b21)
- Only store best model if `override_with_best_model` is enabled [(github)](https://github.com/h2oai/h2o-dev/commit/5bd1e2327a09b649f251b251ff72af9aa8f4824c)
- `beta_eps` added, passing tests changed [(github)](https://github.com/h2oai/h2o-dev/commit/5e5acb6bdb89ff966151b0bc1ae20e96577d0368)
- For GLM, default values for `max_iters` parameter were changed from 1000 to 50. 
- For quantiles, probabilities are displayed. 
- Run Deep Learning Multinomial from Flow [(HEXDEV-108)](https://0xdata.atlassian.net/browse/HEXDEV-108)



#####API
- Expose DL weights/biases to clients via REST call [(PUBDEV-344)](https://0xdata.atlassian.net/browse/PUBDEV-344)
- Flow: Implement notification bar/API [(PUBDEV-359)](https://0xdata.atlassian.net/browse/PUBDEV-116)
- Variable importance data in REST output for GLM [(PUBDEV-359)](https://0xdata.atlassian.net/browse/PUBDEV-359)
- Add extra DL parameters to R API (`average_activation, sparsity_beta, max_categorical_features, reproducible`) [(github)](https://github.com/h2oai/h2o-dev/commit/8c7b860e29f297ff42ad6f45a1f138a8c6bb6b29)
- Update GLRM API model output [(github)](https://github.com/h2oai/h2o-dev/commit/653a9906003c2bab5e65d576420c76093fc92d12) 
- h2o.anomaly missing in R [(PUBDEV-434)](https://0xdata.atlassian.net/browse/PUBDEV-434)
- No method to get enum levels [(PUBDEV-432)](https://0xdata.atlassian.net/browse/PUBDEV-432)



#####System
- Improve memory footprint with latest version of h2o-dev [(github)](https://github.com/h2oai/h2o-dev/commit/c54efaf41bc13677d5acd53a0496cca2b192baef)
- For now, let model.delete() of DL delete its best models too. This allows R code to not leak when only calling h2o.rm() on the main model. [(github)](https://github.com/h2oai/h2o-dev/commit/08b151a2bcbef8d56063b576638a6c0250379bd0)
- Bind both TCP and UDP ports before clustering [(github)](https://github.com/h2oai/h2o-dev/commit/d83c35841800b2abcc9d479fc74583d6ccdc714c)
- Round summary row#. Helps with pctiles for very small row counts. Add a test to check for getting close to the 50% percentile on small rows. [(github)](https://github.com/h2oai/h2o-dev/commit/7f4f7b159de0041894166f62d21e694dbd9c4c5d)
- Increase Max Value size in DKV to 256MB [(github)](https://github.com/h2oai/h2o-dev/commit/336b06e2a129509d424156653a2e7e4d5e972ed8)
- Flow: make parseRaw() do both import and parse in sequence [(HEXDEV-184)](https://0xdata.atlassian.net/browse/HEXDEV-184)
- Remove notion of individual job/job tracking from Flow [(PUBDEV-449)](https://0xdata.atlassian.net/browse/PUBDEV-449)
- Capability to name prediction results Frame in flow [(PUBDEV-233)](https://0xdata.atlassian.net/browse/PUBDEV-233)



####Bug Fixes

#####Algorithms

- GLM binomial prediction failing [(PUBDEV-403)](https://0xdata.atlassian.net/browse/PUBDEV-403)
- DL: Predict with auto encoder enabled gives Error processing error [(PUBDEV-433)](https://0xdata.atlassian.net/browse/PUBDEV-433)
- balance_classes in Deep Learning intermittent poor result [(PUBDEV-437)](https://0xdata.atlassian.net/browse/PUBDEV-437)
- Flow: Building GLM model fails [(PUBDEV-186)](https://0xdata.atlassian.net/browse/PUBDEV-186)
- summary returning incorrect 0.5 quantile for 5 row dataset [(PUBDEV-95)](https://0xdata.atlassian.net/browse/PUBDEV-95)
- GBM missing variable importance and balance-classes [(PUBDEV-309)](https://0xdata.atlassian.net/browse/PUBDEV-309)
- H2O Dev GBM first tree differs from H2O 1 [(PUBDEV-421)](https://0xdata.atlassian.net/browse/PUBDEV-421)
- get glm model from flow fails to find coefficient name field [(PUBDEV-394)](https://0xdata.atlassian.net/browse/PUBDEV-394)
- GBM/GLM build model fails on Hadoop after building 100% => Failed to find schema for version: 3 and type: GBMModel [(PUBDEV-378)](https://0xdata.atlassian.net/browse/PUBDEV-378)
- Parsing KDD wrong [(PUBDEV-393)](https://0xdata.atlassian.net/browse/PUBDEV-393)
- GLM AIOOBE [(PUBDEV-199)](https://0xdata.atlassian.net/browse/PUBDEV-199)
- Flow : Build GLM Model with family poisson => java.lang.ArrayIndexOutOfBoundsException: 1 at hex.glm.GLM$GLMLambdaTask.needLineSearch(GLM.java:359) [(PUBDEV-210)](https://0xdata.atlassian.net/browse/PUBDEV-210)
- Flow : GLM Model Error => Enum conversion only works on small integers [(PUBDEV-365)](https://0xdata.atlassian.net/browse/PUBDEV-365)
- GLM binary response, do_classfication=FALSE, family=binomial, prediction error [(PUBDEV-339)](https://0xdata.atlassian.net/browse/PUBDEV-339)
- Epsilon missing from GLM parameters [(PUBDEV-354)](https://0xdata.atlassian.net/browse/PUBDEV-354)
- GLM NPE [(PUBDEV-395)](https://0xdata.atlassian.net/browse/PUBDEV-395)
- Flow: GLM bug (or incorrect output) [(PUBDEV-252)](https://0xdata.atlassian.net/browse/PUBDEV-252)
- GLM binomial prediction failing [(PUBDEV-403)](https://0xdata.atlassian.net/browse/PUBDEV-403)
- GLM binomial on benign.csv gets assertion error in predict [(PUBDEV-132)](https://0xdata.atlassian.net/browse/PUBDEV-132)
- current summary default_pctiles doesn't have 0.001 and 0.999 like h2o1 [(PUBDEV-94)](https://0xdata.atlassian.net/browse/PUBDEV-94)
- Flow: Build GBM/DL Model: java.lang.IllegalArgumentException: Enum conversion only works on integer columns [(PUBDEV-213)](https://0xdata.atlassian.net/browse/PUBDEV-213) [(github)](https://github.com/h2oai/h2o-dev/commit/57d6d96e4fed0a993bc8017f6e5eb1f60e9ceaa4)
- ModelMetrics on cup98VAL_z dataset has response with many nulls [(PUBDEV-214)](https://0xdata.atlassian.net/browse/PUBDEV-214)
- GBM : Predict model category output/inspect parameters shows as Regression when model is built with do classification enabled [(PUBDEV-441)](https://0xdata.atlassian.net/browse/PUBDEV-441)
- Fix double-precision DRF bugs [(github)](https://github.com/h2oai/h2o-dev/commit/cf7910e7bde1d8e3c1d91fadfcf37c5a74882145)

#####System
- Null columnTypes for /smalldata/arcene/arcene_train.data [(PUBDEV-406)](https://0xdata.atlassian.net/browse/PUBDEV-406) [(github)](https://github.com/h2oai/h2o-dev/commit/8511114a6ef6444938fb75e9ac9d5d7b7fe088d5)
- Flow: Waiting for -1 responses after starting h2o on hadoop cluster of 5 nodes [(PUBDEV-419)](https://0xdata.atlassian.net/browse/PUBDEV-419)
- Parse: airlines_all.csv => Airtime type shows as ENUM instead of Integer [(PUBDEV-426)](https://0xdata.atlassian.net/browse/PUBDEV-426) [(github)](https://github.com/h2oai/h2o-dev/commit/f6051de374b46376bf178064719fdd9b03e84dfa)
- Flow: Typo - "Time" option displays twice in column header type menu in Parse [(PUBDEV-446)](https://0xdata.atlassian.net/browse/PUBDEV-446)
- Duplicate validation messages in k-means output [(PUBDEV-305)](https://0xdata.atlassian.net/browse/PUBDEV-305) [(github)](https://github.com/h2oai/h2o-dev/commit/7905ba668572cb0eb518d791dc3262a2e8ff2fe0)
- Fixes Parse so that it returns to supplying generic column names when no column names exist [(github)](https://github.com/h2oai/h2o-dev/commit/d404bff2ef41e9a6e2d559c53c42225f11a81bff)
- Flow: Import File: File doesn't exist on all the hdfs nodes => Fails without valid message [(PUBDEV-313)](https://0xdata.atlassian.net/browse/PUBDEV-313)
- Flow: Parse => 1m.svm hangs at 42% [(HEXDEV-174)](https://0xdata.atlassian.net/browse/HEXDEV-174)
- Prediction NFE [(PUBDEV-308)](https://0xdata.atlassian.net/browse/PUBDEV-308)
- NPE doing Frame to key before it's fully parsed [(PUBDEV-79)](https://0xdata.atlassian.net/browse/PUBDEV-79)
- `h2o_master_DEV_gradle_build_J8` #351 hangs for past 17 hrs [(PUBDEV-239)](https://0xdata.atlassian.net/browse/PUBDEV-239)
- Sparkling water - container exited due to unavailable port [(PUBDEV-357)](https://0xdata.atlassian.net/browse/PUBDEV-357)



#####API
- Flow: Splitframe => java.lang.ArrayIndexOutOfBoundsException [(PUBDEV-410)](https://0xdata.atlassian.net/browse/PUBDEV-410) [(github)](https://github.com/h2oai/h2o-dev/commit/f5cf2888230df8904f0d87b8d97c31cc9cf26f79)
- Incorrect dest.type, description in /CreateFrame jobs [(PUBDEV-404)](https://0xdata.atlassian.net/browse/PUBDEV-404)
- space in windows filename on python [(PUBDEV-444)](https://0xdata.atlassian.net/browse/PUBDEV-444) [(github)](https://github.com/h2oai/h2o-dev/commit/c3a7f2f95ee41f5eb9bd9f4efd5b870af6cbc314)
- Python end-to-end data science example 1 runs correctly [(PUBDEV-182)](https://0xdata.atlassian.net/browse/PUBDEV-182)
- 3/NodePersistentStorage.json/foo/id should throw 404 instead of 500 for 'not-found' [(HEXDEV-163)](https://0xdata.atlassian.net/browse/HEXDEV-163)
- POST /3/NodePersistentStorage.json should handle Content-Type:multipart/form-data [(HEXDEV-165)](https://0xdata.atlassian.net/browse/HEXDEV-165)
- by class water.KeySnapshot$GlobalUKeySetTask; class java.lang.AssertionError: *** Attempting to block on task (class water.TaskGetKey) with equal or lower priority. Can lead to deadlock! 122 <= 122 [(PUBDEV-92)](https://0xdata.atlassian.net/browse/PUBDEV-92)
- Sparkling water : val train:DataFrame = prostateRDD => Fails with ArrayIndexOutOfBoundsException [(PUBDEV-392)](https://0xdata.atlassian.net/browse/PUBDEV-392)
- Flow : getModels produces error: Error calling GET /3/Models.json [(PUBDEV-254)](https://0xdata.atlassian.net/browse/PUBDEV-254)
- Flow : Splitframe => java.lang.ArrayIndexOutOfBoundsException [(PUBDEV-410)](https://0xdata.atlassian.net/browse/PUBDEV-410)
- ddply 'Could not find the operator' [(HEXDEV-162)](https://0xdata.atlassian.net/browse/HEXDEV-162) [(github)](https://github.com/h2oai/h2o-dev/commit/5f5dca9b9fc7d7d4888af0ab7ddad962f0381993)
- h2o.table AIOOBE during NewChunk creation [(HEXDEV-161)](https://0xdata.atlassian.net/browse/HEXDEV-161) [(github)](https://github.com/h2oai/h2o-dev/commit/338d654bd2a80ddf0fba8f65272b3ba07237d2eb)
- Fix warning in h2o.ddply when supplying multiple grouping columns [(github)](https://github.com/h2oai/h2o-dev/commit/1a7adb0a1f1bffe7bf77e5332f6291d4325d6a7f)


---



###0.1.26.1051 - 2/13/15

####New Features

- Flow: Display alternate UI for splitFrames() [(PUBDEV-399)](https://0xdata.atlassian.net/browse/PUBDEV-399)


####Enhancements 

#####System
-  Embedded H2O config can now provide flat file (needed for Hadoop) [(github)](https://github.com/h2oai/h2o-dev/commit/62c344505b1c1c9154624fd9ca07d9b7217a9cfa)
- Don't logging GET of individual jobs to avoid filling up the logs [(github)](https://github.com/h2oai/h2o-dev/commit/9d4a8249ceda49fcc64b5111a62c7a86076d7ec9)

#####Algorithms
-  Increase GBM/DRF factor binning back to historical levels. Had been capped accidentally at nbins (typically 20), was intended to support a much higher cap. [(github)](https://github.com/h2oai/h2o-dev/commit/4dac6ba640818bf5d482e6352a5e6aa62214ca4b)
-  Tweaked rho heuristic in glm [(github)](https://github.com/h2oai/h2o-dev/commit/7aec116974eb14ad6c7d7002a23d952a11339b79)
-  Enable variable importances for autoencoders [(github)](https://github.com/h2oai/h2o-dev/commit/19751e56c11f4ab672d47aabde84cf73271925dd)
-  Removed `group_split` option from GBM
-  Flow: display varimp for GBM output [(PUBDEV-398)](https://0xdata.atlassian.net/browse/PUBDEV-398)
-  variable importance for GBM [(github)](https://github.com/h2oai/h2o-dev/commit/f5085c3964d87d5349f406d1cfcc81fa0b34a27f)
-  GLM in H2O-Dev may provide slightly different coefficient values when applying an L1 penalty in comparison with H2O1.

####Bug Fixes

#####Algorithms
- Fixed bug in GLM exception handling causing GLM jobs to hang [(github)](https://github.com/h2oai/h2o-dev/commit/966a58f93d6cf746a2d6ec205d070247e4aeda01)
- Fixed a bug in kmeans input parameter schema where init was always being set to Furthest [(github)](https://github.com/h2oai/h2o-dev/commit/419754634ea30f6b9d9e24a2c62730a3a3b25042)
- Fixed mean computation in GLM [(github)](https://github.com/h2oai/h2o-dev/commit/74d9314a2b73812fa6dab03de9e8ea67c8a4693e)
- Fixed kmeans.R [(github)](https://github.com/h2oai/h2o-dev/commit/a532a0c850cd3c48b281bd34f83adac9108ac885)
- Flow: Building GBM model fails with Error executing javascript [(PUBDEV-396)](https://0xdata.atlassian.net/browse/PUBDEV-396)

#####System
- DataFrame propagates absolute path to parser [(github)](https://github.com/h2oai/h2o-dev/commit/0fad77b63512f2a20e20c93830e036a32a7643fe)
- Fix flow shutdown bug [(github)](https://github.com/h2oai/h2o-dev/commit/a26bd190dac59750131a2284bdf46e77ad12b67e)


---

###0.1.26.1032 - 2/6/15

####New Features

#####General Improvements 

- better model output 
- support for Python client
- support for Maven
- support for Sparkling Water
- support for REST API schema 
- support for Hadoop CDH5 [(github)](https://github.com/h2oai/h2o-dev/commit/6a0feaebc9c7e253fe07b43dc383dfe4cbae2f29)



#####UI
- Display summary visualizations by default in column summary output cells [(PUBDEV-337)](https://0xdata.atlassian.net/browse/PUBDEV-337)
- Display AUC curve by default in binomial prediction output cells [(PUBDEV-338)](https://0xdata.atlassian.net/browse/PUBDEV-338)
- Flow: Implement About H2O/Flow with version information [(PUBDEV-111)](https://0xdata.atlassian.net/browse/PUBDEV-111)
- Add UI for CreateFrame [(PUBDEV-218)](https://0xdata.atlassian.net/browse/PUBDEV-218)
- Flow: Add ability to cancel running jobs [(PUBDEV-373)](https://0xdata.atlassian.net/browse/PUBDEV-373)
- Flow: warn when user navigates away while having unsaved content [(PUBDEV-322)](https://0xdata.atlassian.net/browse/PUBDEV-322)





#####Algorithms
- Implement splitFrame() in Flow [(PUBDEV-356)](https://0xdata.atlassian.net/browse/PUBDEV-356)
- Variable importance graph in Flow for GLM [(PUBDEV-360)](https://0xdata.atlassian.net/browse/PUBDEV-360)
- Flow: Implement model building form init and validation [(PUBDEV-102)](https://0xdata.atlassian.net/browse/PUBDEV-102)
- Added a shuffle-and-split-frame function; Use it to build a saner model on time-series data [(github)](https://github.com/h2oai/h2o-dev/commit/730c8d64316c913183a1271d1a2441f92fa11442)
- Added binomial model metrics [(github)](https://github.com/h2oai/h2o-dev/commit/2d124bea91474f3f55eb5e33f2494ae52ffba749)
- Run KMeans from R [(HEXDEV-105)](https://0xdata.atlassian.net/browse/HEXDEV-105)
- Be able to create a new GLM model from an existing one with updated coefficients [(HEXDEV-48)](https://0xdata.atlassian.net/browse/HEXDEV-48) 
- Run KMeans from Python [(HEXDEV-106)](https://0xdata.atlassian.net/browse/HEXDEV-106)
- Run Deep Learning Binomial from Flow [(HEXDEV-83)](https://0xdata.atlassian.net/browse/HEXDEV-83)
- Run KMeans from Flow [(HEXDEV-104)](https://0xdata.atlassian.net/browse/HEXDEV-104)
- Run Deep Learning from Python [(HEXDEV-85)](https://0xdata.atlassian.net/browse/HEXDEV-85)
- Run Deep Learning from R [(HEXDEV-84)](https://0xdata.atlassian.net/browse/HEXDEV-84)
- Run Deep Learning Multinomial from Flow [(HEXDEV-108)](https://0xdata.atlassian.net/browse/HEXDEV-108)
- Run Deep Learning Regression from Flow [(HEXDEV-109)](https://0xdata.atlassian.net/browse/HEXDEV-109)


#####API
- Flow: added REST API documentation to the web ui [(PUBDEV-60)](https://0xdata.atlassian.net/browse/PUB-60)
- Flow: Implement visualization API [(PUBDEV-114)](https://0xdata.atlassian.net/browse/PUBDEV-114)



#####System
- Dataset inspection from Flow [(HEXDEV-66)](https://0xdata.atlassian.net/browse/HEXDEV-66)
- Basic data munging (Rapids) from R [(HEXDEV-70)](https://0xdata.atlassian.net/browse/HEXDEV-70)
- Implement stack operator/stacking in Lightning [(HEXDEV-128)](https://0xdata.atlassian.net/browse/HEXDEV-128)





####Enhancements 


#####UI
- Added better message when h2o.init() not yet called (`No active connection to an H2O cluster. Try calling "h2o.init()"`) [(github)](https://github.com/h2oai/h2o-dev/commit/b6bbbcee5972624cecc56099c0f95e1b2dd67253)



#####Algorithms
- Updated column-based gradient task to use sparse interface [(github)](https://github.com/h2oai/h2o-dev/commit/de5685b7c8e109cc39b671ef0bfd016516145d30)
- Updated LBFGS (added progress monitor interface, updated some default params), added progress and job support to GLM lbfgs [(github)](https://github.com/h2oai/h2o-dev/commit/6b89bb9201a89df93c4131b7ba10a7d17b45d72e)
- Added pretty print [(github)](https://github.com/h2oai/h2o-dev/commit/ebc824f9b081b61337c88e52b682bf42d9825c97)
- Added AutoEncoder to R model categories [(github)](https://github.com/h2oai/h2o-dev/commit/7030e7f1fb5779c026e0eed48662571f03f13428)
- Added Coefficients table to GLM model [(github)](https://github.com/h2oai/h2o-dev/commit/a432337d9d8b6480efbdaf0a0ebdb2ca3ad3f91a)
- Updated glm lbfgs to allow for efficient lambda-search (l2 penalty only) [(github)](https://github.com/h2oai/h2o-dev/commit/302ee73916516f2a25f98d96d9dd8fbff324dc5d)
- Removed splitframe shuffle parameter [(github)](https://github.com/h2oai/h2o-dev/commit/27f030721ae71006da7f0cc66be28337973f78f8)
- Simplified model builders and added deeplearning model builder [(github)](https://github.com/h2oai/h2o-dev/commit/302c819ea3d7b623af1968a181614d51d7dc68ed)
- Add DL model outputs to Flow [(PUBDEV-372)](https://0xdata.atlassian.net/browse/PUBDEV-372)
- Flow: Deep Learning: Expert Mode [(PUBDEV-284)](https://0xdata.atlassian.net/browse/PUBDEV-284)
- Flow: Display multinomial and regression DL model outputs [(PUBDEV-383)](https://0xdata.atlassian.net/browse/PUBDEV-383)
- Display varimp details for DL models [(PUBDEV-381)](https://0xdata.atlassian.net/browse/PUBDEV-381)
- Make binomial response "0" and "1" by default [(github)](https://github.com/h2oai/h2o-dev/commit/f597d4958ff2200f68e2cead31f3a184bfcaa5f2)
- Add Coefficients table to GLM model [(github)](https://github.com/h2oai/h2o-dev/commit/a432337d9d8b6480efbdaf0a0ebdb2ca3ad3f91a)
- Removed splitframe shuffle parameter [(github)](https://github.com/h2oai/h2o-dev/commit/27f030721ae71006da7f0cc66be28337973f78f8)
-  Update R GBM demos to reflect new input parameter names [(github)](https://github.com/h2oai/h2o-dev/commit/8cb99b5bf5ba828d08deba4647309824829a27a5)
-  Rename GLM variable importance to normalized coefficient magnitudes [(github)](https://github.com/h2oai/h2o-dev/commit/8cb99b5bf5ba828d08deba4647309824829a27a5)




#####API
- Changed `key` to `destination_key` [(github)](https://github.com/h2oai/h2o-dev/commit/22067ae62a23af712d3081d981ae08756e6c071e)
- Cleaned up REST API schema interface [(github)](https://github.com/h2oai/h2o-dev/commit/ce581ec9fe670f43e8fb4aa955569cc9e92d013b)
- Changed method name, cleaned setup, added a pyunit runner [(github)](https://github.com/h2oai/h2o-dev/commit/26ea2c52440dd6ad8009c72bac8057d1edd9da0a)





#####System
- Allow changing column types during parse-setup [(PUBDEV-376)](https://0xdata.atlassian.net/browse/PUBDEV-376)
- Display %NAs in model builder column lists [(PUBDEV-375)](https://0xdata.atlassian.net/browse/PUBDEV-375)
- Figure out how to add H2O to PyPl [(PUBDEV-178)](https://0xdata.atlassian.net/browse/PUBDEV-178)




####Bug Fixes


#####UI
- Flow: Parse => 1m.svm hangs at 42% [(PUBDEV-345)](https://0xdata.atlassian.net/browse/PUBDEV-345)
- cup98 Dataset has columns that prevent validation/prediction [(PUBDEV-349)](https://0xdata.atlassian.net/browse/PUBDEV-349)
- Flow: predict step failed to function [(PUBDEV-217)](https://0xdata.atlassian.net/browse/PUBDEV-217)
- Flow: Arrays of numbers (ex. hidden in deeplearning)require brackets [(PUBDEV-303)](https://0xdata.atlassian.net/browse/PUBDEV-303)
- Flow v.0.1.26.1030: StackTrace was broken [(PUBDEV-371)](https://0xdata.atlassian.net/browse/PUBDEV-371)
- Flow: Import files -> Search -> Parse these files -> null pointer exception [(PUBDEV-170)](https://0xdata.atlassian.net/browse/PUBDEV-170)
- Flow: "getJobs" not working [(PUBDEV-320)](https://0xdata.atlassian.net/browse/PUBDEV-320)
- Thresholds x Metrics and Max Criteria x Metrics tables were flipped in flow [(HEXDEV-155)](https://0xdata.atlassian.net/browse/HEXDEV-155)
- Flow v.0.1.26.1030: StackTrace is broken [(PUBDEV-348)](https://0xdata.atlassian.net/browse/PUBDEV-348)
- flow: getJobs always shows "Your H2O cloud has no jobs" [(PUBDEV-243)](https://0xdata.atlassian.net/browse/PUBDEV-243)
- Flow: First and last characters deleted from ignored columns [(PUBDEV-300)](https://0xdata.atlassian.net/browse/PUBDEV-300)
- Sparkling water => Flow => Menu buttons for cell do not show up [(PUBDEV-294)](https://0xdata.atlassian.net/browse/PUBDEV-294)




#####Algorithms
- Flow: Build K Means model with default K value gives error "Required field k not specified" [(PUBDEV-167)](https://0xdata.atlassian.net/browse/PUBDEV-167)
- Slicing out a specific data point is broken [(PUBDEV-280)](https://0xdata.atlassian.net/browse/PUBDEV-280)
- Flow: SplitFrame and grep in algorithms for flow and loops back onto itself [(PUBDEV-272)](https://0xdata.atlassian.net/browse/PUBDEV-272)
- Fixed the predict method [(github)](https://github.com/h2oai/h2o-dev/commit/10e6b88147791ef0e7e010ffad36bb3eb2969c7b)
- Refactor ModelMetrics into a different class for Binomial [(github)](https://github.com/h2oai/h2o-dev/commit/014d14c13fee5b87bdde1cb8b441c67def1365cc)
- /Predictions.json did not cache predictions [(HEXDEV-119)](https://0xdata.atlassian.net/browse/HEXDEV-119)
- Flow, DL: Error after changing hidden layer size [(PUBDEV-323)](https://0xdata.atlassian.net/browse/PUBDEV-323)
- Error in node$h2o#node: $ operator is invalid for atomic vectors [(PUBDEV-348)](https://0xdata.atlassian.net/browse/PUBDEV-348)
- Fixed K-means predict [(PUBDEV-321)](https://0xdata.atlassian.net/browse/PUBDEV-321)
- Flow: DL build mode fails => as it's missing adding quotes to parameter [(PUBDEV-301)](https://0xdata.atlassian.net/browse/PUBDEV-301)
- Flow: Build K means model with training/validation frames => unknown error [(PUBDEV-185)](https://0xdata.atlassian.net/browse/PUBDEV-185)
- Flow: Build quantile mode=> Click goes in loop [(PUBDEV-188)](https://0xdata.atlassian.net/browse/PUBDEV-188)





#####API
- Sparkling Water/Flow: Failed to find version for schema [(PUBDEV-367)](https://0xdata.atlassian.net/browse/PUBDEV-367)
- Cloud.json returns odd node name [(PUBDEV-259)](https://0xdata.atlassian.net/browse/PUBDEV-259)





#####System
- guesser needs to send types to parse [(PUBDEV-279)](https://0xdata.atlassian.net/browse/PUBDEV-279)
- Got h2o.clusterStatus function working in R. [(github)](https://github.com/h2oai/h2o-dev/commit/0d5a837f75145b3486e35eea198e322488e9afce)
- Parse: Using R => java.lang.NullPointerException [(PUBDEV-380)](https://0xdata.atlassian.net/browse/PUBDEV-380)
- Flow: Jobs => click on destination key => unimplemented: Unexpected val class for Inspect: class water.fvec.DataFrame [(PUBDEV-363)](https://0xdata.atlassian.net/browse/PUBDEV-363)
- Column assignment in R exposes NullPointerException in Rollup [(PUBDEV-155)](https://0xdata.atlassian.net/browse/PUBDEV-155)
- import from hdfs doesn't add files [(PUBDEV-260)](https://0xdata.atlassian.net/browse/PUBDEV-260)
- AssertionError: ERROR: got tcp resend with existing in-progress task [(PUBDEV-219)](https://0xdata.atlassian.net/browse/PUBDEV-219)
- HDFS parse fails when H2O launched on Spark CDH5 [(PUBDEV-138)](https://0xdata.atlassian.net/browse/PUBDEV-138)
- Flow: Parse failure => java.lang.ArrayIndexOutOfBoundsException [(PUBDEV-296)](https://0xdata.atlassian.net/browse/PUBDEV-296)
- "predict" step is not working in flow [(PUBDEV-202)](https://0xdata.atlassian.net/browse/PUBDEV-202)
- Flow: Frame finishes parsing but comes up as null in flow [(PUBDEV-270)](https://0xdata.atlassian.net/browse/PUBDEV-270)
- scala >flightsToORD.first() fails with "not serializable result" [(PUBDEV-304)](https://0xdata.atlassian.net/browse/PUBDEV-304)
- DL throws NPE for bad column names [(PUBDEV-15)](https://0xdata.atlassian.net/browse/PUBDEV-15)
- Flow: Build model: Not able to build KMeans/Deep Learning model [(PUBDEV-297)](https://0xdata.atlassian.net/browse/PUBDEV-297)
- Flow: Col summary for NA/Y cols breaks [(PUBDEV-325)](https://0xdata.atlassian.net/browse/PUBDEV-325)
- Sparkling Water : util.SparkUncaughtExceptionHandler: Uncaught exception in thread Thread NanoHTTPD Session,9,main [(PUBDEV-346)](https:/0xdata.atlassian.net/browse/PUBDEV-346)
- toDataFrame doesn't support sequence format schema (array, vectorUDT) [(PUBDEV-457)](https://0xdata.atlassian.net/browse/PUBDEV-457)





---

###0.1.20.1019 - 1/19/15

####New Features

#####UI
- Added various documentation links to the build page [(github)](https://github.com/h2oai/h2o-dev/commit/df222484f4bd4a48b7e1ca896b0e0c89bcf534b2)

#####Algorithms
- Ported matrix multiply over and connected it to rapids [(github)](https://github.com/h2oai/h2o-dev/commit/7361da8ff7e290b4bc3bdcc476d398147bf3d40e)

####Enhancements 

#####UI
- Allow user to specify (the log of) the number of rows per chunk for a new constant chunk; use this new function in CreateFrame [(github)](https://github.com/h2oai/h2o-dev/commit/3a35f88405a378391756d0550da5946ae59ba8f4)
- Make CreateFrame non-blocking, now displays progress bar in Flow [(github)](https://github.com/h2oai/h2o-dev/commit/991bfd8491e6b72d953b4539e7ba4973fa738a7c)
- Add row and column count to H2OFrame show method [(github)](https://github.com/h2oai/h2o-dev/commit/b541d092e5db83ac810ba9b5dab3c0e7e0053938)
- Admin watermeter page [(PUBDEV-234)](https://0xdata.atlassian.net/browse/PUBDEV-234)
- Admin stack trace [(PUBDEV-228)](https://0xdata.atlassian.net/browse/PUBDEV-228)
- Admin profile [(PUBDEV-227)](https://0xdata.atlassian.net/browse/PUBDEV-227)
- Flow: Add download logs in UI [(PUBDEV-204)](https://0xdata.atlassian.net/browse/PUBDEV-204)
- Need shutdown, minimally like h2o [(PUBDEV-74)](https://0xdata.atlassian.net/browse/PUBDEV-74)

#####API
- Changed 2 to 3 for JSON requests [(github)](https://github.com/h2oai/h2o-dev/commit/5dec9669cb71cf0e9f39154aef47403c82656aaf)
- Rename some more fields per consistency (`max_iters` changed to `max_iterations`, `_iters` to `_iterations`, `_ncats` to `_categorical_column_count`, `_centersraw` to `centers_raw`, `_avgwithinss` to `avg_within_ss`, `_withinmse` to `within_mse`) [(github)](https://github.com/h2oai/h2o-dev/commit/5dec9669cb71cf0e9f39154aef47403c82656aaf)
- Changed K-Means output parameters (`withinmse` to `within_mse`, `avgss` to `avg_ss`, `avgbetweenss` to `avg_between_ss`) [(github)](https://github.com/h2oai/h2o-dev/commit/cd24020b03c772c3ffcde9d97f84687cf1c32ce2)
- Remove default field values from DeepLearning parameters schema, since they come from the backing class [(github)](https://github.com/h2oai/h2o-dev/commit/ac1c8bb1c19d5a18d38463c25a2e4e785a71a0cc)
- Add @API help annotation strings to JSON model output [(PUBDEV-216)](https://0xdata.atlassian.net/browse/PUBDEV-216)

#####Algorithms
- Minor fix in rapids matrix multiplicaton [(github)](https://github.com/h2oai/h2o-dev/commit/a5d171ae4de00ce62768731781317a57074f0a09)
- Updated sparse chunk to cut off binary search for prefix/suffix zeros [(github)](https://github.com/h2oai/h2o-dev/commit/61f07672a1c7511e6e860488f6800341431627a1)
- Updated L_BFGS for GLM - warm-start solutions during lambda search, correctly pass current lambda value, added column-based gradient task [(github)](https://github.com/h2oai/h2o-dev/commit/b954c40c27cf22a56fd2995ae238fe6c18fba9bb)
- Fix model parameters' default values in the metadata [(github)](https://github.com/h2oai/h2o-dev/commit/dc0ac668c396e4c33ea6cedd304b0c04eb391755) 
- Set default value of k = number of clusters to 1 for K-Means [(PUBDEV-251)](https://0xdata.atlassian.net/browse/PUBDEV-251)

#####System
- Reject any training data with non-numeric values from KMeans model building [(github)](https://github.com/h2oai/h2o-dev/commit/52dcc2275c733f98fdfdfb430e02341e90a68063)

####Bug Fixes

#####API
- Fixed isSparse call for constant chunks [(github)](https://github.com/h2oai/h2o-dev/commit/1debf0d612d40f9707b43781c4561b87ee93f2df)
- Fixed sparse interface of constant chunks (no nonzero if const 1= 0) [(github)](https://github.com/h2oai/h2o-dev/commit/db16d595e654cdb356810681e272e0e0175e89a7)

#####System
- Typeahead for folder contents apparently requires trailing "/" [(github)](https://github.com/h2oai/h2o-dev/commit/53331a3bccb499a905d39870dae0c46c9883492a)
- Fix build and instructions for R install.packages() style of installation; Note we only support source installs now [(github)](https://github.com/h2oai/h2o-dev/commit/cad188739fca3a482a1358093b2e22284d64abc2)
- Fixed R test runner h2o package install issue that caused it to fail to install on dev builds [(github)](https://github.com/h2oai/h2o-dev/commit/e83d0c97ed13ace4d7f36a3b9a53a4792042ab95)
 
---

###0.1.18.1013 - 1/14/15

####New Features

#####UI 

- Admin timeline [(PUBDEV-226)](https://0xdata.atlassian.net/browse/PUBDEV-226)
- Admin cluster status [(PUBDEV-225)](https://0xdata.atlassian.net/browse/PUBDEV-225)
- Markdown cells should auto run when loading a saved Flow notebook [(PUBDEV-87)](https://0xdata.atlassian.net/browse/PUBDEV-87)
- Complete About page to include info about the H2O version [(PUBDEV-223)](https://0xdata.atlassian.net/browse/PUBDEV-223)

####Enhancements 

#####Algorithms

- Flow: Implement model output for GBM [(PUBDEV-119)](https://0xdata.atlassian.net/browse/PUBDEV-119)

---

###0.1.20.1016 - 12/28/14
- Added ip_port field in node json output for Cloud query [(github)](https://github.com/h2oai/h2o-dev/commit/641777855bc9f2c77d0d212eb3a8805452a01073)

---

