/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.tree.creator;

import science.aist.machinelearning.constraint.Constraint;
import science.aist.machinelearning.tree.ConstraintPriorityTreeNode;

import java.util.List;

/**
 * Interface for the implementation of trees with different constraints.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface ConstraintPriorityTreeCreator<RT, CT> {

    /**
     * Builds a PriorityTree using the given constraints.
     *
     * @param constraints behaviour to analyze when creating the tree
     * @return root of the new priority tree
     */
    ConstraintPriorityTreeNode<RT, CT> createTree(List<Constraint<CT>> constraints);
}
