package hex.pca;

import hex.DataInfo;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class PCATest extends TestUtil {
  public static final double TOLERANCE = 1e-6;
  @BeforeClass public static void setup() { stall_till_cloudsize(1); }

  @Test public void testArrests() throws InterruptedException, ExecutionException {
    // Results with de-meaned training frame
    double[] stddev = new double[] {83.732400, 14.212402, 6.489426, 2.482790};
    double[][] eigvec = ard(ard(0.04170432, -0.04482166, 0.07989066, -0.99492173),
                            ard(0.99522128, -0.05876003, -0.06756974, 0.03893830),
                            ard(0.04633575, 0.97685748, -0.20054629, -0.05816914),
                            ard(0.07515550, 0.20071807, 0.97408059, 0.07232502));

    // Results with standardized training frame
    double[] stddev_std = new double[] {1.5748783, 0.9948694, 0.5971291, 0.4164494};
    double[][] eigvec_std = ard(ard(-0.5358995, 0.4181809, -0.3412327, 0.64922780),
                                ard(-0.5831836, 0.1879856, -0.2681484, -0.74340748),
                                ard(-0.2781909, -0.8728062, -0.3780158, 0.13387773),
                                ard(-0.5434321, -0.1673186, 0.8177779, 0.08902432));

    Frame train = null;
    try {
      for (DataInfo.TransformType std : new DataInfo.TransformType[] {
              DataInfo.TransformType.DEMEAN,
              DataInfo.TransformType.STANDARDIZE }) {
        PCAModel model = null;
        train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");   // TODO: Move this outside loop
        try {
          PCAModel.PCAParameters parms = new PCAModel.PCAParameters();
          parms._train = train._key;
          parms._k = 4;
          parms._transform = std;
          parms._max_iterations = 1000;

          PCA job = new PCA(parms);
          try {
            model = job.trainModel().get();
          } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
          } finally {
            job.remove();
          }

          if (std == DataInfo.TransformType.DEMEAN) {
            TestUtil.checkStddev(stddev, model._output._std_deviation, TOLERANCE);
            TestUtil.checkEigvec(eigvec, model._output._eigenvectors, TOLERANCE);
          } else if (std == DataInfo.TransformType.STANDARDIZE) {
            TestUtil.checkStddev(stddev_std, model._output._std_deviation, TOLERANCE);
            TestUtil.checkEigvec(eigvec_std, model._output._eigenvectors, TOLERANCE);
          }
        } catch (Throwable t) {
          t.printStackTrace();
          throw new RuntimeException(t);
        } finally {
          if( model != null ) {
            if (model._parms._keep_loading)
              model._parms._loading_key.get().delete();
            model.delete();
          }
        }
      }
    } finally {
      if(train != null) train.delete();
    }
  }

  @Test public void testArrestsScoring() {
    // Results with original training frame
    double[] stddev = new double[] {202.7230564, 27.8322637, 6.5230482, 2.5813652};
    double[][] eigvec = ard(ard(-0.04239181, 0.01616262, -0.06588426, 0.99679535),
                            ard(-0.94395706, 0.32068580, 0.06655170, -0.04094568),
                            ard(-0.30842767, -0.93845891, 0.15496743, 0.01234261),
                            ard(-0.10963744, -0.12725666, -0.98347101, -0.06760284));

    PCA job = null;
    PCAModel model = null;
    Frame train = null, score = null, scoreR = null;
    try {
      train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
      PCAModel.PCAParameters parms = new PCAModel.PCAParameters();
      parms._train = train._key;
      parms._k = 4;
      parms._transform = DataInfo.TransformType.NONE;

      try {
        job = new PCA(parms);
        model = job.trainModel().get();
        TestUtil.checkStddev(stddev, model._output._std_deviation, 1e-5);
        boolean[] flippedEig = TestUtil.checkEigvec(eigvec, model._output._eigenvectors, 1e-5);

        score = model.score(train);
        scoreR = parse_test_file(Key.make("scoreR.hex"), "smalldata/pca_test/USArrests_PCAscore.csv");
        TestUtil.checkProjection(scoreR, score, TOLERANCE, flippedEig);    // Flipped cols must match those from eigenvectors
      } catch (Throwable t) {
        t.printStackTrace();
        throw new RuntimeException(t);
      } finally {
        if (job != null) job.remove();
      }
    } catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException(t);
    } finally {
      if (train != null) train.delete();
      if (score != null) score.delete();
      if (scoreR != null) scoreR.delete();
      if (model != null) {
        if (model._parms._keep_loading)
          model._parms._loading_key.get().delete();
        model.delete();
      }
    }
  }

  @Test public void testGram() {
    double[][] x = ard(ard(1, 2, 3), ard(4, 5, 6));
    double[][] xgram = ard(ard(17, 22, 27), ard(22, 29, 36), ard(27, 36, 45));  // X'X
    double[][] xtgram = ard(ard(14, 32), ard(32, 77));    // (X')'X' = XX'

    double[][] xgram_glrm = PCA.formGram(x, false);
    double[][] xtgram_glrm = PCA.formGram(x, true);
    Assert.assertArrayEquals(xgram, xgram_glrm);
    Assert.assertArrayEquals(xtgram, xtgram_glrm);
  }
}
