/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.ga;


import science.aist.machinelearning.core.Solution;

import java.util.List;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Selector<ST, PT> {

    /**
     * @param population the population
     * @return the solution
     */
    Solution<ST, PT> select(List<Solution<ST, PT>> population);
}
