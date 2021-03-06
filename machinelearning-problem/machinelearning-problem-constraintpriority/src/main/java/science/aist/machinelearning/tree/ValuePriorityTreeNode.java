/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.tree;

/**
 * Node that changes the value depending on the implementation. Will then hand the changed value over to its child
 * nodes.
 *
 * @param <RT> Return Type
 * @param <CT> Constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class ValuePriorityTreeNode<RT, CT> extends PriorityTreeNode<RT, CT> {

    public RT evaluate(CT object) {

        //change the value of the constraint
        object = changeValue(object);

        RT result = null;
        //look trough the child nodes
        for (PriorityTreeNode<RT, CT> node : getChildNodes()) {
            result = node.evaluate(object);
            //if one of the child nodes returns a result that is not null
            //then we've found a solution and stop looking
            if (result != null) {
                break;
            }
        }
        return result;
    }

    protected abstract CT changeValue(CT constraint);
}
