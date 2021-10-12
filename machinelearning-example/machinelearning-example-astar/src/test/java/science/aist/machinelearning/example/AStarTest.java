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
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.AStar;
import science.aist.machinelearning.algorithm.gene.ShortestPathProblemGene;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.example.mockup.EstimateWeightCalculator;
import science.aist.machinelearning.example.mockup.MockupGraph;
import science.aist.machinelearning.example.mockup.Node;
import science.aist.machinelearning.example.mockup.Weight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class AStarTest {


    @Test
    public void testGenericCallWithMockupGraph() {
        //given
        MockupGraph mockupGraph = new MockupGraph();
        List<Node> expected = mockupGraph.getExpectedPath();

        AStar<Node, Double> aStar = new AStar<>();
        ShortestPathProblemGene<Node, Double> gene = new ShortestPathProblemGene<>();
        gene.setGraph(mockupGraph.getGraphWithBacktracking());
        gene.setFrom(mockupGraph.getStart());
        gene.setTo((node -> node.equals(mockupGraph.getEnd())));
        ProblemGene<ShortestPathProblemGene<Node, Double>> problemGene = new ProblemGene<>();
        problemGene.setGene(gene);
        List<ProblemGene<ShortestPathProblemGene<Node, Double>>> problemGenes = new ArrayList<>();
        problemGenes.add(problemGene);
        Problem<ShortestPathProblemGene<Node, Double>> problem = new Problem<>();
        problem.setProblemGenes(problemGenes);


        //when
        List<Node> actual = aStar.solve(problem).getSolutionGenes().get(0).getGene();

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testCustomGraph() {
        //given
        MockupGraph mockupGraph = new MockupGraph();
        AStar<Node, Weight> aStar = new AStar<>();
        ShortestPathProblemGene<Node, Weight> gene = new ShortestPathProblemGene<>();
        gene.setGraph(mockupGraph.getCustomGraph());
        gene.setFrom(mockupGraph.getStart());
        gene.setTo((node -> node.equals(mockupGraph.getEnd())));
        ProblemGene<ShortestPathProblemGene<Node, Weight>> problemGene = new ProblemGene<>();
        problemGene.setGene(gene);
        List<ProblemGene<ShortestPathProblemGene<Node, Weight>>> problemGenes = new ArrayList<>();
        problemGenes.add(problemGene);
        Problem<ShortestPathProblemGene<Node, Weight>> problem = new Problem<>();
        problem.setProblemGenes(problemGenes);
        List<Node> expected = mockupGraph.getExpectedPath();

        //when
        List<Node> actual = aStar.solve(problem).getSolutionGenes().get(0).getGene();

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testComparator() {
        MockupGraph mockupGraph = new MockupGraph();
        AStar<Node, Weight> aStar = new AStar<>();
        ShortestPathProblemGene<Node, Weight> gene = new ShortestPathProblemGene<>();
        gene.setGraph(mockupGraph.getGraphWithLongAndShortPath());
        gene.setFrom(mockupGraph.getStart());
        gene.setTo((node -> node.equals(mockupGraph.getEnd())));
        ProblemGene<ShortestPathProblemGene<Node, Weight>> problemGene = new ProblemGene<>();
        problemGene.setGene(gene);
        List<ProblemGene<ShortestPathProblemGene<Node, Weight>>> problemGenes = new ArrayList<>();
        problemGenes.add(problemGene);
        Problem<ShortestPathProblemGene<Node, Weight>> problem = new Problem<>();
        problem.setProblemGenes(problemGenes);
        List<Node> expected = mockupGraph.getExpectedPath();

        Comparator<Number> comparator = Comparator.comparingDouble(Number::doubleValue);

        Map<String, Descriptor> options = aStar.getOptions();
        options.put("comparator", new Descriptor<>(comparator));
        aStar.setOptions(options);

        //when
        List<Node> actual = aStar.solve(problem).getSolutionGenes().get(0).getGene();

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testSwappedComparator() {
        MockupGraph mockupGraph = new MockupGraph();
        AStar<Node, Weight> aStar = new AStar<>();
        ShortestPathProblemGene<Node, Weight> gene = new ShortestPathProblemGene<>();
        gene.setGraph(mockupGraph.getGraphWithLongAndShortPath());
        gene.setFrom(mockupGraph.getStart());
        gene.setTo((node -> node.equals(mockupGraph.getEnd())));
        ProblemGene<ShortestPathProblemGene<Node, Weight>> problemGene = new ProblemGene<>();
        problemGene.setGene(gene);
        List<ProblemGene<ShortestPathProblemGene<Node, Weight>>> problemGenes = new ArrayList<>();
        problemGenes.add(problemGene);
        Problem<ShortestPathProblemGene<Node, Weight>> problem = new Problem<>();
        problem.setProblemGenes(problemGenes);
        List<Node> expected = mockupGraph.getExpectedPath2();

        Comparator<Number> comparator = (o1, o2) -> Double.compare(o2.doubleValue(), o1.doubleValue());

        Map<String, Descriptor> options = aStar.getOptions();
        options.put("comparator", new Descriptor<>(comparator));
        aStar.setOptions(options);

        //when
        List<Node> actual = aStar.solve(problem).getSolutionGenes().get(0).getGene();

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testPerdicateEnd() {
        MockupGraph mockupGraph = new MockupGraph();
        AStar<Node, Weight> aStar = new AStar<>();
        ShortestPathProblemGene<Node, Weight> gene = new ShortestPathProblemGene<>();
        gene.setGraph(mockupGraph.getGraphWithMultipleSpecials());
        gene.setFrom(mockupGraph.getStart());
        gene.setTo((Node::isSpecial));
        ProblemGene<ShortestPathProblemGene<Node, Weight>> problemGene = new ProblemGene<>();
        problemGene.setGene(gene);
        List<ProblemGene<ShortestPathProblemGene<Node, Weight>>> problemGenes = new ArrayList<>();
        problemGenes.add(problemGene);
        Problem<ShortestPathProblemGene<Node, Weight>> problem = new Problem<>();
        problem.setProblemGenes(problemGenes);
        List<Node> expected = mockupGraph.getExpectedPath2();

        Comparator<Number> comparator = (o1, o2) -> Double.compare(o2.doubleValue(), o1.doubleValue());

        Map<String, Descriptor> options = aStar.getOptions();
        options.put("comparator", new Descriptor<>(comparator));
        aStar.setOptions(options);

        //when
        List<Node> actual = aStar.solve(problem).getSolutionGenes().get(0).getGene();

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }


    @Test
    public void testCustomWeightCalculator() {
        MockupGraph mockupGraph = new MockupGraph();
        AStar<Node, Double> aStar = new AStar<>();
        ShortestPathProblemGene<Node, Double> gene = new ShortestPathProblemGene<>();
        gene.setGraph(mockupGraph.getGraphWithBeeLine());
        gene.setFrom(mockupGraph.getStart());
        gene.setTo(node -> node.equals(mockupGraph.getEnd()));
        ProblemGene<ShortestPathProblemGene<Node, Double>> problemGene = new ProblemGene<>();
        problemGene.setGene(gene);
        List<ProblemGene<ShortestPathProblemGene<Node, Double>>> problemGenes = new ArrayList<>();
        problemGenes.add(problemGene);
        Problem<ShortestPathProblemGene<Node, Double>> problem = new Problem<>();
        problem.setProblemGenes(problemGenes);
        List<Node> expected = mockupGraph.getExpectedPath();

        Map<String, Descriptor> options = aStar.getOptions();
        options.put("weightCalculator", new Descriptor<>(new EstimateWeightCalculator()));
        aStar.setOptions(options);

        //when
        List<Node> actual = aStar.solve(problem).getSolutionGenes().get(0).getGene();

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }
}
