/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm;

import science.aist.machinelearning.algorithm.gene.ShortestPathProblemGene;
import science.aist.machinelearning.algorithm.mapping.AStarGeneCreator;
import science.aist.machinelearning.algorithm.mapping.WeightCalculator;
import science.aist.machinelearning.core.AbstractAlgorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class AStar<NT, WT extends Number> extends AbstractAlgorithm<List<NT>, ShortestPathProblemGene<NT, WT>> {

    /**
     * solutionCreator
     */
    private final SolutionCreator<List<NT>, ShortestPathProblemGene<NT, WT>> solutionCreator = new OneToOneSolutionCreator<>();

    /**
     * specific options
     */
    private final Map<String, Descriptor> specificOptions = new HashMap<>();

    /**
     * constructor
     */
    public AStar() {
        Comparator<WT> comparator = Comparator.comparingDouble(Number::doubleValue);
        Descriptor<Comparator<WT>> descriptor = new Descriptor<>(comparator);
        this.specificOptions.put("comparator", descriptor);

        WeightCalculator<NT, Number> weightCalculator = new WeightCalculator<>() {
            @Override
            public Number weight(Number accumulatedWeight, Number weightForOneNode, Number estimatedWeight) {
                return accumulatedWeight.doubleValue() + weightForOneNode.doubleValue() + estimatedWeight.doubleValue();
            }

            @Override
            public Number estimatedWeight(NT from, NT to, Map<NT, Map<NT, Number>> graph) {
                return 0;
            }
        };
        Descriptor<WeightCalculator<NT, Number>> weightCalculatorDescriptor = new Descriptor<>(weightCalculator);
        this.specificOptions.put("weightCalculator", weightCalculatorDescriptor);
    }

    @Override
    protected Map<String, Descriptor> getSpecificOptions() {

        return specificOptions;
    }

    @Override
    protected boolean setSpecificOption(String s, Descriptor descriptor) {
        if (s.equals("comparator") && descriptor != null) {
            specificOptions.put(s, descriptor);
            return true;
        } else if (s.equals("weightCalculator") && descriptor != null) {
            specificOptions.put(s, descriptor);
        }
        return false;
    }

    /**
     * solves the shortest-path-problem
     *
     * @param problem to be solved
     * @return solution for the shortest-path-problem
     */
    @Override
    public Solution<List<NT>, ShortestPathProblemGene<NT, WT>> solve(Problem<ShortestPathProblemGene<NT, WT>> problem) {
        AStarGeneCreator<NT, WT> geneCreator = new AStarGeneCreator<>();
        geneCreator.setOptions(specificOptions);
        this.solutionCreator.setGeneCreator(geneCreator);
        return solutionCreator.createSolution(problem);
    }

    /**
     * solves the shortest-path-problem
     *
     * @param problem  problem to be solved
     * @param solution ignored
     * @return solution for the shortest-path-problem
     */
    @Override
    public Solution<List<NT>, ShortestPathProblemGene<NT, WT>> solve(Problem<ShortestPathProblemGene<NT, WT>> problem, Solution<List<NT>, ShortestPathProblemGene<NT, WT>> solution) {
        AStarGeneCreator<NT, WT> geneCreator = new AStarGeneCreator<>();
        geneCreator.setOptions(specificOptions);
        this.solutionCreator.setGeneCreator(geneCreator);
        return solutionCreator.createSolution(problem);
    }
}
