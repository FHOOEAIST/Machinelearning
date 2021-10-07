package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.*;
import science.aist.machinelearning.algorithm.gp.nodes.math.*;
import science.aist.machinelearning.algorithm.gp.nodes.programming.*;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.algorithm.gp.util.GPRepair;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.algorithm.mutation.GPReplacingNodeMutator;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.ListDescriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;
import science.aist.machinelearning.problem.GPProblem;
import science.aist.machinelearning.problem.fitness.evaluation.GPEvaluationTimerCachet;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;

import java.util.*;

/**
 * UnitTestClass for {@link GPReplacingNodeMutator}
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPReplacingNodeMutatorTest {
    private final ResultNode root = new ResultNode();
    private final Map<Class, ArrayList<GPGraphNode>> validNodes = new HashMap<>();
    private final Map<Class, Map<String, Descriptor>> settings = new HashMap<>();
    private Problem<Element[]> elementProblem;
    private GenericEvaluatorImpl<Element[], Element[]> evaluator;
    private GenericEvaluatorImpl<ResultNode, GPProblem> gpEvaluator;
    private GPProblem problem;

    @BeforeClass
    public void setup() {

        OneToOneSolutionCreator solutionCreator = new OneToOneSolutionCreator();
        solutionCreator.setGeneCreator(new RandomGeneCreator());

        evaluator = new GenericEvaluatorImpl<>();
        gpEvaluator = new GenericEvaluatorImpl<>();

        RandomGeneMutator mutator = new RandomGeneMutator();
        mutator.setEvaluator(evaluator);

        // configure evaluator
        ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        GPEvaluationTimerCachet evaluationCachet = new GPEvaluationTimerCachet();
        evaluationCachet.setRunsPerProblem(3);

        Map<CachetEvaluator<ResultNode, GPProblem>, Double> gpCachets = new HashMap<>();
        gpCachets.put(evaluationCachet, 1.0);
        gpEvaluator.setCachetEvaluators(gpCachets);

        //generate a elementProblem to work with
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

        elementProblem = new Problem(problems);

        Collection<Problem<Element[]>> problemList = new ArrayList<>();
        problemList.add(elementProblem);

        UniformCrossover crossover = new UniformCrossover();

        TournamentSelector<Element[], Element[]> selector = new TournamentSelector<>();
        selector.setTournamentSize(10);

        evaluationCachet.setProblems(problemList);
        evaluationCachet.setEvaluator(evaluator);

        //create necessary nodes and set their values

        SolutionCreatorNode creatorNode = new SolutionCreatorNode();
        creatorNode.setSolutionCreator(solutionCreator);
        creatorNode.setProblem(elementProblem);
        creatorNode.setEvaluator(evaluator);

        //create connections between nodes
        root.addChildNode(creatorNode);

        //setting up the gp-elementProblem
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
        solutionCreatorSettings.put("problem", new Descriptor<>(elementProblem));
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
        constantNumberSettings.put("value", new MinMaxDescriptor<>(0.0, 100.0));
        settings.put(ConstantDoubleNode.class, constantNumberSettings);

        returnsNumber.add(new DivideNode());

        returnsBoolean.add(new EqualsNode());

        returnsNumber.add(new ExponentiateNode());

        returnsBoolean.add(new LessThanNode());

        returnsNumber.add(new MultiplyNode());

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
        whileCollectionSettings.put("maxIterations", new MinMaxDescriptor<>(1, 10));
        settings.put(WhileCollectionNode.class, whileCollectionSettings);

        returnsObject.add(new WhileNode<>(Object.class));
        Map<String, Descriptor> whileSettings = new HashMap<>();
        whileSettings.put("maxIterations", new MinMaxDescriptor<>(1, 10));
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
    }

    @Test
    public void checkValidityOfGraph() {
        //given
        //we have a root and check if all its children are valid
        boolean valid;

        //when
        valid = GPValidator.validateGraph(root);

        //then
        //if everything runs through, then it works
        Assert.assertTrue(valid);
    }

    @Test(dependsOnMethods = "checkValidityOfGraph")
    public void mutateGraph() {
        //given
        //we mutate the graph and get a different solution for constants
        List<ProblemGene<GPProblem>> problemGenes = new ArrayList<>();
        problemGenes.add(new ProblemGene<>(problem));

        Solution<ResultNode, GPProblem> solution = new Solution<>();
        solution.addGene(new SolutionGene<>(root, problemGenes));

        GPReplacingNodeMutator gpValueMutator = new GPReplacingNodeMutator();
        gpValueMutator.setEvaluator(gpEvaluator);
        gpValueMutator.setRepair(new GPRepair(5, 1, 0.20, 0.30));

        gpEvaluator.evaluateQuality(solution);

        int prevNumberOfNodes = BasicNodeUtil.numberOfNodesInGraph(root);

        //its not guaranteed we end up with a better solution immediately, but over 100 runs we should find something
        for (int i = 0; i < 100; i++) {
            //when
            solution = gpValueMutator.mutate(solution);

            if (prevNumberOfNodes != BasicNodeUtil.numberOfNodesInGraph(solution.getSolutionGenes().get(0).getGene())) {
                break;
            }
        }

        //then
        Assert.assertNotEquals(prevNumberOfNodes, BasicNodeUtil.numberOfNodesInGraph(solution.getSolutionGenes().get(0).getGene()));
    }

}
