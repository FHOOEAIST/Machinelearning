/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.constraint;

import science.aist.machinelearning.tree.ConstraintPriorityTreeNode;

import java.io.Serializable;

/**
 * Interface for creating constraints, required by the {@link ConstraintPriorityTreeNode}. Evaluate constraints of the
 * type CT and return true or false.
 *
 * @param <CT> constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface Constraint<CT> extends Serializable {

    /**
     * Evaluates the given object.
     *
     * @param object object to evaluate
     * @return true = object satisfied, false = not satisfied
     */
    boolean evaluate(CT object);
}
