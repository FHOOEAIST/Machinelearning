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
import java.util.Map;

/**
 * Cachet that evaluates quality by checking which nodes exist in the graph and then setting costs according to the
 * nodes used. This way, one can set costs for each node and force the graph to evolve in a very specific manner.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPNodeCostCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    private Map<Class, Double> costMap;

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            solution.getCachets().add(new Cachet(1_000_000.0, "GPNodeCostCachet"));
            return 1_000_000; //super bad quality
        }

        double quality = evaluateQuality(solution.getSolutionGenes().get(0).getGene(), costMap, new ArrayList<>());

        if (quality < 0 || quality > 1_000_000) {
            quality = 1_000_000;
        }

        solution.getCachets().add(new Cachet(quality, "GPNodeCostCachets"));

        return quality;

    }

    @Override
    public String getName() {
        return "GPNodeCostCachet";
    }

    private double evaluateQuality(GPGraphNode currentNode, Map<Class, Double> costMap, Collection<GPGraphNode> previousNodes) {
        if (!previousNodes.contains(currentNode)) {
            previousNodes.add(currentNode);

            double cost = costMap.get(currentNode.getClass());

            if (currentNode instanceof FunctionalGPGraphNode) {
                FunctionalGPGraphNode casted = (FunctionalGPGraphNode) currentNode;

                for (GPGraphNode nextNode : (List<GPGraphNode>) casted.getChildNodes()) {
                    cost += evaluateQuality(nextNode, costMap, previousNodes);
                }
            }

            return cost;
        }

        return 0.0;
    }

    public void setCostMap(Map<Class, Double> costMap) {
        this.costMap = costMap;
    }
}
