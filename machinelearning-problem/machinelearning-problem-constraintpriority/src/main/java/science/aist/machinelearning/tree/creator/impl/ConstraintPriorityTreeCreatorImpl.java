/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.tree.creator.impl;

import science.aist.machinelearning.constraint.Constraint;
import science.aist.machinelearning.constraint.ConstraintCalculation;
import science.aist.machinelearning.tree.ConstraintPriorityTreeNode;
import science.aist.machinelearning.tree.PriorityTreeNode;
import science.aist.machinelearning.tree.creator.ConstraintPriorityTreeCreator;

import java.util.*;

/**
 * Implementation for the creation of trees suited to the given constraints. Will check which constraints-settings
 * appear the most and build the tree in such a way, that those constraints will be calculated earlier.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ConstraintPriorityTreeCreatorImpl<RT, CT> implements ConstraintPriorityTreeCreator<RT, CT> {

    private Map<Collection<Constraint<CT>>, ConstraintCalculation<RT, CT>> calculationSettings;

    @Override
    public ConstraintPriorityTreeNode<RT, CT> createTree(List<Constraint<CT>> constraints) {

        //Build the tree and set the different constraints
        ConstraintPriorityTreeNode<RT, CT> tree = buildTree(new ArrayList<>(constraints), 0);

        //set the calculations for the nodes in the tree
        distributeCalculations(tree, new HashMap<>(calculationSettings), null);

        return tree;
    }

    /**
     * Creates a new TreeNode and sets the constraint using the currentConstraintIndex. Depending on depth, will then
     * this method again to create its children.
     * <p>
     * Stops when the currentConstraintIndex is bigger than the size of the constraintList.
     *
     * @param constraintList         List of constraints that get distributed on the tree
     * @param currentConstraintIndex current depth in the tree
     * @return new tree node and subTrees with constraints
     */
    private ConstraintPriorityTreeNode<RT, CT> buildTree(List<Constraint<CT>> constraintList, int currentConstraintIndex) {

        //if the index is greater than size of the list, then we have no more constraints to distribute
        if (currentConstraintIndex >= constraintList.size()) {
            return null;
        }

        //create a new node and set the constraint for the current depth
        ConstraintPriorityTreeNode<RT, CT> node = new ConstraintPriorityTreeNode<>();
        node.setConstraint(constraintList.get(currentConstraintIndex));

        //create the child nodes and set them to the current node
        for (int i = currentConstraintIndex + 1; i < constraintList.size(); i++) {
            node.getChildNodes().add(buildTree(constraintList, i));
        }

        //return the node
        return node;
    }

    /**
     * Goes through the entire tree and sets the correct calculation, depending on the map of constraints. Will use a
     * list of previous constraints to determine which calculation is correct.
     *
     * @param node                node to set the calculation for
     * @param calculations        map of constraints and calculations
     * @param previousConstraints constraints of child nodes of the current node
     */
    private void distributeCalculations(ConstraintPriorityTreeNode<RT, CT> node, Map<Collection<Constraint<CT>>, ConstraintCalculation<RT, CT>> calculations, Collection<Constraint<CT>> previousConstraints) {

        Collection<Constraint<CT>> key = null;

        //check if we're at the root node
        if (previousConstraints == null) {
            previousConstraints = new ArrayList<>();
            for (Map.Entry<Collection<Constraint<CT>>, ConstraintCalculation<RT, CT>> entry : calculations.entrySet()) {
                //if we found a constraintList size 0, then we got the calculations for the root
                if (entry.getKey().size() == 0) {
                    key = entry.getKey();
                    break;
                }
            }
        } else {
            previousConstraints.add(node.getConstraint());
            for (Map.Entry<Collection<Constraint<CT>>, ConstraintCalculation<RT, CT>> entry : calculations.entrySet()) {
                //if we found a constraintList of the same size and containing all our constraints, then we got the correct calculation
                if (entry.getKey().size() == previousConstraints.size() && entry.getKey().containsAll(previousConstraints)) {
                    key = entry.getKey();
                    break;
                }
            }
        }

        //set the calculation to the node and remove it from the map of calculations
        node.setCalculation(calculations.remove(key));

        //call all the children and try to set their calculations
        for (PriorityTreeNode<RT, CT> child : node.getChildNodes()) {
            distributeCalculations(node, calculations, previousConstraints);
        }

        //remove the constraint again, or it would influence other nodes as well and not only the children
        previousConstraints.remove(node.getConstraint());
    }

    public void setCalculationSettings(Map<Collection<Constraint<CT>>, ConstraintCalculation<RT, CT>> calculationSettings) {
        this.calculationSettings = calculationSettings;
    }
}
