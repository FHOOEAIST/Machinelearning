/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.clustering.kmeans.distance;

import science.aist.machinelearning.algorithm.clustering.kmeans.VectorDistance;

/**
 * <p>Calculates the euclidean distance between two vectors</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class EuclideanVectorDistance implements VectorDistance {
    /**
     * Euclidean distance is just the square root of the squared euclidean distance, so we use this function as a
     * starting point
     */
    private final EuclideanSquaredVectorDistance euclideanSquaredVectorDistance = new EuclideanSquaredVectorDistance();

    @Override
    public double calculateDistance(double[] vector1, double[] vector2) {
        return Math.sqrt(euclideanSquaredVectorDistance.calculateDistance(vector1, vector2));
    }
}
