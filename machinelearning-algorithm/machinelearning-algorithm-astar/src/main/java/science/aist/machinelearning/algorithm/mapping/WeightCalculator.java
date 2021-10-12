/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mapping;


import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public interface WeightCalculator<NT, WT> {

    /**
     * determines on how to sum up accumulatedWeight, weightForOneNode and estimatedWeight
     *
     * @param accumulatedWeight weight of the current path
     * @param weightForOneNode  weight from the last node of the current path to the next node
     * @param estimatedWeight   estimated weight of the next node
     * @return sum of accumulatedWeight, weightForOneNode and estimatedWeight
     */
    WT weight(Number accumulatedWeight, Number weightForOneNode, Number estimatedWeight);

    /**
     * calculates or estimates the weight of a node to the end of the path or between the nodes from and to
     *
     * @param from  node
     * @param to    to
     * @param graph graph
     * @return weight from the node to the end of the path
     */
    WT estimatedWeight(NT from, NT to, Map<NT, Map<NT, WT>> graph);

}
