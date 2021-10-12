/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.tree;

import science.aist.machinelearning.constraint.Constraint;
import science.aist.machinelearning.constraint.ConstraintCalculation;

/**
 * Node for a ConstraintPriorityTree. Will check if own constraint is satisfied. If constraint is not satisfied, returns
 * null. Otherwise, will check nodes below for additional constraints.
 * <p>
 * If the other nodes return null, then no additional constraints have been found, and calculation has to be done at
 * this node.
 * <p>
 * If the other nodes return a result not null, then this result will be returned.
 *
 * @param <RT> Return Type
 * @param <CT> Constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ConstraintPriorityTreeNode<RT, CT> extends PriorityTreeNode<RT, CT> {

    private Constraint<CT> constraint;

    private ConstraintCalculation<RT, CT> calculation;

    public RT evaluate(CT object) {

        //check if we fulfill the constraint of the current node
        if (this.constraint.evaluate(object)) {
            RT result = null;
            //look trough the child nodes
            for (PriorityTreeNode<RT, CT> node : getChildNodes()) {
                result = node.evaluate(object);
                //if one of the child nodes returns a result that is not null
                //then we've found a better constrained solution and stop looking
                if (result != null) {
                    break;
                }
            }
            //check if one of the children found a solution
            //calculate own result, if children couldn't calculate the result
            return result == null ? calculation.calculate(object) : result;
        }

        //constraint is not satisfied, return null
        return null;
    }

    public Constraint<CT> getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint<CT> constraint) {
        this.constraint = constraint;
    }

    public ConstraintCalculation<RT, CT> getCalculation() {
        return calculation;
    }

    public void setCalculation(ConstraintCalculation<RT, CT> calculation) {
        this.calculation = calculation;
    }
}
