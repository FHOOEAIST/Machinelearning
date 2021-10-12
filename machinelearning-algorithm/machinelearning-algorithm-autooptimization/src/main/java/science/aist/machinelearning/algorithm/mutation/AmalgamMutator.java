/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.ListDescriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;
import science.aist.machinelearning.problem.autooptimization.AmalgamProblem;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;

/**
 * Mutates a single value of the given algorithm by newly setting the option. Returns the mutated gene then.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamMutator<GT, PT> extends RollbackRandomNGenesMutator<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> {

    private final Random r = new Random();

    @Override
    protected SolutionGene<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> createGeneByMutation(SolutionGene<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> gene) {

        SolutionGene<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> mutation = new SolutionGene<>();
        mutation.setProblemGenes(gene.getProblemGenes());

        //pick a random algorithm
        Algorithm mutationGene = null;

        try {
            //create a new instance of the chosen algorithm
            mutationGene = gene.getGene().getClass().getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (mutationGene == null) {
            return null;
        }

        mutationGene.setOptions(gene.getGene().getOptions());

        //change algorithm by a bit

        for (Map.Entry<String, Descriptor> entry : gene.getProblemGenes().get(0).getGene().getOptions().entrySet()) {
            //if the value is null, then the underlying descriptor has to be a MinMax- or ListDescriptor that should be changed
            if (entry.getValue().getValue() == null) {
                //check if its a MinMaxDescriptor
                if (entry.getValue() instanceof MinMaxDescriptor) {
                    MinMaxDescriptor descriptor = (MinMaxDescriptor) entry.getValue();

                    //check which type we need for calculating the random value between min and max.
                    if (descriptor.getMin().getClass() == Integer.class) {
                        MinMaxDescriptor<Integer> casted = (MinMaxDescriptor<Integer>) descriptor;
                        mutationGene.setOption(entry.getKey(), new Descriptor(r.nextInt(casted.getMax() - casted.getMin()) + casted.getMin()));
                    } else if (descriptor.getMin().getClass() == Double.class) {
                        MinMaxDescriptor<Double> casted = (MinMaxDescriptor<Double>) descriptor;
                        mutationGene.setOption(entry.getKey(), new Descriptor(casted.getMin() + (casted.getMax() - casted.getMin()) * r.nextDouble()));
                    } else if (descriptor.getMin().getClass() == Float.class) {
                        MinMaxDescriptor<Float> casted = (MinMaxDescriptor<Float>) descriptor;
                        mutationGene.setOption(entry.getKey(), new Descriptor(casted.getMin() + (casted.getMax() - casted.getMin()) * r.nextFloat()));
                    }
                }
                //if not, its a ListDescriptor
                else {
                    ListDescriptor descriptor = (ListDescriptor) entry.getValue();
                    //set a random value of the list
                    mutationGene.setOption(entry.getKey(), new Descriptor(descriptor.getValueList().get(r.nextInt(descriptor.getValueList().size()))));
                }

                //we only want to mutate a single value, so break if we set options once using MinMax- or ListDescriptors
                break;
            }
        }

        mutation.setGene(mutationGene);

        return mutation;
    }
}
