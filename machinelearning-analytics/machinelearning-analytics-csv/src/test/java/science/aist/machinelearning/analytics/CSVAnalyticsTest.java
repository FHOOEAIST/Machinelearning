package science.aist.machinelearning.analytics;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import science.aist.machinelearning.core.analytics.Analytics;

import java.io.File;
import java.util.Arrays;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class CSVAnalyticsTest {

    private final Analytics analytics = new CSVAnalytics();

    private File[] findFiles() {
        File f = new File(".");
        return f.listFiles((dir, name) -> name.startsWith("AlgRun_"));
    }

    @BeforeMethod
    @AfterMethod
    public void clean() {
        for (File f : findFiles()) {
            f.delete();
        }
    }

    @Test
    public void testFullAnalyticsRun() {
        // given nothing

        // when
        analytics.startAnalytics();
        analytics.logParam("paramA", "valueA");
        analytics.logAlgorithmStepHeaders(Arrays.asList("a", "b", "c"));
        analytics.logAlgorithmStep(Arrays.asList("a1", "b1", "c1"));
        analytics.logAlgorithmStep(Arrays.asList("a2", "b2", "c2"));
        analytics.finishAnalytics();

        // then
        File[] foundFiles = findFiles();
        Assert.assertNotNull(foundFiles);
        Assert.assertEquals(foundFiles.length, 1);
    }


}
