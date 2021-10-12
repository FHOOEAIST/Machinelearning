/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mutation;


import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.problem.genome.Element;

import java.util.Random;

/**
 * Takes a random gene and mutates it by putting its complementary into the string.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ComplementaryGeneMutator extends RandomNGenesMutator<Element[], Element[]> {

    Random r = new Random();

    protected SolutionGene<Element[], Element[]> createGeneByMutation(SolutionGene<Element[], Element[]> gene) {
        SolutionGene<Element[], Element[]> mutated = new SolutionGene<>();
        if (gene != null && gene.getGene() != null && gene.getProblemGenes() != null) {

            int index = r.nextInt(gene.getGene().length);
            char c = gene.getGene()[index].getValue();

            switch (c) {
                case 'A':
                    c = 'T';
                    break;
                case 'T':
                    c = 'A';
                    break;
                case 'G':
                    c = 'C';
                    break;
                default:
                    c = 'G';
            }

            //create a new SolutionGene with the previous data
            //mutate it afterwards

            mutated.setProblemGenes(gene.getProblemGenes());
            Element[] mutatedElement = new Element[gene.getGene().length];
            for (int i = 0; i < gene.getGene().length; i++) {
                mutatedElement[i] = new Element(gene.getGene()[i].getValue());
            }
            mutatedElement[index].setValue(c);
            mutated.setGene(mutatedElement);
        }
        return mutated;
    }
}
