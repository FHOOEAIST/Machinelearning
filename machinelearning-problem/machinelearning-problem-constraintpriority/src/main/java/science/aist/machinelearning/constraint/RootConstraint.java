/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.constraint;

/**
 * Constraint for the root of a ConstraintPriorityTree. Will always return true, forcing calculation of the root node,
 * if no other result has been found in the tree.
 *
 * @param <CT> constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public class RootConstraint<CT> implements Constraint<CT> {

    @Override
    public boolean evaluate(CT object) {
        return true;
    }
}
