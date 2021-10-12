/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.cmaes.operator;

import science.aist.machinelearning.algorithm.cmaes.FloatValueProblem;
import science.aist.machinelearning.core.Gene;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;

/**
 * The default is to leave the double array as an actual double array Produces a solution with one gene per double
 * value
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class DefaultRealValuedSolutionBuilder<PT extends FloatValueProblem> implements RealValuedSolutionBuilder<Double, PT> {

    /**
     * Transforms the variable vector into a solution, whatever this solution may be
     *
     * @param variableVector Vector of variables to be represented in the solution
     * @return Solution that represents the double vector
     */
    public Solution<Double, PT> transformToSolution(double[] variableVector, Problem<PT> problem) {
        int variableCount = problem.getProblemGenes().get(0).getGene().getVariableCount();
        if (variableCount != variableVector.length) {
            throw new IllegalArgumentException("The solution is NOT valid as its size is not equal to the problems variable count");
        }

        Solution<Double, PT> result = new Solution<>();

        for (double aVariableVector : variableVector) {
            result.addGene(new SolutionGene<>(aVariableVector, problem.getProblemGenes()));
        }

        return result;
    }

    @Override
    public double[] getOriginalVector(Solution<Double, PT> solution) {
        return solution.getSolutionGenes().stream().mapToDouble(Gene::getGene).toArray();
    }

    @Override
    public double getVaribleFromOriginalVector(Solution<Double, PT> solution, int index) {
        return solution.getSolutionGenes().get(index).getGene();
    }
}
