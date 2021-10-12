/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Problem for the creation of GP-heuristics that should solve a collection of problems. Consists of nodes to use during
 * creation of the GP-Graph.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPProblem {

    /**
     * GraphNodes that may be used for calculation. Key = fitting return type for the node Value = all nodes that return
     * the same type as the key
     * <p>
     * Contains functional and terminal nodes.
     */
    private final Map<Class, ArrayList<GPGraphNode>> validGraphNodes;

    /**
     * GraphNodes that contain no children and define the end of the graph. Key = fitting return type for the node Value
     * = all nodes that return the same type as the key
     */
    private final Map<Class, ArrayList<GPGraphNode>> terminalGraphNodes;

    /**
     * GraphNodes that contain children and will broaden the graph. Key = fitting return type for the node Value = all
     * nodes that return the same type as the key
     */
    private final Map<Class, ArrayList<GPGraphNode>> functionalGraphNodes;

    /**
     * Contains other settings that the nodes may require during creation (like evaluator, solutionCreators, etc.).
     * Those settings will then be set by the "setOption" method of the graphs.
     */
    private final Map<Class, Map<String, Descriptor>> nodeSettings;

    public GPProblem(Map<Class, ArrayList<GPGraphNode>> validGraphNodes, Map<Class, Map<String, Descriptor>> nodeSettings) {
        this.validGraphNodes = validGraphNodes;
        this.nodeSettings = nodeSettings;


        //find the terminal graphNodes
        this.terminalGraphNodes = new HashMap<>();
        this.functionalGraphNodes = new HashMap<>();

        for (Map.Entry<Class, ArrayList<GPGraphNode>> entry : validGraphNodes.entrySet()) {
            ArrayList<GPGraphNode> terminals = new ArrayList<>();
            ArrayList<GPGraphNode> functionals = new ArrayList<>();

            terminalGraphNodes.put(entry.getKey(), terminals);
            functionalGraphNodes.put(entry.getKey(), functionals);

            for (GPGraphNode node : entry.getValue()) {
                //if the node is not a functional node, then its a terminal node
                if (!(node instanceof FunctionalGPGraphNode)) {
                    terminals.add(node);
                } else {
                    functionals.add(node);
                }
            }
        }
    }

    /**
     * Constructor for the additional setting of other terminals. This way, functionals can also be used like terminals.
     * Functionals that are good to close the tree with (e.g. ForCollectionNode) can be set there.
     *
     * @param validGraphNodes     all graph nodes
     * @param nodeSettings        settings for the different nodes
     * @param additionalTerminals additional nodes that will be added to the terminals
     */
    public GPProblem(Map<Class, ArrayList<GPGraphNode>> validGraphNodes, Map<Class, Map<String, Descriptor>> nodeSettings, Map<Class, ArrayList<GPGraphNode>> additionalTerminals) {
        this(validGraphNodes, nodeSettings);

        for (Map.Entry<Class, ArrayList<GPGraphNode>> entry : terminalGraphNodes.entrySet()) {
            ArrayList<GPGraphNode> terminals = entry.getValue();

            ArrayList<GPGraphNode> additional = additionalTerminals.get(entry.getKey());
            if (additional != null) {
                terminals.addAll(additional);
            }
        }
    }


    public Map<Class, ArrayList<GPGraphNode>> getValidGraphNodes() {
        return validGraphNodes;
    }

    public Map<Class, ArrayList<GPGraphNode>> getTerminalGraphNodes() {
        return terminalGraphNodes;
    }

    public Map<Class, Map<String, Descriptor>> getNodeSettings() {
        return nodeSettings;
    }

    public Map<Class, ArrayList<GPGraphNode>> getFunctionalGraphNodes() {
        return functionalGraphNodes;
    }
}
