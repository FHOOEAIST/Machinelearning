package science.aist.machinelearning.analytics.graph.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import science.aist.machinelearning.analytics.graph.nodes.AnalyticsNode;
import science.aist.machinelearning.analytics.graph.nodes.StepNode;
import science.aist.neo4j.Neo4jRepository;

/**
 * Benchmark test for comparing spring-data with native neo4j Note: We are trying this out on the neo4j server, as
 * performance measurements on the in-memory db have no impact on real world performance for us. Note: We do not yet
 * have automation for this benchmark. Please manually CLEAR the db before starting this test for fair starting
 * conditions
 *
 * @author Oliver Krauss
 * @since 1.0
 */
@ContextConfiguration(locations = {"classpath:repositoryConfig.xml"})
public class GraphAnalyticsBenchmark extends AbstractTestNGSpringContextTests {

    private static final long AMOUNT = 20000L;

    @Autowired
    private Neo4jRepository<AnalyticsNode, Long> analyticsRepository;

    // Note we are intentionally testing single-inserts to compare the CREATE statement
    @Test(enabled = false)
    public void testWriteNodes() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < AMOUNT; i++) {
            // given
            AnalyticsNode node = new AnalyticsNode("TITLE");

            // when
            analyticsRepository.save(node);
        }

        // then
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("This took us " + estimatedTime);
    }

    @Test(enabled = false, dependsOnMethods = "testWriteNodes")
    public void testReadNodes() {
        // given

        // when
        long count = 0;
        long i = 0;
        long startTime = System.currentTimeMillis();

        while (count < AMOUNT) {
            // when
            try {
                analyticsRepository.findById(i);
                count++;
            } catch (Exception e) {
            }

            i++;
        }

        // then
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("This took us " + estimatedTime);
        Assert.assertEquals(count, AMOUNT);
    }

    @Test(enabled = false, dependsOnMethods = "testReadNodes")
    public void testWriteRelationship() {
        // given
        AnalyticsNode node = new AnalyticsNode();

        // when
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < AMOUNT; i++) {
            node.addStep(new StepNode());
            analyticsRepository.save(node);
        }

        // then
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("This took us " + estimatedTime);
    }
}
