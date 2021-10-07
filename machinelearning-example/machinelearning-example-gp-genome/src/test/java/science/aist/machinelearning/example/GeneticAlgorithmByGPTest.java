package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ga.crossover.OnePointCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.*;
import science.aist.machinelearning.algorithm.gp.nodes.math.ConstantDoubleNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.LessThanNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.RandomNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.SubtractNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.CacheTraderCollectionNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.CollectionMergeNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.ForCollectionNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.IfThenElseNode;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Imitates the behaviour of a genetic algorithm using genetic programming.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GeneticAlgorithmByGPTest {

    private final ResultNode root = new ResultNode();

    private Problem<Element[]> problem;

    private GenericEvaluatorImpl<Element[], Element[]> evaluator;

    @BeforeClass
    public void setup() {

        OneToOneSolutionCreator solutionCreator = new OneToOneSolutionCreator();
        solutionCreator.setGeneCreator(new RandomGeneCreator());

        evaluator = new GenericEvaluatorImpl<>();

        RandomGeneMutator mutator = new RandomGeneMutator();
        mutator.setEvaluator(evaluator);

        // configure evaluator
        ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(new RandomGeneCreator());

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

        problem = new Problem(problems);


        //create necessary nodes and set their values

        ConstantDoubleNode numberNode1 = new ConstantDoubleNode();
        numberNode1.setValue(0.0);

        ConstantDoubleNode numberNode2 = new ConstantDoubleNode();
        numberNode2.setValue(100.0);

        SolutionCreatorNode creatorNode = new SolutionCreatorNode();
        creatorNode.setSolutionCreator(solutionCreator);
        creatorNode.setProblem(problem);
        creatorNode.setEvaluator(evaluator);

        ForCollectionNode<Solution> forCollectionNode1 = new ForCollectionNode<>(Solution.class);
        forCollectionNode1.setCached(true);

        ConstantDoubleNode numberNode3 = new ConstantDoubleNode();
        numberNode3.setValue(10.0);

        EliteNode eliteNode = new EliteNode(Solution.class);

        CrossoverNode crossoverNode = new CrossoverNode();
        crossoverNode.setCrossover(new OnePointCrossover<>());
        crossoverNode.setSelector(new TournamentSelector<>());
        crossoverNode.setEvaluator(evaluator);

        MutatorNode mutatorNode = new MutatorNode();
        mutatorNode.setMutator(mutator);
        mutatorNode.setEvaluator(evaluator);

        ConstantDoubleNode numberNode4 = new ConstantDoubleNode();
        numberNode4.setValue(0.0);

        ConstantDoubleNode numberNode5 = new ConstantDoubleNode();
        numberNode5.setValue(1.0);

        RandomNode randomNode = new RandomNode();

        ConstantDoubleNode numberNode6 = new ConstantDoubleNode();
        numberNode6.setValue(0.25);

        LessThanNode lessThanNode1 = new LessThanNode();

        IfThenElseNode<Solution> ifNode1 = new IfThenElseNode<>(Solution.class);

        ConstantDoubleNode numberNode7 = new ConstantDoubleNode();
        numberNode7.setValue(100.0);

        SubtractNode subtractNode1 = new SubtractNode();

        ConstantDoubleNode numberNode8 = new ConstantDoubleNode();
        numberNode8.setValue(0.0);

        ForCollectionNode<Solution> forCollectionNode2 = new ForCollectionNode<>(Solution.class);

        CollectionMergeNode<Solution> mergeNode = new CollectionMergeNode<>(Solution.class);

        CacheTraderCollectionNode<Solution> traderNode = new CacheTraderCollectionNode<>(Solution.class);

        EvaluatorSolutionNode evaluatorNode1 = new EvaluatorSolutionNode();
        evaluatorNode1.setEvaluator(evaluator);

        ConstantDoubleNode numberNode9 = new ConstantDoubleNode();
        numberNode9.setValue(0.0);

        ConstantDoubleNode numberNode10 = new ConstantDoubleNode();
        numberNode10.setValue(100.0);

        ForCollectionNode<Solution> forCollectionNode3 = new ForCollectionNode<>(Solution.class);

        EvaluatorSolutionNode evaluatorNode2 = new EvaluatorSolutionNode();
        evaluatorNode2.setEvaluator(evaluator);

        //create connections between nodes

        forCollectionNode1.addChildNode(numberNode2);
        forCollectionNode1.addChildNode(creatorNode);

        eliteNode.addChildNode(numberNode3);
        eliteNode.addChildNode(forCollectionNode1);

        crossoverNode.addChildNode(forCollectionNode1);

        mutatorNode.addChildNode(crossoverNode);

        randomNode.addChildNode(numberNode4);
        randomNode.addChildNode(numberNode5);

        lessThanNode1.addChildNode(randomNode);
        lessThanNode1.addChildNode(numberNode6);

        ifNode1.addChildNode(lessThanNode1);
        ifNode1.addChildNode(mutatorNode);
        ifNode1.addChildNode(crossoverNode);

        subtractNode1.addChildNode(numberNode7);
        subtractNode1.addChildNode(numberNode3);

        forCollectionNode2.addChildNode(subtractNode1);
        forCollectionNode2.addChildNode(ifNode1);

        mergeNode.addChildNode(eliteNode);
        mergeNode.addChildNode(forCollectionNode2);

        traderNode.addChildNode(forCollectionNode1);
        traderNode.addChildNode(mergeNode);

        evaluatorNode1.addChildNode(traderNode);

        forCollectionNode3.addChildNode(numberNode10);
        forCollectionNode3.addChildNode(evaluatorNode1);

        evaluatorNode2.addChildNode(forCollectionNode3);

        root.addChildNode(evaluatorNode2);
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
    public void evaluateGraph() {
        //given
        //we have a root and it returns us a solution

        //when
        Solution<Element[], Element[]> s = root.execute();

        //then
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
