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
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.MutatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.ConstantDoubleNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.CacheTraderNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.ForNode;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.algorithm.mutation.ComplementaryGeneMutator;
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
 * Imitates the behaviour of an iterative local search using genetic programming
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class IterativeLocalSearchByGPTest {

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

        ComplementaryGeneMutator mutator2 = new ComplementaryGeneMutator();
        mutator2.setEvaluator(evaluator);

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

        ForNode forNode1 = new ForNode(Solution.class);

        MutatorNode mutatorNode2 = new MutatorNode();
        mutatorNode2.setMutator(mutator2);
        mutatorNode2.setEvaluator(evaluator);

        CacheTraderNode traderNode2 = new CacheTraderNode(Solution.class);

        ConstantDoubleNode numberNode3 = new ConstantDoubleNode();
        numberNode3.setValue(0.0);

        ConstantDoubleNode numberNode4 = new ConstantDoubleNode();
        numberNode4.setValue(100.0);

        ForNode forNode2 = new ForNode(Solution.class);

        //create connections between nodes

        mutatorNode.addChildNode(creatorNode);

        traderNode.addChildNode(mutatorNode);
        traderNode.addChildNode(creatorNode);

        forNode1.addChildNode(numberNode2);
        forNode1.addChildNode(traderNode);

        mutatorNode2.addChildNode(forNode1);

        traderNode2.addChildNode(mutatorNode2);
        traderNode2.addChildNode(creatorNode);

        forNode2.addChildNode(numberNode4);
        forNode2.addChildNode(traderNode2);

        root.addChildNode(forNode2);
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
