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
 * <p>Calculates the cosine similarity between two vectors</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class CosineVectorDistance implements VectorDistance {
    @Override
    public double calculateDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) throw new IllegalArgumentException("Only supports vectors of same size");
        // Calculation: A x B / (len(A) * len(B))
        return vectorProduct(vector1, vector2) / (vectorLength(vector1) * vectorLength(vector2));
    }

    /**
     * Calculates the length of a vector
     *
     * @param vector the vector
     * @return the length of the vector
     */
    private double vectorLength(double[] vector) {
        int sumSquaredLength = 0;
        for (double vectorI : vector) {
            sumSquaredLength += vectorI * vectorI;
        }
        return Math.sqrt(sumSquaredLength);
    }

    /**
     * Calculates the product of two vectors
     *
     * @param vector1 vector 1
     * @param vector2 vector 2
     * @return the scalar product of the vectors
     */
    private double vectorProduct(double[] vector1, double[] vector2) {
        double product = 0;
        for (int i = 0; i < vector1.length; i++) {
            product += vector1[i] * vector2[i];
        }
        return product;
    }
}
