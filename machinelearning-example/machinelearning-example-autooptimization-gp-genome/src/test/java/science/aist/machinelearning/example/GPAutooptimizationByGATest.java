package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.crossover.GPCrossover;
import science.aist.machinelearning.algorithm.ga.GeneticAlgorithm;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.*;
import science.aist.machinelearning.algorithm.gp.nodes.math.*;
import science.aist.machinelearning.algorithm.gp.nodes.programming.*;
import science.aist.machinelearning.algorithm.gp.util.GPRepair;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.algorithm.mutation.GPReplacingNodeMutator;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.ListDescriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;
import science.aist.machinelearning.problem.GPProblem;
import science.aist.machinelearning.problem.fitness.GPDepthCachet;
import science.aist.machinelearning.problem.fitness.GPSolutionCachet;
import science.aist.machinelearning.problem.fitness.evaluation.GPEvaluationAbortingTimerCachet;
import science.aist.machinelearning.problem.fitness.evaluation.GPEvaluationTimerCachet;
import science.aist.machinelearning.problem.fitness.runtime.GPRuntimeFromEvaluationCachet;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;
import science.aist.machinelearning.problem.mapping.GPGeneCreator;

import java.util.*;

/**
 * Create an optimized heuristic with the help of a GA.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPAutooptimizationByGATest {

    private final GeneticAlgorithm<ResultNode, GPProblem> geneticAlgorithm = new GeneticAlgorithm<>();

    private GPProblem problem;

    private GenericEvaluatorImpl<Element[], Element[]> evaluator;

    private Problem<Element[]> genomeProblem;

    private GPRepair gpRepair;

    private RandomGeneCreator randomGeneCreator;

    @BeforeClass
    public void setup() {

        Map<Class, ArrayList<GPGraphNode>> validNodes = new HashMap<>();

        Map<Class, Map<String, Descriptor>> settings = new HashMap<>();

        //setup evaluators and other classes for the nodes

        OneToOneSolutionCreator solutionCreator = new OneToOneSolutionCreator();
        randomGeneCreator = new RandomGeneCreator();
        solutionCreator.setGeneCreator(randomGeneCreator);

        evaluator = new GenericEvaluatorImpl<>();

        RandomGeneMutator mutator = new RandomGeneMutator();
        mutator.setEvaluator(evaluator);

        // configure evaluator
        ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        //generate a problem to work with
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

        genomeProblem = new Problem(problems);

        UniformCrossover crossover = new UniformCrossover();

        TournamentSelector<Element[], Element[]> selector = new TournamentSelector<>();
        selector.setTournamentSize(10);


        ArrayList<GPGraphNode> returnsSolution = new ArrayList<>();
        ArrayList<GPGraphNode> returnsNumber = new ArrayList<>();
        ArrayList<GPGraphNode> returnsBoolean = new ArrayList<>();
        ArrayList<GPGraphNode> returnsCollection = new ArrayList<>();
        ArrayList<GPGraphNode> returnsObject = new ArrayList<>();

        //setting up heuristic nodes
        returnsSolution.add(new CrossoverNode());
        Map<String, Descriptor> crossoverSettings = new HashMap<>();
        crossoverSettings.put("crossover", new Descriptor<>(crossover));
        crossoverSettings.put("selector", new Descriptor<>(selector));
        crossoverSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(CrossoverNode.class, crossoverSettings);

        returnsCollection.add(new EliteNode(Solution.class));

        returnsNumber.add(new EvaluatorQualityNode());
        Map<String, Descriptor> evaluatorQualitySettings = new HashMap<>();
        evaluatorQualitySettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(EvaluatorQualityNode.class, evaluatorQualitySettings);

        returnsSolution.add(new EvaluatorSolutionNode());
        Map<String, Descriptor> evaluatorSolutionSettings = new HashMap<>();
        evaluatorSolutionSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(EvaluatorSolutionNode.class, evaluatorSolutionSettings);

        returnsSolution.add(new MutatorNode());
        Map<String, Descriptor> mutatorSettings = new HashMap<>();
        mutatorSettings.put("mutator", new Descriptor<>(mutator));
        mutatorSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(MutatorNode.class, mutatorSettings);

        returnsSolution.add(new SolutionCreatorNode());
        Map<String, Descriptor> solutionCreatorSettings = new HashMap<>();
        solutionCreatorSettings.put("creator", new Descriptor<>(solutionCreator));
        solutionCreatorSettings.put("problem", new Descriptor<>(genomeProblem));
        solutionCreatorSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(SolutionCreatorNode.class, solutionCreatorSettings);

        //setting up math nodes
        returnsNumber.add(new AddNode());

        returnsBoolean.add(new AndNode());

        returnsBoolean.add(new ConstantBooleanNode());
        Map<String, Descriptor> constantBooleanSettings = new HashMap<>();
        ArrayList<Boolean> possibleValues = new ArrayList<>();
        possibleValues.add(true);
        possibleValues.add(false);
        constantBooleanSettings.put("value", new ListDescriptor<>(possibleValues));
        settings.put(ConstantBooleanNode.class, constantBooleanSettings);

        returnsNumber.add(new ConstantDoubleNode());
        Map<String, Descriptor> constantNumberSettings = new HashMap<>();
        constantNumberSettings.put("value", new MinMaxDescriptor<>(0.0, 1000.0));
        settings.put(ConstantDoubleNode.class, constantNumberSettings);

        returnsNumber.add(new DivideNode());

        returnsBoolean.add(new EqualsNode());

        //returnsNumber.add(new ExponentiateNode());

        returnsBoolean.add(new LessThanNode());

        //returnsNumber.add(new MultiplyNode());

        returnsBoolean.add(new OrNode());

        returnsNumber.add(new RandomNode());

        returnsNumber.add(new SquareRootNode());

        returnsNumber.add(new SubtractNode());

        //setting up programming nodes

        returnsCollection.add(new CacheTraderCollectionNode<>(Object.class));

        returnsObject.add(new CacheTraderNode<>(Object.class));

        returnsCollection.add(new CollectionMergeNode<>(Object.class));

        returnsCollection.add(new ForCollectionNode<>(Solution.class));

        returnsObject.add(new ForNode<>(Object.class));

        returnsObject.add(new IfThenElseNode<>(Object.class));

        returnsNumber.add(new SizeOfCollectionNode());

        returnsCollection.add(new WhileCollectionNode<>(Object.class));
        Map<String, Descriptor> whileCollectionSettings = new HashMap<>();
        whileCollectionSettings.put("maxIterations", new MinMaxDescriptor<>(1, 100));
        settings.put(WhileCollectionNode.class, whileCollectionSettings);

        returnsObject.add(new WhileNode<>(Object.class));
        Map<String, Descriptor> whileSettings = new HashMap<>();
        whileSettings.put("maxIterations", new MinMaxDescriptor<>(1, 100));
        settings.put(WhileCollectionNode.class, whileSettings);


        validNodes.put(Object.class, returnsObject);
        validNodes.put(Number.class, returnsNumber);
        validNodes.put(Boolean.class, returnsBoolean);
        validNodes.put(Solution.class, returnsSolution);
        validNodes.put(Collection.class, returnsCollection);


        //additional Terminals
        ArrayList<GPGraphNode> returnsCollectionTerminal = new ArrayList<>();
        returnsCollectionTerminal.add(new ForCollectionNode<>(Solution.class));
        Map<Class, ArrayList<GPGraphNode>> additionalTerminals = new HashMap<>();
        additionalTerminals.put(Collection.class, returnsCollectionTerminal);

        problem = new GPProblem(validNodes, settings, additionalTerminals);

        //setup the GA for creating a good GP that can solve the genome problem
        geneticAlgorithm.setMaximumGenerations(10);
        geneticAlgorithm.setPopulationSize(10);
        geneticAlgorithm.setElites(5);

        //GA evaluator

        GenericEvaluatorImpl<ResultNode, GPProblem> gaEvaluator = new GenericEvaluatorImpl<>();
        Map<CachetEvaluator<ResultNode, GPProblem>, Double> gaCachets = new HashMap<>();

        ArrayList<Problem<Element[]>> problemToSolve = new ArrayList<>();
        problemToSolve.add(genomeProblem);

        GPEvaluationTimerCachet gpEvaluationTimerCachet = new GPEvaluationTimerCachet();
        gpEvaluationTimerCachet.setEvaluator(evaluator);
        gpEvaluationTimerCachet.setProblems(problemToSolve);
        gpEvaluationTimerCachet.setRunsPerProblem(5);
        gpEvaluationTimerCachet.setEvaluationTime(1000);

        gaCachets.put(new GPDepthCachet<>(), 0.1);
        gaCachets.put(gpEvaluationTimerCachet, 1.0);
        gaCachets.put(new GPSolutionCachet<>(), 0.001);
        gaCachets.put(new GPRuntimeFromEvaluationCachet<>(gpEvaluationTimerCachet), 0.001);

        gaEvaluator.setCachetEvaluators(gaCachets);

        geneticAlgorithm.setEvaluator(gaEvaluator);

        //GA crossover

        GPCrossover gpCrossover = new GPCrossover();
        geneticAlgorithm.setCrossover(gpCrossover);

        //GA solution creator

        gpRepair = new GPRepair(10, 3, 0.2, 0.2);

        GPGeneCreator gpGeneCreator = new GPGeneCreator();
        gpGeneCreator.setRepair(gpRepair);

        SolutionCreator<ResultNode, GPProblem> gpSolutionCreator = new OneToOneSolutionCreator<>();
        gpSolutionCreator.setGeneCreator(gpGeneCreator);

        geneticAlgorithm.setSolutionCreator(gpSolutionCreator);

        //GA mutator

        GPReplacingNodeMutator gpMutator = new GPReplacingNodeMutator();
        gpMutator.setRepair(gpRepair);

        gpMutator.setMutationsPerSolution(1);
        gpMutator.setEvaluator(gaEvaluator);

        geneticAlgorithm.setGenMutator(gpMutator);

        //GA selector
        geneticAlgorithm.setSelector(new TournamentSelector<>());
    }

    @Test
    public void createGPHeuristicViaGA() {
        //given
        ArrayList<ProblemGene<GPProblem>> problemGenes = new ArrayList<>();
        problemGenes.add(new ProblemGene<>(problem));

        Problem<GPProblem> problemToSolve = new Problem<>(problemGenes);

        //when
        Solution<ResultNode, GPProblem> solution = geneticAlgorithm.solve(problemToSolve);

        //then
        Assert.assertNotNull(solution);
        Assert.assertTrue(GPValidator.validateGraph(solution.getSolutionGenes().get(0).getGene()));
    }

    @Test(enabled = false)
    public void testRuntimeOfEvaluators() {

        Random r = new Random();

        long gpRepairSeed = r.nextInt(Integer.MAX_VALUE);
        long randomGeneCreatorSeed = r.nextInt(Integer.MAX_VALUE);

        ArrayList<ProblemGene<GPProblem>> problemGenes = new ArrayList<>();
        problemGenes.add(new ProblemGene<>(problem));

        Problem<GPProblem> problemToSolveGP = new Problem<>(problemGenes);

        //Test 1 - GPEvaluationTimerCachet
        GenericEvaluatorImpl<ResultNode, GPProblem> gaEvaluator = new GenericEvaluatorImpl<>();
        Map<CachetEvaluator<ResultNode, GPProblem>, Double> gaCachets = new HashMap<>();

        ArrayList<Problem<Element[]>> problemToSolve = new ArrayList<>();
        problemToSolve.add(genomeProblem);

        GPEvaluationTimerCachet gpEvaluationTimerCachet = new GPEvaluationTimerCachet();
        gpEvaluationTimerCachet.setEvaluator(evaluator);
        gpEvaluationTimerCachet.setProblems(problemToSolve);
        gpEvaluationTimerCachet.setRunsPerProblem(2);
        gpEvaluationTimerCachet.setEvaluationTime(1000);

        gaCachets.put(new GPDepthCachet<>(), 1.0);
        gaCachets.put(gpEvaluationTimerCachet, 10.0);

        gaEvaluator.setCachetEvaluators(gaCachets);

        //Test 2 - GPEEvaluationLimitCreatorCachet
        GenericEvaluatorImpl<ResultNode, GPProblem> gaEvaluator2 = new GenericEvaluatorImpl<>();
        Map<CachetEvaluator<ResultNode, GPProblem>, Double> gaCachets2 = new HashMap<>();

        GPEvaluationAbortingTimerCachet gpEvaluationAbortingTimerCachet = new GPEvaluationAbortingTimerCachet();
        gpEvaluationAbortingTimerCachet.setEvaluator(evaluator);
        gpEvaluationAbortingTimerCachet.setProblems(problemToSolve);
        gpEvaluationAbortingTimerCachet.setRunsPerProblem(2);
        gpEvaluationAbortingTimerCachet.setEvaluationTime(1000);

        gaCachets2.put(new GPDepthCachet<>(), 1.0);
        gaCachets2.put(gpEvaluationAbortingTimerCachet, 10.0);

        gaEvaluator2.setCachetEvaluators(gaCachets2);


        gpRepair.getR().setSeed(gpRepairSeed);
        randomGeneCreator.getR().setSeed(randomGeneCreatorSeed);

        geneticAlgorithm.setEvaluator(gaEvaluator);

        long runtime = System.currentTimeMillis();
        Solution<ResultNode, GPProblem> solution = geneticAlgorithm.solve(problemToSolveGP);
        System.out.println("Time for GPEvaluationTimerCachet: " + (System.currentTimeMillis() - runtime));


        gpRepair.getR().setSeed(gpRepairSeed);
        randomGeneCreator.getR().setSeed(randomGeneCreatorSeed);

        geneticAlgorithm.setEvaluator(gaEvaluator2);

        runtime = System.currentTimeMillis();
        Solution<ResultNode, GPProblem> solution2 = geneticAlgorithm.solve(problemToSolveGP);
        System.out.println("Time for GPEvaluationAbortingTimerCachet: " + (System.currentTimeMillis() - runtime));
    }
}
