/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.cmaes;

/**
 * This is an interface that all problems that shall be solved by {@link CovarianceMatrixAdaptionEvolutionStrategy} MUST
 * implement
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface FloatValueProblem {

    int getVariableCount();

}
