package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ga.GeneticAlgorithm;
import science.aist.machinelearning.algorithm.ga.crossover.OnePointCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.ils.IterativeLocalSearchAlgorithm;
import science.aist.machinelearning.algorithm.localsearch.LocalSearch;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Wilfing
 * @since 1.0
 */
public class FisherThompsonTest {

    private final FisherThompsonCachet cachet = new FisherThompsonCachet();

    private final FisherThompsonGeneCreator solutionBuilder = new FisherThompsonGeneCreator();

    private final FisherThompsonMutator mutator = new FisherThompsonMutator();

    //basics

    private final OneToOneSolutionCreator<List<Integer>, Integer> solutionCreator = new OneToOneSolutionCreator<>();

    private final GenericEvaluatorImpl<List<Integer>, Integer> evaluator = new GenericEvaluatorImpl<>();

    //genetic algorithm

    private final GeneticAlgorithm<List<Integer>, Integer> geneticAlgorithm = new GeneticAlgorithm<>();

    private final OnePointCrossover<List<Integer>, Integer> crossover = new OnePointCrossover<>();

    private final TournamentSelector<List<Integer>, Integer> selector = new TournamentSelector<>();

    //iterative local search

    IterativeLocalSearchAlgorithm<List<Integer>, Integer> iterativeLocalSearchAlgorithm = new IterativeLocalSearchAlgorithm<>();

    //local search

    LocalSearch<List<Integer>, Integer> localSearch = new LocalSearch<>();

    /**
     * From: http://www.eii.uva.es/elena/JSSP/InstancesJSSP.htm http://www.eii.uva.es/elena/Elena%20Perez%20Vazquez_archivos/files_optimaJSSP/jobshop1.txt
     * <p>
     * Each instance consists of a line of description, a line containing the number of jobs and the number of machines,
     * and then one line for each job, listing the machine number and processing time for each step of the job. The
     * machines are numbered starting with 0.
     * <p>
     * +++++++++++++++++++++++++++++ Fisher and Thompson 6x6 instance, alternate name (mt06) 6 6 2  1  0  3  1  6  3  7
     * 5  3  4  6 1  8  2  5  4 10  5 10  0 10  3  4 2  5  3  4  5  8  0  9  1  1  4  7 1  5  0  5  2  5  3  3  4  8  5
     * 9 2  9  1  3  4  5  5  4  0  3  3  1 1  3  3  3  5  9  0 10  4  4  2  1 +++++++++++++++++++++++++++++
     */
    int[][] ft06 = {
            {3, 6, 1, 7, 6, 3},
            {10, 8, 5, 4, 10, 10},
            {9, 1, 5, 4, 7, 8},
            {5, 5, 5, 3, 8, 9},
            {3, 3, 9, 1, 5, 4},
            {10, 3, 1, 3, 4, 9},
    };
    int[][] ft06v2 = {
            {1, 3, 6, 7, 3, 6},
            {8, 5, 10, 10, 10, 4},
            {5, 4, 8, 9, 1, 7},
            {5, 5, 5, 3, 8, 9},
            {9, 3, 5, 4, 3, 1},
            {3, 3, 9, 10, 4, 1},
    };
    //optimum = 55

    /**
     * +++++++++++++++++++++++++++++ Fisher and Thompson 10x10 instance, alternate name (mt10) 10 10 0 29 1 78 2  9 3 36
     * 4 49 5 11 6 62 7 56 8 44 9 21 0 43 2 90 4 75 9 11 3 69 1 28 6 46 5 46 7 72 8 30 1 91 0 85 3 39 2 74 8 90 5 10 7
     * 12 6 89 9 45 4 33 1 81 2 95 0 71 4 99 6  9 8 52 7 85 3 98 9 22 5 43 2 14 0  6 1 22 5 61 3 26 4 69 8 21 7 49 9 72
     * 6 53 2 84 1  2 5 52 3 95 8 48 9 72 0 47 6 65 4  6 7 25 1 46 0 37 3 61 2 13 6 32 5 21 9 32 8 89 7 30 4 55 2 31 0
     * 86 1 46 5 74 4 32 6 88 8 19 9 48 7 36 3 79 0 76 1 69 3 76 5 51 2 85 9 11 6 40 7 89 4 26 8 74 1 85 0 13 2 61 6  7
     * 8 64 9 76 5 47 3 52 4 90 7 45 +++++++++++++++++++++++++++++
     */
    int[][] ft10 = {
            {29, 78, 9, 36, 49, 11, 62, 56, 44, 21},
            {43, 28, 90, 69, 75, 46, 46, 72, 30, 11},
            {85, 91, 74, 39, 33, 10, 89, 12, 90, 45},
            {71, 81, 95, 98, 99, 43, 9, 82, 52, 22},
            {6, 22, 14, 26, 69, 61, 53, 49, 21, 72},
            {47, 2, 84, 95, 6, 52, 65, 25, 48, 72},
            {37, 46, 13, 61, 55, 21, 32, 30, 89, 32},
            {86, 46, 31, 79, 32, 74, 88, 36, 19, 48},
            {76, 69, 85, 76, 26, 51, 40, 89, 74, 11},
            {13, 85, 61, 52, 90, 47, 7, 45, 64, 76}
    };
    //optimum = 930

