/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example.mockup;

import science.aist.machinelearning.algorithm.mapping.WeightCalculator;

import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class SimpleWeightCalculator implements WeightCalculator<Node, Double> {

    @Override
    public Double weight(Number accumulatedWeight, Number weightForOneNode, Number estimatedWeight) {
        return accumulatedWeight.doubleValue() + weightForOneNode.doubleValue();
    }

    @Override
    public Double estimatedWeight(Node from, Node to, Map<Node, Map<Node, Double>> graph) {
        return 0d;
    }
}
