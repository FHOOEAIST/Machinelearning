/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.cmaes;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.cmaes.operator.DefaultRealValuedSolutionBuilder;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oliver Krauss
 * @since 1.0
 */

public class CovarianceMatrixAdaptionEvolutionStrategyTest {

    private final GenericEvaluatorImpl<Double, SampleProblem> evaluator = new GenericEvaluatorImpl<>();
    private final double A = 3000;
    private final double B = -7.5689;
    private final double C = 30;
    private final double D = -20;
    private final double E = 0;
    CovarianceMatrixAdaptionEvolutionStrategy<Double, SampleProblem> cmaes = new CovarianceMatrixAdaptionEvolutionStrategy<>();

    @BeforeClass
    public void setUp() {
        cmaes.setCheckConsistency(true);

        Map<CachetEvaluator<Double, SampleProblem>, Double> cachets = new HashMap<>();
        cachets.put(new SampleCachetEvaluator(), 1.0);
        evaluator.setCachetEvaluators(cachets);
        cmaes.setEvaluator(evaluator);

        cmaes.setSolutionBuilder(new DefaultRealValuedSolutionBuilder<>());
        cmaes.setMaximumGenerations(5000); // note getting Quality 0 is LUCK with anything less than 15k generations. I wanted to minimize runtime for Jenkins
    }

    @Test
    public void test() {
        // given
        List<ProblemGene<SampleProblem>> problemList = new ArrayList<>();
        problemList.add(new ProblemGene<>(new SampleProblem()));
        Problem<SampleProblem> p = new Problem<>(problemList);

        // when
        Solution<Double, SampleProblem> solution = cmaes.solve(p);
        System.out.println("Solved: " + solution.getQuality());
        System.out.println("A: " + solution.getSolutionGenes().get(0).getGene());
        System.out.println("B: " + solution.getSolutionGenes().get(1).getGene());
        System.out.println("C: " + solution.getSolutionGenes().get(2).getGene());
        System.out.println("D: " + solution.getSolutionGenes().get(3).getGene());
        System.out.println("E: " + solution.getSolutionGenes().get(4).getGene());


        // then
        Assert.assertNotNull(solution);
        Assert.assertTrue(solution.getQuality() < 1.0E-14);
        Assert.assertTrue(solution.getSolutionGenes().get(0).getGene() - A < 1.0E-14);
        Assert.assertTrue(solution.getSolutionGenes().get(1).getGene() - B < 1.0E-14);
        Assert.assertTrue(solution.getSolutionGenes().get(2).getGene() - C < 1.0E-14);
        Assert.assertTrue(solution.getSolutionGenes().get(3).getGene() - D < 1.0E-14);
        Assert.assertTrue(solution.getSolutionGenes().get(4).getGene() - E < 1.0E-14);
    }

    private static class SampleProblem implements FloatValueProblem {
        @Override
        public int getVariableCount() {
            return 5;
        }
    }

    private class SampleCachetEvaluator implements CachetEvaluator<Double, SampleProblem> {

        @Override
        public double evaluateQuality(Solution<Double, SampleProblem> solution) {
            double a = solution.getSolutionGenes().get(0).getGene();
            double b = solution.getSolutionGenes().get(1).getGene();
            double c = solution.getSolutionGenes().get(2).getGene();
            double d = solution.getSolutionGenes().get(3).getGene();
            double e = solution.getSolutionGenes().get(4).getGene();
            return Math.abs(A - a) + Math.abs(B - b) + Math.abs(C - c) + Math.abs(D - d) + Math.abs(E - e);
        }

        @Override
        public String getName() {
            return "sample-0.1";
        }
    }

}
