/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.visualisation.jgraphx;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.ValueContainer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses JGraphX to visualise the graph and all its nodes. Will show nodes and edges, as well as the classes inside of
 * the nodes.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPVisualisation extends JFrame {

    private int nodeWidth = 80;
    private int nodeHeight = 30;

    private int maximumDepth = 0;

    /**
     * Contains all nodes that have already been drawn in a given graph.
     */
    private Map<GPGraphNode, Object> drawnNodes = null;

    public GPVisualisation() throws HeadlessException {
    }

    public GPVisualisation(int nodeWidth, int nodeHeight) throws HeadlessException {
        this.nodeWidth = nodeWidth;
        this.nodeHeight = nodeHeight;
    }

    /**
     * Draws Graph of the given node, including all children. Will show classNames as well as edges between the nodes.
     *
     * @param node node to draw
     */
    public void drawGPGraph(GPGraphNode node) {

        //necessary to later set the correct window size
        maximumDepth = 0;
        drawnNodes = new HashMap<>();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();

        //start to draw the root node
        drawNode(node, null, graph, 0, 0, screenSize.width, parent);

        graph.getModel().endUpdate();

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

        //show the resulting graph
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setSize(screenSize.width, (maximumDepth + 1) * (nodeHeight + 2) + 60);
        this.setVisible(true);

        while (this.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Draws a single node and then connects the new node with the previous node. Will then also draw the child nodes of
     * the new node.
     *
     * @param node         node to draw
     * @param previousNode parent of the node, required to connect with edge
     * @param graph        mxGraph object
     * @param depth        depth required to set the correct Y position
     * @param startWidth   start position, required to set the correct X position
     * @param endWidth     end position, required to set the correct X position
     * @param parent       parent object, required by mxGraph
     */
    private void drawNode(GPGraphNode node, Object previousNode, mxGraph graph, int depth, double startWidth, double endWidth, Object parent) {

        if (depth > maximumDepth) {
            maximumDepth = depth;
        }

        //get the classname, remove the "node" suffix
        String className = node.getClass().getSimpleName();
        className = className.substring(0, className.length() - 4);

        if (node instanceof ValueContainer) {
            ValueContainer value = (ValueContainer) node;
            className += "\n(" + value.getValue() + ")";
        }

        Object newNode;
        if (node instanceof CacheableGPGraphNode && ((CacheableGPGraphNode) node).isCached()) {
            newNode = graph.insertVertex(parent, null, className, (endWidth - startWidth) / 2 + startWidth, depth * (nodeHeight + 2), nodeWidth, nodeHeight, "fillColor=green");
        } else {
            newNode = graph.insertVertex(parent, null, className, (endWidth - startWidth) / 2 + startWidth, depth * (nodeHeight + 2), nodeWidth, nodeHeight);
        }


        drawnNodes.put(node, newNode);

        //set edges to previous nodes
        if (previousNode != null) {
            graph.insertEdge(parent, null, "", previousNode, newNode);
        }

        //if its a functional node, start to draw the children
        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode casted = (FunctionalGPGraphNode) node;

            double currentChild = 0;
            double children = casted.getChildNodes().size();
            double widthPart = (endWidth - startWidth) / children;

            for (GPGraphNode child : (ArrayList<GPGraphNode>) casted.getChildNodes()) {

                if (drawnNodes.containsKey(child)) {
                    graph.insertEdge(parent, null, "", newNode, drawnNodes.get(child));
                } else {
                    drawNode(child, newNode, graph, depth + 1, startWidth + currentChild * widthPart, startWidth + (currentChild + 1) * widthPart, parent);
                    currentChild++;
                }
            }
        }
    }

    public void setNodeWidth(int nodeWidth) {
        this.nodeWidth = nodeWidth;
    }

    public void setNodeHeight(int nodeHeight) {
        this.nodeHeight = nodeHeight;
    }
}
