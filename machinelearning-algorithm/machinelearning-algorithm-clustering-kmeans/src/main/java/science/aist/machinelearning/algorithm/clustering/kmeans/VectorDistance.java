/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.clustering.kmeans;

/**
 * <p>Calculate a VectorDistance between two vectors</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public interface VectorDistance {
    /**
     * Calculates the distance between two vectors
     *
     * @param vector1 vector1
     * @param vector2 vector2
     * @return the distance between vector 1 and vector 2
     */
    double calculateDistance(double[] vector1, double[] vector2);
}
