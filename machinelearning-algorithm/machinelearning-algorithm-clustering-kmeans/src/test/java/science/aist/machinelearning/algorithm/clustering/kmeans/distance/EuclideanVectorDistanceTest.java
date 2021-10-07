package science.aist.machinelearning.algorithm.clustering.kmeans.distance;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * <p>Tests {@link EuclideanVectorDistance}</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class EuclideanVectorDistanceTest {
    @Test
    public void test() {
        // given
        EuclideanVectorDistance evd = new EuclideanVectorDistance();
        double[] a1 = new double[]{1, 9, 23, 83};
        double[] a2 = new double[]{4, 17, 53, 76};

        // when
        double distance = evd.calculateDistance(a1, a2);

        // then
        Assert.assertEquals(distance, 31.96, 0.01);
    }
}