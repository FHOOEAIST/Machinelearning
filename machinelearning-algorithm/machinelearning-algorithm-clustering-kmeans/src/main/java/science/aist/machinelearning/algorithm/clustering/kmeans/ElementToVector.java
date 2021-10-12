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
 * <p>Maps a given element to a multidimensional vector space</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public interface ElementToVector<T> {
    /**
     * Maps the given element to a vector
     *
     * @param element the element to be mapped
     * @return the vector
     */
    double[] mapElementToVector(T element);
}
