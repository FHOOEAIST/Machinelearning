/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.clustering.kmeans.distance;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * <p>Tests {@link EuclideanSquaredVectorDistance}</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class EuclideanSquaredVectorDistanceTest {
    @Test
    public void test() {
        // given
        EuclideanSquaredVectorDistance esvd = new EuclideanSquaredVectorDistance();
        double[] a1 = new double[]{1, 9, 23, 83};
        double[] a2 = new double[]{4, 17, 53, 76};

        // when
        double distance = esvd.calculateDistance(a1, a2);

        // then
        Assert.assertEquals(distance, 1022.0, 0.1);
    }
}
