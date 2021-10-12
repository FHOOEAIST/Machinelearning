/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.cmaes.operator;

import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;

/**
 * This Operator is (currently) exclusive to CMAES. CMAES actually does all of its nifty mutation and crossover around a
 * centroid of real values (doubles). It was originally intended to optimize vectors (or matrices as you can represent
 * any matrix as vector), but you can really optimize anything with CMAES as long as you can represent it as a double
 * vector.
 * <p>
 * The responsibility of this class is to transform double[] into a Solution&lt;ST,PT&gt;, whatever that may be.
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface RealValuedSolutionBuilder<ST, PT> {

    /**
     * Transforms the variable vector into a solution, whatever this solution may be
     *
     * @param variableVector Vector of variables to be represented in the solution
     * @return Solution that represents the double vector
     */
    Solution<ST, PT> transformToSolution(double[] variableVector, Problem<PT> problem);

    /**
     * Reverter function as CMAES must be able to access the originals
     *
     * @param solution to be transformed back
     * @return vector that transformToSolution was called with
     */
    double[] getOriginalVector(Solution<ST, PT> solution);

    /**
     * Reverter function for single variable as CMAES must be able to access the originals
     *
     * @param solution to be transformed back
     * @param index    index of variable in original vector
     * @return vector that transformToSolution was called with
     */
    default double getVaribleFromOriginalVector(Solution<ST, PT> solution, int index) {
        return getOriginalVector(solution)[index];
    }
}
