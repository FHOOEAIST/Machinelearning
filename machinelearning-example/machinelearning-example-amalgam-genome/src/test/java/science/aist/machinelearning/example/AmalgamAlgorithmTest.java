package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.amalgam.AmalgamAlgorithm;
import science.aist.machinelearning.algorithm.ga.GeneticAlgorithm;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.ils.IterativeLocalSearchAlgorithm;
import science.aist.machinelearning.algorithm.localsearch.LocalSearch;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Algorithm;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UnitTestClass for {@link AmalgamAlgorithm}
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamAlgorithmTest {

    //Settings for the LocalSearch
    private final IterativeLocalSearchAlgorithm<Element[], Element[]> iterativeLocalSearchAlgorithm = new IterativeLocalSearchAlgorithm<>();
    private final LocalSearch<Element[], Element[]> localSearch = new LocalSearch<>();

    //Settings for the GeneticAlgorithm
    private final GeneticAlgorithm<Element[], Element[]> geneticAlgorithm = new GeneticAlgorithm<>();
    private final UniformCrossover crossover = new UniformCrossover();
    private final TournamentSelector<Element[], Element[]> tournamentSelector = new TournamentSelector<>();

    //Settings used by both
    private final RandomGeneMutator mutator = new RandomGeneMutator();
    private final SolutionCreator solutionCreator = new OneToOneSolutionCreator<>();
    private final RandomGeneCreator geneCreator = new RandomGeneCreator();
    private final GenericEvaluatorImpl<Element[], Element[]> evaluator = new GenericEvaluatorImpl<>();
    private final ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

    private final AmalgamAlgorithm<Element[], Element[]> amalgamAlgorithm = new AmalgamAlgorithm<>();

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

        // configure local search
        solutionCreator.setGeneCreator(geneCreator);

        localSearch.setMaximumGenerations(10);
        localSearch.setMutator(mutator);
        localSearch.setEvaluator(evaluator);

        iterativeLocalSearchAlgorithm.setMaximumGenerations(100);
        iterativeLocalSearchAlgorithm.setEvaluator(evaluator);
        iterativeLocalSearchAlgorithm.setSolutionCreator(solutionCreator);
        iterativeLocalSearchAlgorithm.setMutator(mutator);
        iterativeLocalSearchAlgorithm.setSearchAlgorithm(localSearch);

        // configure genetic alg
        geneticAlgorithm.setElites(1);
        geneticAlgorithm.setMaximumGenerations(50);
        geneticAlgorithm.setMutationProbability(0.05);
        geneticAlgorithm.setPopulationSize(50);

        geneticAlgorithm.setEvaluator(evaluator);
        geneticAlgorithm.setSolutionCreator(solutionCreator);
        geneticAlgorithm.setCrossover(crossover);
        geneticAlgorithm.setGenMutator(mutator);
        geneticAlgorithm.setSelector(tournamentSelector);

        //configure amalgam algorithm
        List<Algorithm<Element[], Element[]>> algorithms = new ArrayList<>();
        algorithms.add(iterativeLocalSearchAlgorithm);
        algorithms.add(geneticAlgorithm);
        algorithms.add(iterativeLocalSearchAlgorithm);

        amalgamAlgorithm.setAlgorithms(algorithms);
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
        Solution<Element[], Element[]> s = amalgamAlgorithm.solve(problem);

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
}
