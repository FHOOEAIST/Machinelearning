package science.aist.machinelearning.analytics.graph.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.analytics.GraphAnalytics;
import science.aist.machinelearning.analytics.graph.ProblemGeneRepository;
import science.aist.machinelearning.analytics.graph.it.nodes.NodiestNode;
import science.aist.machinelearning.analytics.graph.nodes.AnalyticsNode;
import science.aist.machinelearning.analytics.graph.nodes.StepNode;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.neo4j.Neo4jRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
@ContextConfiguration(locations = {"classpath*:testRepositoryConfig.xml"})
public class GraphAnalyticsTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private GraphAnalytics analytics;

    @Autowired
    private Neo4jRepository<AnalyticsNode, Long> analyticsRepository;

    @Autowired
    private Neo4jRepository<StepNode, Long> stepRepository;

    @Autowired
    private Neo4jRepository<Problem, Long> problemRepository;

    @Autowired
    private ProblemGeneRepository problemGeneRepository;

    @Autowired
    private Neo4jRepository<Solution, Long> solutionRepository;

    @Autowired
    private Neo4jRepository<SolutionGene, Long> solutionGeneRepository;

    @Autowired
    private Neo4jRepository<Cachet, Long> cachetRepository;

    @BeforeClass
    @AfterClass
    public void cleanDb() {
        analyticsRepository.deleteAll();
        stepRepository.deleteAll();
        problemRepository.deleteAll();
        solutionRepository.deleteAll();
        solutionGeneRepository.deleteAll();
        problemGeneRepository.deleteAll();
        cachetRepository.deleteAll();
    }

    @Test
    public void testStartAnalytics() {
        // given

        // when
        analytics.startAnalytics();

        // then
        int count = 0;
        for (AnalyticsNode x : analyticsRepository.findAll()) {
            count++;
        }
        Assert.assertEquals(count, 1);
    }

    @Test(dependsOnMethods = "testStartAnalytics")
    public void testAddParam() {
        // given

        // when
        analytics.logParam("populationSize", "1234");
        analytics.logParam("generations", "321");

        // then
        AnalyticsNode node = analyticsRepository.findAll().iterator().next();
        Assert.assertEquals(node.getParameters().size(), 2);
        Assert.assertTrue(node.getParameters().containsKey("populationSize"));
        Assert.assertTrue(node.getParameters().containsKey("generations"));
        Assert.assertEquals(node.getParameters().get("populationSize"), "1234");
        Assert.assertEquals(node.getParameters().get("generations"), "321");
    }

    @Test(dependsOnMethods = "testStartAnalytics")
    public void testAddStepHeaders() {
        // given
        List<String> headers = new ArrayList<>();
        headers.add("best Quality");
        headers.add("average Quality");
        headers.add("worst Quality");

        // when
        analytics.logAlgorithmStepHeaders(headers);

        // then
        // nothing, we just want no error. We can only check the headers with the addStep method
    }

    @Test(dependsOnMethods = "testAddStepHeaders")
    public void testAddStep() {
        // given
        List<String> values = new ArrayList<>();
        values.add("1");
        values.add("3");
        values.add("5");

        // when
        analytics.logAlgorithmStep(values);

        // then
        int count = 0;
        for (StepNode x : stepRepository.findAll()) {
            count++;
        }
        Assert.assertEquals(count, 1);
        StepNode node = stepRepository.findAll().iterator().next();
        Assert.assertNotNull(node.getTime());
        Assert.assertEquals(node.getParameters().size(), 3);
        Assert.assertTrue(node.getParameters().containsKey("best Quality"));
        Assert.assertTrue(node.getParameters().containsKey("average Quality"));
        Assert.assertTrue(node.getParameters().containsKey("worst Quality"));
        Assert.assertEquals(node.getParameters().get("best Quality"), "1");
        Assert.assertEquals(node.getParameters().get("average Quality"), "3");
        Assert.assertEquals(node.getParameters().get("worst Quality"), "5");
    }

    @Test(dependsOnMethods = "testAddStep")
    public void testAddStep2() {
        // given
        List<String> values = new ArrayList<>();
        values.add("1");

        // when
        analytics.logAlgorithmStep(values);

        // then
        int count = 0;
        for (StepNode x : stepRepository.findAll()) {
            count++;
        }
        Assert.assertEquals(count, 2);
    }

    @Test(dependsOnMethods = "testAddStep2")
    public void testAddStep3() {
        // given
        List<String> values = new ArrayList<>();
        values.add("0");

        // when
        analytics.logAlgorithmStep(values);

        // then
        int count = 0;
        for (StepNode x : stepRepository.findAll()) {
            count++;
        }
        Assert.assertEquals(count, 3);
        AnalyticsNode analyticsNode = analyticsRepository.findAll().iterator().next();
        Assert.assertEquals(analyticsNode.getSteps().size(), 3);
    }

    @Test(dependsOnMethods = "testStartAnalytics")
    public void testLogProblem() {
        // given
        Problem<NodiestNode> problem = new Problem<>();
        List<ProblemGene<NodiestNode>> genes = new ArrayList<>();
        genes.add(new ProblemGene<>(new NodiestNode("gene1")));
        genes.add(new ProblemGene<>(new NodiestNode("gene2")));
        problem.setProblemGenes(genes);

        // when
        analytics.logProblem(problem);

        // then
        int count = 0;
        for (Problem x : problemRepository.findAll()) {
            count++;
        }
        Assert.assertEquals(count, 1);
        Problem node = problemRepository.findAll().iterator().next();
        Assert.assertNotNull(node.getProblemGenes());
        Assert.assertEquals(node.getProblemGenes().size(), 2);
        Assert.assertNotNull(((ProblemGene) node.getProblemGenes().iterator().next()).getDescription());
        AnalyticsNode analyticsNode = analyticsRepository.findAll().iterator().next();
        Assert.assertEquals(analyticsNode.getProblem().getId(), node.getId());
    }

    @Test(dependsOnMethods = "testStartAnalytics")
    public void testLogSolution() {
        // given
        Solution<NodiestNode, NodiestNode> solution = new Solution<>();
        solution.getCachets().add(new Cachet(1.0, "test"));
        solution.setQuality(5.0);
        List<ProblemGene<NodiestNode>> genes = new ArrayList<>();
        genes.add(new ProblemGene<>(new NodiestNode("gene1")));
        genes.add(new ProblemGene<>(new NodiestNode("gene2")));
        solution.addGene(new SolutionGene<>(new NodiestNode("solution"), genes));

        // when
        analytics.logSolution(solution);

        // then
        int count = 0;
        for (Solution x : solutionRepository.findAll()) {
            count++;
        }
        Assert.assertEquals(count, 1);
        Solution node = solutionRepository.findAll().iterator().next();
        Assert.assertNotNull(node.getSolutionGenes());
        Assert.assertEquals(node.getQuality(), 5.0);
        Assert.assertEquals(node.getSolutionGenes().size(), 1);
        SolutionGene geneNode = (SolutionGene) node.getSolutionGenes().iterator().next();
        geneNode = solutionGeneRepository.findById(geneNode.getId()); // repo only resolves 1st level relations; reload 2nd level from here
        Assert.assertNotNull(geneNode.getDescription());
        Assert.assertNotNull(geneNode.getProblemGenes());
        Assert.assertEquals(geneNode.getProblemGenes().size(), 2);
        Assert.assertNotNull(node.getCachets());
        Assert.assertEquals(node.getCachets().size(), 1);
        Assert.assertEquals(((Cachet) node.getCachets().iterator().next()).getQuality(), 1.0);
        AnalyticsNode analyticsNode = analyticsRepository.findAll().iterator().next();
        Assert.assertEquals(analyticsNode.getSolution().getId(), node.getId());
    }

}
