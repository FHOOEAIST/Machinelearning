/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.EvaluatorSolutionNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.ConstantDoubleNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.ForCollectionNode;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.algorithm.mutation.GPValueMutator;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.problem.GPProblem;
import science.aist.machinelearning.problem.fitness.evaluation.GPEvaluationTimerCachet;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;

import java.util.*;

/**
 * UnitTestClass for {@link GPValueMutator}
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPValueMutatorTest {

    private final ResultNode root = new ResultNode();

    private Problem<Element[]> problem;

    private GenericEvaluatorImpl<Element[], Element[]> evaluator;

    private GenericEvaluatorImpl<ResultNode, GPProblem> gpEvaluator;

    private ConstantDoubleNode numberNode2;

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

        Map<CachetEvaluator<ResultNode, GPProblem>, Double> gpCachets = new HashMap<>();
        gpCachets.put(evaluationCachet, 1.0);
        gpEvaluator.setCachetEvaluators(gpCachets);

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

        Collection<Problem<Element[]>> problemList = new ArrayList<>();
        problemList.add(problem);

        evaluationCachet.setProblems(problemList);
        evaluationCachet.setEvaluator(evaluator);

        //create necessary nodes and set their values

        numberNode2 = new ConstantDoubleNode();
        numberNode2.setValue(100.0);

        SolutionCreatorNode creatorNode = new SolutionCreatorNode();
        creatorNode.setSolutionCreator(solutionCreator);
        creatorNode.setProblem(problem);
        creatorNode.setEvaluator(evaluator);

        ForCollectionNode<Solution> forCollectionNode1 = new ForCollectionNode<>(Solution.class);
        forCollectionNode1.setCached(true);

        EvaluatorSolutionNode evaluatorNode1 = new EvaluatorSolutionNode();
        evaluatorNode1.setEvaluator(evaluator);

        //create connections between nodes

        forCollectionNode1.addChildNode(numberNode2);
        forCollectionNode1.addChildNode(creatorNode);

        evaluatorNode1.addChildNode(forCollectionNode1);

        root.addChildNode(evaluatorNode1);
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
        Solution<ResultNode, GPProblem> solution = new Solution<>();
        solution.addGene(new SolutionGene<>(root));
        GPValueMutator gpValueMutator = new GPValueMutator();
        gpValueMutator.setEvaluator(gpEvaluator);

        gpEvaluator.evaluateQuality(solution);

        Double prevValue2 = numberNode2.getValue();

        Double newValue2 = 0.0;

        //its not guaranteed we end up with a better solution immediately, but over 100 runs we should find something
        for (int i = 0; i < 100; i++) {

            //when
            solution = gpValueMutator.mutate(solution);

            newValue2 = ((ConstantDoubleNode) ((FunctionalGPGraphNode) ((FunctionalGPGraphNode) solution.getSolutionGenes().get(0).getGene().getChildNodes().get(0)).getChildNodes().get(0)).getChildNodes().get(0)).getValue();

            if (newValue2 != prevValue2) {
                break;
            }
        }

        //then
        Assert.assertNotSame(newValue2, prevValue2);
        Assert.assertTrue(GPValidator.validateGraph(solution.getSolutionGenes().get(0).getGene()));
    }
}
