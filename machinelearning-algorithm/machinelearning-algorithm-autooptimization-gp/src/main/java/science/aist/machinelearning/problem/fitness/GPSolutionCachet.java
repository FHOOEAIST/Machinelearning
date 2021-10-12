/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.fitness;

import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.algorithm.gp.util.GPTrim;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.problem.GPProblem;

/**
 * Cachet that evaluates the quality of a heuristic depending on how many solutions its going to create. The more
 * solutions created, the worse.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPSolutionCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().isEmpty()) {
            solution.getCachets().add(new Cachet(1_000_000.0, "GPSolutionCachet"));
            return 1_000_000;
        }

        double quality = BasicNodeUtil.solutionsCreatedByGraph(GPTrim.trimGraph(solution.getSolutionGenes().get(0).getGene()));

        //quality < 0 can sometimes happen if the tree is very convoluted and it overflows back into negative values
        if (quality < 0 || quality > 1_000_000) {
            quality = 1_000_000;
        }


        solution.getCachets().add(new Cachet(quality, "GPSolutionCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPSolutionCachet";
    }
}
