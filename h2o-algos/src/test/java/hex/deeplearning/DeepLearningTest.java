package hex.deeplearning;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import water.*;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.Log;

import java.util.Arrays;

public class DeepLearningTest extends TestUtil {
  @BeforeClass public static void stall() { stall_till_cloudsize(1); }

  abstract static class PrepData { abstract int prep(Frame fr); }

  static String[] s(String...arr)  { return arr; }
  static long[]   a(long ...arr)   { return arr; }
  static long[][] a(long[] ...arr) { return arr; }

  @Test public void testClassIris1() throws Throwable {

    // iris ntree=1
    basicDLTest_Classification(
            "./smalldata/iris/iris.csv", "iris.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                return fr.numCols() - 1;
              }
            },
            1,
            a(a(0, 50, 0),
                    a(0, 8, 42),
                    a(0, 1, 49)),
            s("Iris-setosa", "Iris-versicolor", "Iris-virginica"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testClassIris5() throws Throwable {
    // iris ntree=50
    basicDLTest_Classification(
            "./smalldata/iris/iris.csv", "iris5.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                return fr.numCols() - 1;
              }
            },
            5,
            a(a(50, 0, 0),
                    a(0, 24, 26),
                    a(0, 2, 48)),
            s("Iris-setosa", "Iris-versicolor", "Iris-virginica"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testClassCars1() throws Throwable {
    // cars ntree=1
    basicDLTest_Classification(
            "./smalldata/junit/cars.csv", "cars.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
              }
            },
            1,
            a(a(0, 3, 0, 1, 0),
                    a(0, 191, 3, 13, 0),
                    a(0, 2, 1, 0, 0),
                    a(0, 57, 0, 27, 0),
                    a(0, 4, 0, 23, 81)),
            s("3", "4", "5", "6", "8"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testClassCars5() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/junit/cars.csv", "cars5.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
              }
            },
            5,
            a(a(0, 4, 0, 0, 0),
                    a(0, 205, 0, 2, 0),
                    a(0, 2, 0, 1, 0),
                    a(0, 14, 0, 69, 1),
                    a(0, 0, 0, 5, 103)),
            s("3", "4", "5", "6", "8"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testConstantCols() throws Throwable {
    try {
      basicDLTest_Classification(
              "./smalldata/poker/poker100", "poker.hex",
              new PrepData() {
                @Override
                int prep(Frame fr) {
                  for (int i = 0; i < 7; i++) {
                    fr.remove(3).remove();
                  }
                  return 3;
                }
              },
              1,
              null,
              null,
              DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
      Assert.fail();
    } catch( H2OModelBuilderIllegalArgumentException iae ) {
    /*pass*/
    }
  }

  @Ignore @Test public void testBadData() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/junit/drf_infinities.csv", "infinitys.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                return fr.find("DateofBirth");
              }
            },
            1,
            a(a(6, 0),
                    a(9, 1)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  //@Test
  public void testCreditSample1() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/kaggle/creditsample-training.csv.gz", "credit.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("MonthlyIncome").remove();
                return fr.find("SeriousDlqin2yrs");
              }
            },
            1,
            a(a(46294, 202),
                    a(3187, 107)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testCreditProstate1() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/logreg/prostate.csv", "prostate.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
              }
            },
            1,
            a(a(57, 170),
              a(16, 137)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testCreditProstateReLUDropout() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/logreg/prostate.csv", "prostateReLUDropout.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
              }
            },
            1,
            a(a(5, 222),
              a(0, 153)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.RectifierWithDropout);

  }

  @Test public void testCreditProstateTanh() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/logreg/prostate.csv", "prostateTanh.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
              }
            },
            1,
            a(a(147,  80),
              a( 32, 121)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Tanh);
  }

  @Test public void testCreditProstateTanhDropout() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/logreg/prostate.csv", "prostateTanhDropout.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
              }
            },
            1,
            a(a(58, 169),
              a(12, 141)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.TanhWithDropout);
  }

  @Test public void testCreditProstateMaxout() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/logreg/prostate.csv", "prostateMaxout.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
              }
            },
            1,
            a(a(56, 171),
              a( 8, 145)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Maxout);
  }

  @Test public void testCreditProstateMaxoutDropout() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/logreg/prostate.csv", "prostateMaxoutDropout.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
              }
            },
            1,
            a(a(58, 169),
              a(13, 140)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.MaxoutWithDropout);
  }

  @Test public void testCreditProstateRegression1() throws Throwable {
    basicDLTest_Regression(
            "./smalldata/logreg/prostate.csv", "prostateRegression.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
              }
            },
            1,
            50.644808115120775,
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testCreditProstateRegressionTanh() throws Throwable {
    basicDLTest_Regression(
            "./smalldata/logreg/prostate.csv", "prostateRegressionTanh.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
              }
            },
            1,
            43.23511220404915,
            DeepLearningModel.DeepLearningParameters.Activation.Tanh);

  }

  @Test public void testCreditProstateRegressionMaxout() throws Throwable {
    basicDLTest_Regression(
            "./smalldata/logreg/prostate.csv", "prostateRegressionMaxout.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
              }
            },
            1,
            42.894661346549356,
            DeepLearningModel.DeepLearningParameters.Activation.Maxout);

  }

  @Test public void testCreditProstateRegression5() throws Throwable {
    basicDLTest_Regression(
            "./smalldata/logreg/prostate.csv", "prostateRegression5.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
              }
            },
            5,
            43.187341679697695,
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testCreditProstateRegression50() throws Throwable {
    basicDLTest_Regression(
            "./smalldata/logreg/prostate.csv", "prostateRegression50.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
              }
            },
            50,
            39.28708964178392,
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);

  }
  @Test public void testAlphabet() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/gbm_test/alphabet_cattest.csv", "alphabetClassification.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                return fr.find("y");
              }
            },
            10,
            a(a(2080, 0),
                    a(0, 2080)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }
  @Test public void testAlphabetRegression() throws Throwable {
    basicDLTest_Regression(
            "./smalldata/gbm_test/alphabet_cattest.csv", "alphabetRegression.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                return fr.find("y");
              }
            },
            10,
            0.012242754628809,
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  @Ignore  //1-vs-5 node discrepancy (parsing into different number of chunks?)
  @Test public void testAirlines() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/airlines/allyears2k_headers.zip", "airlines.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                for (String s : new String[]{
                        "DepTime", "ArrTime", "ActualElapsedTime",
                        "AirTime", "ArrDelay", "DepDelay", "Cancelled",
                        "CancellationCode", "CarrierDelay", "WeatherDelay",
                        "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed"
                }) {
                  fr.remove(s).remove();
                }
                return fr.find("IsDepDelayed");
              }
            },
            7,
            a(a(4051, 15612), //for 5-node
                    a(1397, 20322)),
