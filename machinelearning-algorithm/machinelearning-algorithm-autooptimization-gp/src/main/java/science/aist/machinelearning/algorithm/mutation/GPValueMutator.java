/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.ValueContainer;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.problem.GPProblem;

import java.util.ArrayList;
import java.util.Random;

/**
 * Changes values of nodes depending on the settings. Will look for nodes that are {@link ValueContainer}, and then
 * change it depending on the settings.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPValueMutator extends RollbackRandomNGenesMutator<ResultNode, GPProblem> {

    private final Random r = new Random();
    /**
     * Decides how much the value can be changed. E.g. changeByMax of 1.0 can add or subtract up to the entire value of
     * the valueContainer.
     */
    private Double changeByMax = 0.50;

    @Override
    protected SolutionGene<ResultNode, GPProblem> createGeneByMutation(SolutionGene<ResultNode, GPProblem> gene) {

        SolutionGene<ResultNode, GPProblem> mutation = new SolutionGene<>();
        mutation.setProblemGenes(gene.getProblemGenes());

        //create a new gene by reconstructing the graph
        ResultNode root = BasicNodeUtil.deepCopyForGraph(gene.getGene());

        //find nodes which are valueContainer
        findAndChangeValueContainer(root.getChildNodes().get(0));

        mutation.setGene(root);

        return mutation;
    }

    public void setChangeByMax(Double changeByMax) {
        this.changeByMax = changeByMax;
    }

    /**
     * Looks for Nodes implementing the ValueContainer interface. Will then change the values of those nodes.
     *
     * @param node the graph node
     */
    private void findAndChangeValueContainer(GPGraphNode node) {
        if (node instanceof ValueContainer) {
            ValueContainer nodeWithValue = (ValueContainer) node;

            //with boolean, can only turn true to false and opposite
            if (nodeWithValue.getValue().getClass().equals(Boolean.class)) {
                nodeWithValue.setValue(!((Boolean) nodeWithValue.getValue()));
            } else if (nodeWithValue.getValue().getClass().equals(Double.class)) {
                Double value = (Double) nodeWithValue.getValue();
                value += changeByMax * value * r.nextDouble() * (r.nextBoolean() ? 1 : -1);
                nodeWithValue.setValue(value);
            } else if (nodeWithValue.getValue().getClass().equals(Float.class)) {
                Float value = (Float) nodeWithValue.getValue();
                value += changeByMax.floatValue() * value * r.nextFloat() * (r.nextBoolean() ? 1 : -1);
                nodeWithValue.setValue(value);
            } else if (nodeWithValue.getValue().getClass().equals(Integer.class)) {
                Integer value = (Integer) nodeWithValue.getValue();
                value += Double.valueOf(changeByMax.doubleValue() * (double) value * r.nextDouble() * (r.nextBoolean() ? 1 : -1)).intValue();
                nodeWithValue.setValue(value);
            }
        }

        //look through the children to find more ValueContainers
        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode functionalNode = (FunctionalGPGraphNode) node;

            for (GPGraphNode child : (ArrayList<GPGraphNode>) functionalNode.getChildNodes()) {
                findAndChangeValueContainer(child);
            }
        }
    }

}
