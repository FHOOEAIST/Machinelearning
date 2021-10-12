/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.fitness;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.problem.GPProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Checks the quality of the GP-heuristic by looking at the depth of the tree. The bigger the depth, the worse the
 * quality.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPDepthCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    private double quality = 0.0;

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            solution.getCachets().add(new Cachet(1_000_000.0, "GPDepthCachet"));
            return 1_000_000; //super bad quality
        }

        quality = 0.0;

        evaluateDepth(solution.getSolutionGenes().get(0).getGene(), 0, new ArrayList<>());

        if (quality < 0 || quality > 1_000_000) {
            quality = 1_000_000;
        }

        solution.getCachets().add(new Cachet(quality, "GPDepthCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPDepthCachet";
    }

    private void evaluateDepth(GPGraphNode currentNode, int depth, Collection<GPGraphNode> previousNodes) {
        if (!previousNodes.contains(currentNode)) {
            previousNodes.add(currentNode);

            if (depth > quality) {
                quality = depth;
            }

            if (currentNode instanceof FunctionalGPGraphNode) {
                FunctionalGPGraphNode casted = (FunctionalGPGraphNode) currentNode;

                for (GPGraphNode nextNode : (List<GPGraphNode>) casted.getChildNodes()) {
                    evaluateDepth(nextNode, depth + 1, previousNodes);
                }
            }
        }
    }
}