//            a(a(4396, 15269), //for 1-node
//              a(1740, 19993)),
            s("NO", "YES"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }

  @Ignore //PUBDEV-1001
  @Test public void testCzechboard() throws Throwable {
    basicDLTest_Classification(
            "./smalldata/gbm_test/czechboard_300x300.csv", "czechboard_300x300.hex",
            new PrepData() {
              @Override
              int prep(Frame fr) {
                Vec resp = fr.remove("C2");
                fr.add("C2", resp.toEnum());
                resp.remove();
                return fr.find("C3");
              }
            },
            1,
            a(a(1, 44999),
              a(0, 45000)),
            s("0", "1"),
            DeepLearningModel.DeepLearningParameters.Activation.Rectifier);
  }


  // Put response as the last vector in the frame and return possible frames to clean up later
  static Vec unifyFrame(DeepLearningModel.DeepLearningParameters drf, Frame fr, PrepData prep, boolean classification) {
    int idx = prep.prep(fr);
    if( idx < 0 ) { idx = ~idx; }
    String rname = fr._names[idx];
    drf._response_column = fr.names()[idx];

    Vec resp = fr.vecs()[idx];
    Vec ret = null;
    if (classification) {
      ret = fr.remove(idx);
      fr.add(rname,resp.toEnum());
    } else {
      fr.remove(idx);
      fr.add(rname,resp);
    }
    return ret;
  }

  public void basicDLTest_Classification(String fnametrain, String hexnametrain, PrepData prep, int epochs, long[][] expCM, String[] expRespDom, DeepLearningModel.DeepLearningParameters.Activation activation) throws Throwable { basicDL(fnametrain, hexnametrain, null, prep, epochs, expCM, expRespDom, -1, new int[]{10,10}, 1e-5, true, activation); }
  public void basicDLTest_Regression(String fnametrain, String hexnametrain, PrepData prep, int epochs, double expMSE, DeepLearningModel.DeepLearningParameters.Activation activation) throws Throwable { basicDL(fnametrain, hexnametrain, null, prep, epochs, null, null, expMSE, new int[]{10,10}, 1e-5, false, activation); }

  public void basicDL(String fnametrain, String hexnametrain, String fnametest, PrepData prep, int epochs, long[][] expCM, String[] expRespDom, double expMSE, int[] hidden, double l1, boolean classification, DeepLearningModel.DeepLearningParameters.Activation activation) throws Throwable {
    Scope.enter();
    DeepLearningModel.DeepLearningParameters dl = new DeepLearningModel.DeepLearningParameters();
    Frame frTest = null, pred = null;
    Frame frTrain = null;
    Frame test = null, res = null;
    DeepLearningModel model = null;
    try {
      frTrain = parse_test_file(fnametrain);
      Vec removeme = unifyFrame(dl, frTrain, prep, classification);
      if (removeme != null) Scope.track(removeme._key);
      DKV.put(frTrain._key, frTrain);
      // Configure DL
      dl._train = frTrain._key;
      dl._response_column = ((Frame)DKV.getGet(dl._train)).lastVecName();
      dl._seed = (1L<<32)|2;
      dl._model_id = Key.make("DL_model_" + hexnametrain);
      dl._reproducible = true;
      dl._epochs = epochs;
      dl._activation = activation;
      dl._export_weights_and_biases = true;
      dl._hidden = hidden;
      dl._l1 = l1;

      // Invoke DL and block till the end
      DeepLearning job = null;
      try {
        job = new DeepLearning(dl);
        // Get the model
        model = job.trainModel().get();
        Log.info(model._output);
      } finally {
        if (job != null) job.remove();
      }
      Assert.assertTrue(job._state == Job.JobState.DONE); //HEX-1817

      hex.ModelMetrics mm;
      if (fnametest != null) {
        frTest = parse_test_file(fnametest);
        pred = model.score(frTest);
        mm = hex.ModelMetrics.getFromDKV(model, frTest);
        // Check test set CM
      } else {
        pred = model.score(frTrain);
        mm = hex.ModelMetrics.getFromDKV(model, frTrain);
      }

      test = parse_test_file(fnametrain);
      res = model.score(test);

      if (classification) {
        Assert.assertTrue("Expected: " + Arrays.deepToString(expCM) + ", Got: " + Arrays.deepToString(mm.cm()._cm),
                Arrays.deepEquals(mm.cm()._cm, expCM));

        String[] cmDom = model._output._domains[model._output._domains.length - 1];
        Assert.assertArrayEquals("CM domain differs!", expRespDom, cmDom);
        Log.info("\nTraining CM:\n" + mm.cm().toASCII());
        Log.info("\nTraining CM:\n" + hex.ModelMetrics.getFromDKV(model, test).cm().toASCII());
      } else {
        Assert.assertTrue("Expected: " + expMSE + ", Got: " + mm.mse(), expMSE == mm.mse());
        Log.info("\nOOB Training MSE: " + mm.mse());
        Log.info("\nTraining MSE: " + hex.ModelMetrics.getFromDKV(model, test).mse());
      }

      hex.ModelMetrics.getFromDKV(model, test);

      // Build a POJO, validate same results
      Assert.assertTrue(model.testJavaScoring(test,res,1e-5));

    } finally {
      if (frTrain!=null) frTrain.remove();
      if (frTest!=null) frTest.remove();
      if( model != null ) model.delete(); // Remove the model
      if( pred != null ) pred.delete();
      if( test != null ) test.delete();
      if( res != null ) res.delete();
      Scope.exit();
    }
  }

