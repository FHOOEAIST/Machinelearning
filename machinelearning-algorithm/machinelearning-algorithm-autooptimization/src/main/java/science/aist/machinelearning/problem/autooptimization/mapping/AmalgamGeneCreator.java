/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.autooptimization.mapping;

import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.mapping.GeneCreator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.ListDescriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;
import science.aist.machinelearning.problem.autooptimization.AmalgamProblem;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;

/**
 * Creates an algorithm with random values. Settings like the evaluator, mapping creator and mutator are taken by a
 * template algorithm, which is given in the problem.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamGeneCreator<GT, PT> implements GeneCreator<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> {

    private final Random r = new Random();

    @Override
    public Algorithm<GT, PT> createGene(ProblemGene<AmalgamProblem<GT, PT>> problem) {

        //pick a random algorithm
        Algorithm algorithm = null;

        try {
            //create a new instance of the chosen algorithm
            algorithm = problem.getGene().getAlgorithm().getClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (algorithm == null) {
            return null;
        }

        for (Map.Entry<String, Descriptor> entry : problem.getGene().getOptions().entrySet()) {
            //if value is not null, then the value is fixed
            if (entry.getValue().getValue() != null) {
                algorithm.setOption(entry.getKey(), entry.getValue());
            }
            //if it is null, then we have either a MinMaxDescriptor or ListDescriptor
            else {
                //check if its a MinMaxDescriptor
                if (entry.getValue() instanceof MinMaxDescriptor) {
                    MinMaxDescriptor descriptor = (MinMaxDescriptor) entry.getValue();

                    //check which type we need for calculating the random value between min and max.
                    if (descriptor.getMin().getClass() == Integer.class) {
                        MinMaxDescriptor<Integer> casted = (MinMaxDescriptor<Integer>) descriptor;
                        algorithm.setOption(entry.getKey(), new Descriptor(r.nextInt(casted.getMax() - casted.getMin()) + casted.getMin()));
                    } else if (descriptor.getMin().getClass() == Double.class) {
                        MinMaxDescriptor<Double> casted = (MinMaxDescriptor<Double>) descriptor;
                        algorithm.setOption(entry.getKey(), new Descriptor(casted.getMin() + (casted.getMax() - casted.getMin()) * r.nextDouble()));
                    } else if (descriptor.getMin().getClass() == Float.class) {
                        MinMaxDescriptor<Float> casted = (MinMaxDescriptor<Float>) descriptor;
                        algorithm.setOption(entry.getKey(), new Descriptor(casted.getMin() + (casted.getMax() - casted.getMin()) * r.nextFloat()));
                    }
                }
                //if not, its a ListDescriptor
                else {
                    ListDescriptor descriptor = (ListDescriptor) entry.getValue();
                    //set a random value of the list
                    algorithm.setOption(entry.getKey(), new Descriptor(descriptor.getValueList().get(r.nextInt(descriptor.getValueList().size()))));
                }
            }

        }

        return algorithm;
    }
}
