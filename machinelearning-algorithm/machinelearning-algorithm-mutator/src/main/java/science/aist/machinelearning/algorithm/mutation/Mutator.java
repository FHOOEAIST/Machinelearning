/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.core.Solution;

/**
 * Interface containing necessary methods for the mutation of solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface Mutator<ST, PT> {

    Solution<ST, PT> mutate(Solution<ST, PT> solution);
}
