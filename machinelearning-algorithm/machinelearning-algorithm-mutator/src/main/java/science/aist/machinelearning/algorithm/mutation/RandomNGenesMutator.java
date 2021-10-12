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
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Abstract class for the implementation of mutators. Will mutate x genes of the solution, evaluate the solution and
 * return the better result (previous or mutated).
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class RandomNGenesMutator<ST, PT> extends EvaluatingMutator<ST, PT> {

    private final Random r = new Random();
    /**
     * Number of genes that should be mutated
     */
    private Integer mutationsPerSolution = 1;

    public Solution<ST, PT> mutate(Solution<ST, PT> solution) {
        if (solution == null) {
            return null;
        }

        Solution<ST, PT> mutation = new Solution<>();
        List<Integer> availableIndex = new ArrayList<>();
        int counter = 0;
        for (SolutionGene<ST, PT> gene : solution.getSolutionGenes()) {
            mutation.addGene(new SolutionGene<>(gene.getGene(), gene.getProblemGenes()));
            availableIndex.add(counter++);
        }

        counter = 0;
        while (counter++ < getMutationsPerSolution() && availableIndex.size() > 0) {
            int index = availableIndex.remove(r.nextInt(availableIndex.size()));
            mutation.getSolutionGenes().set(index, createGeneByMutation(mutation.getSolutionGenes().get(index)));
        }

        getEvaluator().evaluateQuality(mutation);

        return mutation.getQuality() < solution.getQuality() ? mutation : new Solution<>(solution);
    }

    protected abstract SolutionGene<ST, PT> createGeneByMutation(SolutionGene<ST, PT> gene);

    public Integer getMutationsPerSolution() {
        return mutationsPerSolution;
    }

    public void setMutationsPerSolution(Integer mutationsPerSolution) {
        this.mutationsPerSolution = mutationsPerSolution;
    }

    public Random getR() {
        return r;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = super.getOptions();
        options.put("mutationsPerSolution", new Descriptor<>(mutationsPerSolution));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("mutationsPerSolution")) {
                setMutationsPerSolution((Integer) descriptor.getValue());
            } else {
                super.setOption(name, descriptor);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
