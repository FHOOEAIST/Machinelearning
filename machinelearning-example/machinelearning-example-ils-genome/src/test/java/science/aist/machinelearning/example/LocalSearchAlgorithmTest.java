package science.aist.machinelearning.example;


import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ils.IterativeLocalSearchAlgorithm;
import science.aist.machinelearning.algorithm.localsearch.LocalSearch;
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
 * Unittest for the {@link IterativeLocalSearchAlgorithm}
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class LocalSearchAlgorithmTest {

    private final IterativeLocalSearchAlgorithm<Element[], Element[]> algorithm = new IterativeLocalSearchAlgorithm<>();

    private final LocalSearch<Element[], Element[]> localSearch = new LocalSearch<>();

    private final RandomGeneMutator mutator = new RandomGeneMutator();

    private final SolutionCreator solutionCreator = new OneToOneSolutionCreator<>();

    private final RandomGeneCreator geneCreator = new RandomGeneCreator();

    private final GenericEvaluatorImpl<Element[], Element[]> evaluator = new GenericEvaluatorImpl<>();

    private final ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

    @BeforeClass
    public void setUp() {

        LoggingConf.setLoggingToRootLevel();

        // configure mutato
        mutator.setEvaluator(evaluator);
        mutator.setMutationsPerSolution(1);

        // configure evaluator
        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(geneCreator);

        localSearch.setMaximumGenerations(100);
        localSearch.setMutator(mutator);
        localSearch.setEvaluator(evaluator);

        algorithm.setMaximumGenerations(1000);
        algorithm.setEvaluator(evaluator);
        algorithm.setSolutionCreator(solutionCreator);
        algorithm.setMutator(mutator);
        algorithm.setSearchAlgorithm(localSearch);

        algorithm.getOptions();
    }

    @Test
    public void localSearchTest() {
        // given
        char[] legalValues = {'A', 'C', 'G', 'T'};
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
    public void localSearchTestWithAnalytics() {
        // given
        CSVAnalytics analytics = new CSVAnalytics();
        algorithm.setAnalytics(analytics);
        localSearch.setAnalytics(analytics);

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
