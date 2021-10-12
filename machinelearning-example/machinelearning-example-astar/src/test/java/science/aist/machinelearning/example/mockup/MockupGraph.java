/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class MockupGraph {

    /**
     * expected path
     */
    private final List<Node> expectedPath = new ArrayList<>();
    /**
     * second expected path, if multiple paths are possible or different outcomes are expected
     */
    private final List<Node> expectedPath2 = new ArrayList<>();
    /**
     * Start node
     */
    private Node start;
    /**
     * end node
     */
    private Node end;

    public Node getStart() {
        return start;
    }

    private void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return end;
    }

    private void setEnd(Node end) {
        this.end = end;
    }

    public List<Node> getExpectedPath() {
        return expectedPath;
    }

    public List<Node> getExpectedPath2() {
        return expectedPath2;
    }

    /**
     * Builds the following Graph A-B-C . | | D-E-F . | | G-H-I
     * <p>
     * Weights: . A B C D E F G H I A 0 1 B 1   1   5 C   1       1 D         5 E   5   5   1   5 F     1   1       1 G
     * 1 H         5   1   1 I           1   1
     * <p>
     * Start A End G Expected Path {A,B,E,H,G} (best with bee line) Expected Path 2 {A,B,C,F,I,H,G} (best with shortest
     * path) uses estimate with bee line instead of shortest path
     *
     * @return graph that uses bee line for path finding
     */
    public Map<Node, Map<Node, Double>> getGraphWithBeeLine() {
        Map<Node, Map<Node, Double>> graph = new HashMap<>();

        Node A = new Node("A", 2);
        Node B = new Node("B", 3);
        Node C = new Node("C", 4);
        Node D = new Node("D", 1);
        Node E = new Node("E", 2);
        Node F = new Node("F", 3);
        Node G = new Node("G", 0);
        Node H = new Node("H", 1);
        Node I = new Node("I", 2);

        setStart(A);
        setEnd(G);

        getExpectedPath().clear();
        getExpectedPath().add(0, A);
        getExpectedPath().add(1, B);
        getExpectedPath().add(2, E);
        getExpectedPath().add(3, H);
        getExpectedPath().add(4, G);

        getExpectedPath2().clear();
        getExpectedPath2().add(0, A);
        getExpectedPath2().add(1, B);
        getExpectedPath2().add(2, C);
        getExpectedPath2().add(3, F);
        getExpectedPath2().add(4, I);
        getExpectedPath2().add(5, H);
        getExpectedPath2().add(6, G);


        Map<Node, Double> fromA = new HashMap<>();
        Map<Node, Double> fromB = new HashMap<>();
        Map<Node, Double> fromC = new HashMap<>();
        Map<Node, Double> fromD = new HashMap<>();
        Map<Node, Double> fromE = new HashMap<>();
        Map<Node, Double> fromF = new HashMap<>();
        Map<Node, Double> fromG = new HashMap<>();
        Map<Node, Double> fromH = new HashMap<>();
        Map<Node, Double> fromI = new HashMap<>();

        fromA.put(B, 1d);

        fromB.put(A, 5d);
        fromB.put(C, 1d);
        fromB.put(E, 5d);

        fromC.put(B, 1d);
        fromC.put(F, 1d);

        fromD.put(E, 5d);

        fromE.put(B, 5d);
        fromE.put(D, 5d);
        fromE.put(F, 1d);
        fromE.put(H, 5d);

        fromF.put(C, 1d);
        fromF.put(E, 1d);
        fromF.put(I, 1d);

        fromG.put(H, 1d);

        fromH.put(E, 5d);
        fromH.put(G, 1d);
        fromH.put(I, 1d);

        fromI.put(F, 1d);
        fromI.put(H, 1d);


        graph.put(A, fromA);
        graph.put(B, fromB);
        graph.put(C, fromC);
        graph.put(D, fromD);
        graph.put(E, fromE);
        graph.put(F, fromF);
        graph.put(G, fromG);
        graph.put(H, fromH);
        graph.put(I, fromI);


        return graph;
    }

    /**
     * Builds the following Graph
     * <p>
     * A | \ B  C | / D
     * <p>
     * Start A End D Shortest Path {A,B,D}, {A,C,D}
     *
     * @return graph with two equally good paths
     */
    public Map<Node, Map<Node, Double>> getGraphWithTwoEqualPaths() {
        Map<Node, Map<Node, Double>> graph = new HashMap<>();


        Node A = new Node("A");
        Node B = new Node("B");
        Node C = new Node("C");
        Node D = new Node("D");


        setStart(A);
        setEnd(D);

        getExpectedPath().clear();
        getExpectedPath().add(0, A);
        getExpectedPath().add(1, B);
        getExpectedPath().add(2, D);

        getExpectedPath2().clear();
        getExpectedPath2().add(0, A);
        getExpectedPath2().add(1, C);
        getExpectedPath2().add(2, D);

        Map<Node, Double> fromA = new HashMap<>();
        Map<Node, Double> fromB = new HashMap<>();
        Map<Node, Double> fromC = new HashMap<>();
        Map<Node, Double> fromD = new HashMap<>();

        fromA.put(B, 1d);
        fromA.put(C, 1d);

        fromB.put(A, 1d);
        fromB.put(D, 1d);

        fromC.put(A, 1d);
        fromC.put(D, 1d);

        fromD.put(B, 1d);
        fromD.put(C, 1d);

        graph.put(A, fromA);
        graph.put(B, fromB);
        graph.put(C, fromC);
        graph.put(D, fromD);

        return graph;
    }

    /**
     * Builds the following Graph A B
     * <p>
     * Start A End B Shortest Path {}
     *
     * @return graph that has no possible shortest path from A to B
     */
    public Map<Node, Map<Node, Double>> getGraphWithImpossiblePath() {
        Map<Node, Map<Node, Double>> graph = new HashMap<>();

        Node A = new Node("A");
        Node B = new Node("B");

        setStart(A);
        setEnd(B);

        getExpectedPath().clear();

        Map<Node, Double> fromA = new HashMap<>();
        Map<Node, Double> fromB = new HashMap<>();

        graph.put(A, fromA);
        graph.put(B, fromB);

        return graph;
    }

    /**
     * Builds the following Graph A--B---E |      | C      D
     * <p>
     * Start A End E Shortest Path {A,B,E}
     *
     * @return graph that contains a gap between C and D
     */
    public Map<Node, Map<Node, Double>> getGraphWithGap() {
        Map<Node, Map<Node, Double>> graph = new HashMap<>();

        Node A = new Node("A");
        Node B = new Node("B");
        Node C = new Node("C");
        Node D = new Node("D");
        Node E = new Node("E");


        setStart(A);
        setEnd(E);

        getExpectedPath().clear();
        getExpectedPath().add(0, A);
        getExpectedPath().add(1, B);
        getExpectedPath().add(2, E);

        Map<Node, Double> fromA = new HashMap<>();
        Map<Node, Double> fromB = new HashMap<>();
        Map<Node, Double> fromC = new HashMap<>();
        Map<Node, Double> fromD = new HashMap<>();
        Map<Node, Double> fromE = new HashMap<>();

        fromA.put(B, 2d);
        fromA.put(C, 1d);

        fromB.put(A, 2d);
        fromB.put(E, 3d);

        fromC.put(A, 1d);

        fromD.put(E, 1d);

        fromE.put(B, 3d);
        fromE.put(D, 1d);

        graph.put(A, fromA);
        graph.put(B, fromB);
        graph.put(C, fromC);
        graph.put(D, fromD);
        graph.put(E, fromE);

        return graph;
    }

    /**
     * Builds the following Graph A-B-C--------/ \---------D-E
     * <p>
     * Start A End E Shortest Path {A,D,E}
     *
     * @return graph that requires the A* Algorithm to backtrack
     */
    public Map<Node, Map<Node, Double>> getGraphWithBacktracking() {
        Map<Node, Map<Node, Double>> graph = new HashMap<>();

        Node A = new Node("A");
        Node B = new Node("B");
        Node C = new Node("C");
        Node D = new Node("D");
        Node E = new Node("E");

        setStart(A);
        setEnd(E);

        getExpectedPath().clear();
        getExpectedPath().add(0, A);
        getExpectedPath().add(1, D);
        getExpectedPath().add(2, E);

        Map<Node, Double> fromA = new HashMap<>();
        Map<Node, Double> fromB = new HashMap<>();
        Map<Node, Double> fromC = new HashMap<>();
        Map<Node, Double> fromD = new HashMap<>();
        Map<Node, Double> fromE = new HashMap<>();

        fromA.put(B, 1d);
        fromA.put(D, 10d);

        fromB.put(A, 1d);
        fromB.put(C, 1d);

        fromC.put(B, 1d);
        fromC.put(E, 10d);

        fromD.put(A, 10d);
        fromD.put(E, 1d);

        fromE.put(C, 10d);
        fromE.put(D, 1d);

        graph.put(A, fromA);
        graph.put(B, fromB);
        graph.put(C, fromC);
        graph.put(D, fromD);
        graph.put(E, fromE);

        return graph;
    }

    /**
     * Builds the following Graph A-B-C--------/ \---------D-E
     * <p>
     * Start A End E Shortest Path {A,D,E}
     * <p>
     * Uses Node as NodeType and Weight as WeightType
     *
     * @return graph to test generic properties of AStar
     */
    public Map<Node, Map<Node, Weight>> getCustomGraph() {
        Map<Node, Map<Node, Weight>> graph = new HashMap<>();

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");

        getExpectedPath().clear();
        getExpectedPath().add(0, a);
        getExpectedPath().add(1, d);
        getExpectedPath().add(2, e);

        setStart(a);
        setEnd(e);


        Map<Node, Weight> fromA = new HashMap<>();
        Map<Node, Weight> fromB = new HashMap<>();
        Map<Node, Weight> fromC = new HashMap<>();
        Map<Node, Weight> fromD = new HashMap<>();
        Map<Node, Weight> fromE = new HashMap<>();

        fromA.put(b, new Weight(1));
        fromA.put(d, new Weight(10));

        fromB.put(a, new Weight(1));
        fromB.put(c, new Weight(1));

        fromC.put(b, new Weight(1));
        fromC.put(e, new Weight(10));

        fromD.put(a, new Weight(10));
        fromD.put(e, new Weight(1));

        fromE.put(c, new Weight(10));
        fromE.put(d, new Weight(1));
        graph.put(d, fromD);
        graph.put(c, fromC);

        graph.put(a, fromA);
        graph.put(b, fromB);
        graph.put(e, fromE);

        return graph;
    }

    /**
     * Builds the following graph A |\ B D | | C E |/ F
     * <p>
     * Start A End E Shortest Path {A,D,E,F} Longest Path {A,B,C,F}
     *
     * @return path to test comparator with longest and shortest path
     */
    public Map<Node, Map<Node, Weight>> getGraphWithLongAndShortPath() {
        Map<Node, Map<Node, Weight>> graph = new HashMap<>();

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");

        getExpectedPath().clear();
        getExpectedPath().add(0, a);
        getExpectedPath().add(1, d);
        getExpectedPath().add(2, e);
        getExpectedPath().add(3, f);

        getExpectedPath2().clear();
        getExpectedPath2().add(0, a);
        getExpectedPath2().add(1, b);
        getExpectedPath2().add(2, c);
        getExpectedPath2().add(3, f);


        setStart(a);
        setEnd(f);


        Map<Node, Weight> fromA = new HashMap<>();
        Map<Node, Weight> fromB = new HashMap<>();
        Map<Node, Weight> fromC = new HashMap<>();
        Map<Node, Weight> fromD = new HashMap<>();
        Map<Node, Weight> fromE = new HashMap<>();
        Map<Node, Weight> fromF = new HashMap<>();


        fromA.put(b, new Weight(3));
        fromA.put(d, new Weight(1));

        fromB.put(a, new Weight(3));
        fromB.put(c, new Weight(4));

        fromC.put(b, new Weight(4));
        fromC.put(f, new Weight(5));

        fromD.put(a, new Weight(1));
        fromD.put(e, new Weight(1));

        fromE.put(d, new Weight(1));
        fromE.put(f, new Weight(1));

        fromF.put(c, new Weight(5));
        fromF.put(e, new Weight(1));
        graph.put(d, fromD);
        graph.put(e, fromE);
        graph.put(a, fromA);
        graph.put(b, fromB);
        graph.put(c, fromC);
        graph.put(f, fromF);

        return graph;
    }


    public Map<Node, Map<Node, Weight>> getGraphWithMultipleSpecials() {
        Map<Node, Map<Node, Weight>> graph = new HashMap<>();

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");
        Node g = new Node("G");

        getExpectedPath().clear();
        getExpectedPath().add(0, a);
        getExpectedPath().add(1, c);
        getExpectedPath().add(2, f);

        setStart(a);
        setEnd(f);


        Map<Node, Weight> fromA = new HashMap<>();
        Map<Node, Weight> fromB = new HashMap<>();
        Map<Node, Weight> fromC = new HashMap<>();
        Map<Node, Weight> fromD = new HashMap<>();
        Map<Node, Weight> fromE = new HashMap<>();
        Map<Node, Weight> fromF = new HashMap<>();
        Map<Node, Weight> fromG = new HashMap<>();


        fromA.put(b, new Weight(1));
        fromA.put(c, new Weight(2));

        fromB.put(a, new Weight(1));
        fromB.put(d, new Weight(6));
        fromB.put(e, new Weight(5));

        fromC.put(a, new Weight(2));
        fromC.put(f, new Weight(2));
        fromC.put(g, new Weight(3));

        fromD.put(b, new Weight(6));

        fromE.put(b, new Weight(5));

        fromF.put(c, new Weight(2));

        fromG.put(c, new Weight(3));

        graph.put(a, fromA);
        graph.put(b, fromB);
        graph.put(c, fromC);
        graph.put(d, fromD);
        graph.put(e, fromE);
        graph.put(f, fromF);
        graph.put(g, fromG);

        return graph;
    }


}