<<<<<<< HEAD
  @Test public void testNoRowWeights() {
    Frame tfr=null, vfr=null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/no_weights.csv");
      DKV.put(tfr);
      DeepLearningModel.DeepLearningParameters parms = new DeepLearningModel.DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._l1 = 0.1;
      parms._epochs = 0.1;
      parms._hidden = new int[]{1};
      parms._classification_stop = -1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
//      assertEquals(0.8518518518518519, mm.auc()._auc, 1e-8);
      assertEquals(0.7222222222222222, mm.auc()._auc, 1e-8);

      double mse = dl._output.train_metrics.mse();
//      assertEquals(0.21757859226445403, mse, 1e-6); //Note: better results than non-shuffled
      assertEquals(0.31894419928669016, mse, 1e-6); //Note: better results than non-shuffled

      job.remove();
      dl.delete();
    } finally{
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
    }
    Scope.exit();
  }

  @Test public void testNoRowWeightsShuffled() {
    Frame tfr=null, vfr=null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/no_weights_shuffled.csv");
      DKV.put(tfr);
      DeepLearningModel.DeepLearningParameters parms = new DeepLearningModel.DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._l1 = 0.1;
      parms._epochs = 0.1;
      parms._hidden = new int[]{1};
      parms._classification_stop = -1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
//      assertEquals(0.8518518518518519, mm.auc()._auc, 1e-8);
      assertEquals(0.7222222222222222, mm.auc()._auc, 1e-8);

      double mse = dl._output.train_metrics.mse();
      assertEquals(0.37203048338693356, mse, 1e-6); //Shuffling changes results!
      job.remove();
      dl.delete();
    } finally{
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
    }
    Scope.exit();
  }
