/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.autooptimization.fitness;

import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.problem.autooptimization.AmalgamProblem;

import java.util.Map;

/**
 * Evaluates the values of different algorithms. Will negatively impact quality depending on the size of the population.
 * Higher population usually leads to much bigger runtime, so we penalize it a bit. Otherwise, will always prefer huge
 * numbers of generations.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamGenerationCachet<GT, PT> implements CachetEvaluator<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> {


    @Override
    public double evaluateQuality(Solution<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> solution) {

        double quality = 0;

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            return 100000000; //super bad quality
        }

        for (SolutionGene<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> gene : solution.getSolutionGenes()) {

            Map<String, Descriptor> options = gene.getGene().getOptions();

            //-> GeneticAlgorithm
            if (options.containsKey("populationSize") && options.containsKey("maximumGenerations")) {
                quality += (Integer) options.get("populationSize").getValue() * (Integer) options.get("maximumGenerations").getValue();
            }
            //-> IterativeLocalSearch || LocalSearch
            else if (options.containsKey("maximumGenerations")) {
                quality += (Integer) options.get("maximumGenerations").getValue();
            }
        }

        return quality;
    }

    @Override
    public String getName() {
        return "AmalgamGenerationCachet";
    }

}
