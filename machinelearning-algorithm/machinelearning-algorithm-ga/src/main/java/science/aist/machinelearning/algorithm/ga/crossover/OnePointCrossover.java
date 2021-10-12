/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.ga.crossover;


import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GenericCrossover that splits both individuals by half. Combines them then in a new mapping.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class OnePointCrossover<ST, PT> extends AbstractCrossover<ST, PT> implements Configurable {

    public static int runs = 0;
    private double crossoverPoint = 0.5;

    @Override
    public Solution<ST, PT> breedTwo(Solution<ST, PT> a, Solution<ST, PT> b) {

        if ((a == null || a.getSolutionGenes() == null || a.getSolutionGenes().size() == 0)) {
            if (b == null || b.getSolutionGenes() == null || b.getSolutionGenes().size() == 0) {
                return null;
            }
            return b;
        } else if (b == null || b.getSolutionGenes() == null || b.getSolutionGenes().size() == 0) {
            return a;
        }

        runs++;

        List<SolutionGene<ST, PT>> genes = new ArrayList<>();

        int point = (int) Math.round(a.getSolutionGenes().size() * getCrossoverPoint());
        //take the first half of individual A
        genes.addAll(a.getSolutionGenes().subList(0, point));
        //and second half of individual B
        genes.addAll(b.getSolutionGenes().subList(point, b.getSolutionGenes().size()));

        Solution<ST, PT> crossoverSolution = new Solution<>();
        crossoverSolution.setSolutionGenes(genes);

        return crossoverSolution;
    }

    public double getCrossoverPoint() {
        return crossoverPoint;
    }

    public void setCrossoverPoint(double crossoverPoint) {
        this.crossoverPoint = crossoverPoint;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();
        options.put("crossoverPoint", new Descriptor<>(crossoverPoint));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("crossoverPoint")) {
                setCrossoverPoint((Double) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
