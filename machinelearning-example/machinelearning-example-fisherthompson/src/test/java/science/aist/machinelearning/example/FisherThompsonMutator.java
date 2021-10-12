/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example;

import science.aist.machinelearning.algorithm.mutation.RollbackRandomNGenesMutator;
import science.aist.machinelearning.core.SolutionGene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mutates solution by swapping to values.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class FisherThompsonMutator extends RollbackRandomNGenesMutator<List<Integer>, Integer> {

    public static int runs = 0;
    private final Random r = new Random();

    @Override
    protected SolutionGene<List<Integer>, Integer> createGeneByMutation(SolutionGene<List<Integer>, Integer> gene) {

        runs++;

        SolutionGene<List<Integer>, Integer> mutation = new SolutionGene<>();
        mutation.setProblemGenes(gene.getProblemGenes());

        List<Integer> mutationGene = new ArrayList<>(gene.getGene());

        int index1 = r.nextInt(mutationGene.size());
        int index2 = r.nextInt(mutationGene.size());

        int cup = mutationGene.get(index1);
        mutationGene.set(index1, mutationGene.get(index2));
        mutationGene.set(index2, cup);

        mutation.setGene(mutationGene);

        return mutation;
    }
}
