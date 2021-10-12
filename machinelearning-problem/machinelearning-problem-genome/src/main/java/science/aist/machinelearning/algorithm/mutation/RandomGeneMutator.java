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

/**
 * This class is specific to the problem instance of genome, and only needs to be applied to the specific algorithm GA
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class RandomGeneMutator extends RandomNGenesMutator<Element[], Element[]> {

    protected SolutionGene<Element[], Element[]> createGeneByMutation(SolutionGene<Element[], Element[]> gene) {

        //create a new SolutionGene with the previous data
        //mutate it afterwards
        SolutionGene<Element[], Element[]> mutated = new SolutionGene<>();

        if (gene != null && gene.getProblemGenes() != null && gene.getProblemGenes().size() > 0 && gene.getGene() != null) {
            mutated.setProblemGenes(gene.getProblemGenes());
            Element[] mutatedElement = new Element[gene.getGene().length];
            for (int i = 0; i < gene.getGene().length; i++) {
                mutatedElement[i] = new Element(gene.getGene()[i].getValue());
            }
            mutatedElement[getR().nextInt(gene.getGene().length)].setValue(
                    gene.getProblemGenes().get(0).getGene()
                            [getR().nextInt(gene.getProblemGenes().get(0).getGene().length)].getValue()
            );
            mutated.setGene(mutatedElement);
        }

        return mutated;
    }

}
