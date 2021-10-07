package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ga.GeneticAlgorithm;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.analytics.CSVAnalytics;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.logging.LoggingConf;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class GeneticAlgorithmTest {

    private final GeneticAlgorithm<Element[], Element[]> algorithm = new GeneticAlgorithm<>();

    private final GenericEvaluatorImpl<Element[], Element[]> evaluator = new GenericEvaluatorImpl<>();

    private final ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

    private final SolutionCreator<Element[], Element[]> solutionCreator = new OneToOneSolutionCreator<>();

    private final RandomGeneCreator geneCreator = new RandomGeneCreator();

    private final UniformCrossover crossover = new UniformCrossover();

    private final TournamentSelector<Element[], Element[]> tournamentSelector = new TournamentSelector<>();

    private final RandomGeneMutator mutator = new RandomGeneMutator();

    @BeforeClass
    public void setUp() {

        LoggingConf.setLoggingToRootLevel();

        // configure mutator
        mutator.setEvaluator(evaluator);
        mutator.setMutationsPerSolution(1);

        // configure selector
        tournamentSelector.setTournamentSize(10);

        // configure evaluator
        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(geneCreator);

        // configure alg
        algorithm.setElites(1);
        algorithm.setMaximumGenerations(100);
        algorithm.setMutationProbability(0.05);
        algorithm.setPopulationSize(100);

        algorithm.setEvaluator(evaluator);
        algorithm.setSolutionCreator(solutionCreator);
        algorithm.setCrossover(crossover);
        algorithm.setGenMutator(mutator);
        algorithm.setSelector(tournamentSelector);
    }

    @Test
    public void test() {
        // given
        Element[] problemVal = new Element[20];
        for (int i = 0; i < 20; i++) {
            if (i >= 15) {
                problemVal[i] = new Element('T');
            } else if (i >= 10) {
                problemVal[i] = new Element('G');
            } else if (i >= 5) {
                problemVal[i] = new Element('C');
            } else {
                problemVal[i] = new Element('A');
            }
        }

        elementEqualityCachet.setTargetSequence("GTACCCGTACCCGTACCCTT");

        List<ProblemGene<Element[]>> problems = new ArrayList<>();
        problems.add(new ProblemGene<>(problemVal));

        Problem<Element[]> problem = new Problem(problems);

        // when
        Solution<Element[], Element[]> s = algorithm.solve(problem);

        // then
        System.out.println("FINAL: " + s.toString() + " " + s.getQuality());
        Assert.assertNotNull(s);
        Assert.assertNotNull(s.getSolutionGenes().get(0).getGene());
        Assert.assertEquals(s.getCachets().size(), 1);
        Assert.assertEquals(s.getCachets().get(0).getQuality(), s.getQuality());
        Assert.assertEquals(s.getSolutionGenes().get(0).getProblemGenes().size(), problem.getProblemGenes().size());
        Assert.assertTrue(s.getQuality() <= 20);
        Assert.assertEquals(s.getQuality(), evaluator.evaluateQuality(s));
    }

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
    public void testWithAnalytics() {
        // given
        algorithm.setAnalytics(new CSVAnalytics());
        Element[] problemVal = new Element[20];
        for (int i = 0; i < 20; i++) {
            if (i >= 15) {
                problemVal[i] = new Element('T');
            } else if (i >= 10) {
                problemVal[i] = new Element('G');
            } else if (i >= 5) {
                problemVal[i] = new Element('C');
            } else {
                problemVal[i] = new Element('A');
            }
        }

        elementEqualityCachet.setTargetSequence("GTACCCGTACCCGTACCCTT");

        List<ProblemGene<Element[]>> problems = new ArrayList<>();
        problems.add(new ProblemGene(problemVal));

        Problem<Element[]> problem = new Problem(problems);

        // when
        Solution<Element[], Element[]> s = algorithm.solve(problem);

        // then
        System.out.println("FINAL: " + s.toString() + " " + s.getQuality());
        Assert.assertNotNull(s);
        Assert.assertNotNull(s.getSolutionGenes().get(0).getGene());
        Assert.assertEquals(s.getCachets().size(), 1);
        Assert.assertEquals(s.getCachets().get(0).getQuality(), s.getQuality());
        Assert.assertEquals(s.getSolutionGenes().get(0).getProblemGenes().size(), problem.getProblemGenes().size());
        Assert.assertTrue(s.getQuality() <= 20);
        Assert.assertEquals(s.getQuality(), evaluator.evaluateQuality(s));
        File[] foundFiles = findFiles();
        Assert.assertNotNull(foundFiles);
        Assert.assertEquals(foundFiles.length, 1);
    }

}
