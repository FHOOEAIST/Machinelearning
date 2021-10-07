package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.MutatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.ConstantDoubleNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.CacheTraderNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.ForNode;
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
 * Imitates the behaviour of local search using genetic programming
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class LocalSearchByGPTest {

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
        SolutionCreatorNode creatorNode = new SolutionCreatorNode();
        creatorNode.setSolutionCreator(solutionCreator);
        creatorNode.setProblem(problem);
        creatorNode.setEvaluator(evaluator);
        creatorNode.setCached(true);

        MutatorNode mutatorNode = new MutatorNode();
        mutatorNode.setMutator(mutator);
        mutatorNode.setEvaluator(evaluator);

        CacheTraderNode traderNode = new CacheTraderNode(Solution.class);

        ConstantDoubleNode numberNode1 = new ConstantDoubleNode();
        numberNode1.setValue(0.0);

        ConstantDoubleNode numberNode2 = new ConstantDoubleNode();
        numberNode2.setValue(100.0);

        ForNode<Solution> forCollectionNode1 = new ForNode<>(Solution.class);

        //create connections between nodes

        mutatorNode.addChildNode(creatorNode);

        traderNode.addChildNode(mutatorNode);
        traderNode.addChildNode(creatorNode);

        forCollectionNode1.addChildNode(numberNode2);
        forCollectionNode1.addChildNode(traderNode);

        root.addChildNode(forCollectionNode1);

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
