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
import science.aist.machinelearning.algorithm.mapping.AStarImpl;
import science.aist.machinelearning.example.mockup.EstimateWeightCalculator;
import science.aist.machinelearning.example.mockup.MockupGraph;
import science.aist.machinelearning.example.mockup.Node;
import science.aist.machinelearning.example.mockup.SimpleWeightCalculator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class AStarImplTest {


    @Test
    public void testAStarBackTracking() {
        //given
        MockupGraph mock = new MockupGraph();
        Map<Node, Map<Node, Double>> graph = mock.getGraphWithBacktracking();
        Node start = mock.getStart();
        Node end = mock.getEnd();
        AStarImpl<Node, Double> aStar = new AStarImpl<>();
        List<Node> expected = mock.getExpectedPath();

        //when
        List<Node> actual = aStar.findShortestPath(
                graph,
                start,
                (node -> node.equals(end)),
                Comparator.comparingDouble(Number::doubleValue),
                new SimpleWeightCalculator()
        );

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testAStarWithTwoEqualPaths() {
        //given
        MockupGraph mock = new MockupGraph();
        Map<Node, Map<Node, Double>> graph = mock.getGraphWithTwoEqualPaths();
        Node start = mock.getStart();
        Node end = mock.getEnd();
        AStarImpl<Node, Double> aStar = new AStarImpl<>();
        List<Node> expected = mock.getExpectedPath();
        List<Node> expected2 = mock.getExpectedPath2();

        //when
        List<Node> actual = aStar.findShortestPath(graph,
                start,
                (node -> node.equals(end)),
                Comparator.comparingDouble(Number::doubleValue),
                new SimpleWeightCalculator()
        );

        //then
        Assert.assertEquals(actual.size(), expected.size());
        Assert.assertEquals(actual.size(), expected2.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertTrue(expected.get(i).equals(actual.get(i))
                    || expected2.get(i).equals(actual.get(i)));
        }
    }

    @Test
    public void testAStarWithGap() {
        //given
        MockupGraph mock = new MockupGraph();
        Map<Node, Map<Node, Double>> graph = mock.getGraphWithGap();
        Node start = mock.getStart();
        Node end = mock.getEnd();
        AStarImpl<Node, Double> aStar = new AStarImpl<>();
        List<Node> expected = mock.getExpectedPath();

        //when
        List<Node> actual = aStar.findShortestPath(graph,
                start,
                (node -> node.equals(end)),
                Comparator.comparingDouble(Number::doubleValue),
                new SimpleWeightCalculator()
        );

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }

    }

    @Test
    public void testAStarImpossiblePath() {
        //given
        MockupGraph mock = new MockupGraph();
        Map<Node, Map<Node, Double>> graph = mock.getGraphWithImpossiblePath();
        Node start = mock.getStart();
        Node end = mock.getEnd();
        AStarImpl<Node, Double> aStar = new AStarImpl<>();
        List<Node> expected = mock.getExpectedPath();

        //when
        List<Node> actual = aStar.findShortestPath(graph,
                start,
                (node -> node.equals(end)),
                Comparator.comparingDouble(Number::doubleValue),
                new SimpleWeightCalculator()
        );

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testCustomWeightCalculator() {
        //given
        MockupGraph mock = new MockupGraph();
        Map<Node, Map<Node, Double>> graph = mock.getGraphWithBeeLine();
        Node start = mock.getStart();
        Node end = mock.getEnd();
        AStarImpl<Node, Double> aStar = new AStarImpl<>();
        List<Node> expected = mock.getExpectedPath();

        //when
        List<Node> actual = aStar.findShortestPath(graph,
                start,
                (node -> node.equals(end)),
                Comparator.comparingDouble(Number::doubleValue),
                new EstimateWeightCalculator()
        );

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }


    @Test
    public void testWithoutCustomWeightCalculator() {
        //given
        MockupGraph mock = new MockupGraph();
        Map<Node, Map<Node, Double>> graph = mock.getGraphWithBeeLine();
        Node start = mock.getStart();
        Node end = mock.getEnd();
        AStarImpl<Node, Double> aStar = new AStarImpl<>();
        List<Node> expected = mock.getExpectedPath2();

        //when
        List<Node> actual = aStar.findShortestPath(graph,
                start,
                (node -> node.equals(end)),
                Comparator.comparingDouble(Number::doubleValue),
                new SimpleWeightCalculator()
        );

        //then
        Assert.assertEquals(actual.size(), expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

}