    /**
     * +++++++++++++++++++++++++++++ Fisher and Thompson 20x5 instance, alternate name (mt20) 20 5 0 29 1  9 2 49 3 62 4
     * 44 0 43 1 75 3 69 2 46 4 72 1 91 0 39 2 90 4 12 3 45 1 81 0 71 4  9 2 85 3 22 2 14 1 22 0 26 3 21 4 72 2 84 1 52
     * 4 48 0 47 3  6 1 46 0 61 2 32 3 32 4 30 2 31 1 46 0 32 3 19 4 36 0 76 3 76 2 85 1 40 4 26 1 85 2 61 0 64 3 47 4
     * 90 1 78 3 36 0 11 4 56 2 21 2 90 0 11 1 28 3 46 4 30 0 85 2 74 1 10 3 89 4 33 2 95 0 99 1 52 3 98 4 43 0  6 1 61
     * 4 69 2 49 3 53 1  2 0 95 3 72 4 65 2 25 0 37 2 13 1 21 3 89 4 55 0 86 1 74 4 88 2 48 3 79 1 69 2 51 0 11 3 89 4
     * 74 0 13 1  7 2 76 3 52 4 45 +++++++++++++++++++++++++++++
     */
    int[][] ft20 = {
            {29, 9, 49, 62, 44},
            {43, 75, 46, 69, 72},
            {39, 91, 90, 45, 12},
            {71, 81, 85, 22, 9},
            {26, 22, 14, 21, 72},
            {47, 52, 84, 6, 48},
            {61, 46, 32, 32, 30},
            {32, 46, 31, 19, 46},
            {76, 40, 85, 76, 26},
            {64, 85, 61, 47, 90},
            {11, 78, 21, 36, 56},
            {11, 28, 90, 46, 30},
            {85, 10, 74, 89, 33},
            {99, 52, 95, 98, 43},
            {6, 61, 49, 53, 69},
            {95, 2, 25, 72, 65},
            {37, 21, 13, 89, 55},
            {86, 74, 48, 79, 88},
            {11, 69, 51, 89, 74},
            {13, 7, 76, 52, 45},
    };
    //optimum = 1165


    @BeforeClass
    public void setup() {

        Map<CachetEvaluator<List<Integer>, Integer>, Double> cachets = new HashMap<>();
        cachets.put(cachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(solutionBuilder);

        //genetic
        geneticAlgorithm.setEvaluator(evaluator);
        geneticAlgorithm.setSolutionCreator(solutionCreator);

        geneticAlgorithm.setCrossover(crossover);
        geneticAlgorithm.setSelector(selector);
        geneticAlgorithm.setGenMutator(mutator);

        mutator.setEvaluator(evaluator);

        geneticAlgorithm.setPopulationSize(100);
        geneticAlgorithm.setMaximumGenerations(100);
        geneticAlgorithm.setElites(10);

        //local search

        localSearch.setEvaluator(evaluator);
        localSearch.setSolutionCreator(solutionCreator);

        localSearch.setMutator(mutator);

        // iterative local search

        iterativeLocalSearchAlgorithm.setEvaluator(evaluator);
        iterativeLocalSearchAlgorithm.setSolutionCreator(solutionCreator);
        iterativeLocalSearchAlgorithm.setMutator(mutator);

        iterativeLocalSearchAlgorithm.setSearchAlgorithm(localSearch);
        iterativeLocalSearchAlgorithm.setMaximumGenerations(100);
    }


    @Test
    public void fisherThompsonFT06() {
        cachet.setTimes(ft06);
        fisherThompsonNxN(6, 6);
    }

    @Test
    public void fisherThompsonFT10() {
        cachet.setTimes(ft10);
        fisherThompsonNxN(10, 10);
    }

    @Test
    public void fisherThompsonFT20() {
        cachet.setTimes(ft20);
        fisherThompsonNxN(20, 5);
    }

    private void fisherThompsonNxN(int jobs, int machines) {

        solutionBuilder.setN(machines);

        Problem<Integer> problem = new Problem<>();
        for (int i = 0; i < jobs; i++) {
            problem.getProblemGenes().add(new ProblemGene<>(i));
        }

        System.out.println("Fisher Thompson " + jobs + " x " + machines);

        // genetic Algorithm
        geneticAlgorithm.setMutationProbability(0.01);
        geneticAlgorithm.setPopulationSize(100);
        Solution<List<Integer>, Integer> solution1 = geneticAlgorithm.solve(problem);

        // iterative local search
        localSearch.setMaximumGenerations(100);
        Solution<List<Integer>, Integer> solution2 = geneticAlgorithm.solve(problem);

        Assert.assertNotNull(solution1);
        Assert.assertTrue(solution1.getQuality() > 0);

        Assert.assertNotNull(solution2);
        Assert.assertTrue(solution2.getQuality() > 0);
    }
}