=======
  @Ignore
  @Test public void testWhatever() {
    DeepLearningModel.DeepLearningParameters dl;
    Frame frTrain = null;
    DeepLearningModel model = null;
    while(true) {
      dl = new DeepLearningModel.DeepLearningParameters();
      Scope.enter();
      try {
        frTrain = parse_test_file("./smalldata/covtype/covtype.20k.data");
        Vec resp = frTrain.lastVec().toEnum();
        frTrain.remove(frTrain.vecs().length-1);
        frTrain.add("Response", resp);
        // Configure DL
        dl._train = frTrain._key;
        dl._response_column = ((Frame) DKV.getGet(dl._train)).lastVecName();
        dl._seed = 1234;
        dl._reproducible = true;
        dl._epochs = 0.0001;
        dl._export_weights_and_biases = true;
        dl._hidden = new int[]{188, 191};

        // Invoke DL and block till the end
        DeepLearning job = null;
        try {
          job = new DeepLearning(dl);
          // Get the model
          model = job.trainModel().get();
          Log.info(model._output);
        } finally {
          if (job != null) job.remove();
        }
        Assert.assertTrue(job._state == Job.JobState.DONE); //HEX-1817
>>>>>>> arno_jenkins


<<<<<<< HEAD
    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/weights_all_ones.csv");
      DKV.put(tfr);
      DeepLearningModel.DeepLearningParameters parms = new DeepLearningModel.DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._row_weights_column = "weight";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._classification_stop = -1;
      parms._l1 = 0.1;
      parms._hidden = new int[]{1};
      parms._epochs = 0.1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
//      assertEquals(0.8518518518518519, mm.auc()._auc, 1e-8);
      assertEquals(0.7222222222222222, mm.auc()._auc, 1e-8);

      double mse = dl._output.train_metrics.mse();
//      assertEquals(0.21757859226445403, mse, 1e-6); //Note: better results than non-shuffled
      assertEquals(0.31894419928669016, mse, 1e-6); //Note: better results than non-shuffled
      job.remove();
      dl.delete();
    } finally{
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
=======
      } finally {
        if (frTrain != null) frTrain.remove();
        if (model != null) model.delete();
        Scope.exit();
      }
>>>>>>> arno_jenkins
    }
  }

<<<<<<< HEAD
  @Test public void testRowWeights() {
    Frame tfr=null, vfr=null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/weights.csv");
      DKV.put(tfr);
      DeepLearningModel.DeepLearningParameters parms = new DeepLearningModel.DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._row_weights_column = "weight";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._classification_stop = -1;
      parms._l1 = 0.1;
      parms._hidden = new int[]{1};
      parms._epochs = 0.1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      dl.score(parms.train(), parms.train().vec(parms._row_weights_column));
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
//      assertEquals(0.8518518518518519, mm.auc()._auc, 1e-8);
      assertEquals(0.7222222222222222, mm.auc()._auc, 1e-8);

      double mse = dl._output.train_metrics.mse();
//      assertEquals(0.21757859226445403, mse, 1e-6); //Note: better results than non-shuffled
      assertEquals(0.31894419928669016, mse, 1e-6); //Note: better results than non-shuffled
      job.remove();
      dl.delete();
    } finally{
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
    }
    Scope.exit();
  }
=======
>>>>>>> arno_jenkins
}
