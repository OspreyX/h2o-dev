#Upgrading to H2O 3.0

##Why Upgrade? 

H2O 3.0 represents our latest iteration of H2O. It includes many improvements, such as a simplified architecture, faster and more accurate algorithms, and an interactive web UI. 

As of May 15th, 2015, this version will supersede the previous version of H2O. Support for previous versions of H2O will be provided for a limited time, but there will no longer be significant updates to the previous version of H2O. 

For a comparison of H2O and H2O 3.0, please refer to <a href="https://github.com/h2oai/h2o-dev/blob/jessica-dev-docs/h2o-docs/src/product/upgrade/H2OvsH2O-Dev.md" target="_blank">this document</a>. 

##How to Update R Scripts

Due to the numerous enhancements to the H2O package for R to make it more consistent and simplified, some parameters have been renamed or deprecated. 

To assist R users in updating their existing scripts for compatibility with H2O 3.0, a "shim" has been developed. When you run the shim on your script, any deprecated or renamed parameters are identified and a suggested replacement is provided. You can access the shim <a href="https://github.com/h2oai/h2o-dev/blob/9795c401b7be339be56b1b366ffe816133cccb9d/h2o-r/h2o-package/R/shim.R" target="_blank">here</a>.

Additionally, there is a <a href="https://github.com/h2oai/h2o-dev/blob/master/h2o-docs/src/product/upgrade/H2ODevPortingRScripts.md" target="_blank">document</a> available that provides a side-by-side comparison of the differences between versions. 

##Supported Algorithms

H2O 3.0 will soon provide feature parity with previous versions of H2O. Currently, the following algorithms are supported: 

- Deep Learning (DL)
- Distributed Random Forest (DRF)
- Gradient Boosting Machine (GBM)
- Generalized Linear Model (GLM) 
- K-means
- Naive Bayes

##Sparkling Water Support

Sparkling Water is only supported with H2O 3.0. For more information, refer to the <a href="https://github.com/h2oai/sparkling-water/blob/master/README.md" target="_blank">Sparkling Water repo</a>.