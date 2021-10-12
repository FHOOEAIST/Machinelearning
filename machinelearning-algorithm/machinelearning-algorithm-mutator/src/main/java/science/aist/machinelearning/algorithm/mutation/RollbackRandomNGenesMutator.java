/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.Cachet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Doesn't make a copy of the old solution, but remembers genes before mutation. If the new solution ends up worse, will
 * use those genes to rollback and get the old solution.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class RollbackRandomNGenesMutator<ST, PT> extends RandomNGenesMutator<ST, PT> {

    @Override
    public Solution<ST, PT> mutate(Solution<ST, PT> solution) {
        if (solution == null) {
            return null;
        }

        solution = new Solution<>(solution);

        List<Integer> availableIndex = new ArrayList<>();
        for (int i = 0; i < solution.getSolutionGenes().size(); i++) {
            availableIndex.add(i);
        }

        //collect data about the old quality
        double previousQuality = solution.getQuality();
        List<Cachet> oldCachets = new ArrayList<>(solution.getCachets());
        //keep info about which genes have been mutated and at which position they've been
        Map<Integer, SolutionGene<ST, PT>> previousGenes = new HashMap<>();

        int counter = 0;
        while (counter++ < getMutationsPerSolution() && availableIndex.size() > 0) {
            int index = availableIndex.remove(getR().nextInt(availableIndex.size()));
            SolutionGene<ST, PT> gene = solution.getSolutionGenes().get(index);
            previousGenes.put(index, new SolutionGene<>(gene.getGene(), gene.getProblemGenes()));
            solution.getSolutionGenes().set(index, createGeneByMutation(gene));
        }

        getEvaluator().evaluateQuality(solution);

        //rollback happens here
        if (previousQuality <= solution.getQuality()) {
            solution.setQuality(previousQuality);
            solution.setCachets(oldCachets);
            //set the old genes back to their correct position
            for (Map.Entry<Integer, SolutionGene<ST, PT>> entry : previousGenes.entrySet()) {
                solution.getSolutionGenes().set(entry.getKey(), entry.getValue());
            }
        }

        return solution;
    }
}
