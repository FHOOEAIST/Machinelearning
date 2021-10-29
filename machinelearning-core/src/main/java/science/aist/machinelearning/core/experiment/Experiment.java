/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import science.aist.machinelearning.core.*;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class for conducting machinelearning experiments.
 * <p>
 * The Experiment will attempt to conduct every single permutation of configuration options for the given amount of
 * repeats. IF you want to exclude specific permutations see {@link ExperimentGroup}
 *
 * @param <ST></ST> Solution Type
 * @param <PT></PT> Problem Type
 * @author Oliver Krauss
 * @since 1.0
 */
@NodeEntity
public class Experiment<ST, PT> {

    /**
     * Id generated by database
     */
    @Id
    private Long id;

    /**
     * How often every permutation is repeated
     */
    private int repeats = 5;

    /**
     * Genes for the problem -> Will be automatically encapsulated into Problem<PT> Can also be just a list of one PT
     */
    private SingleUnwrappingChoice<Problem<PT>> problems = new SingleUnwrappingChoice<>("problems");

    /**
     * Algorithms that will be used to solve the Problem generated out of problemGenes
     */
    private SingleUnwrappingChoice<Algorithm<ST, PT>> algorithms = new SingleUnwrappingChoice<>("algorithms");


    public Experiment() {
    }

    public Experiment(SingleUnwrappingChoice<Problem<PT>> problems, SingleUnwrappingChoice<Algorithm<ST, PT>> algorithms) {
        this.problems = problems;
        this.algorithms = algorithms;
    }

    public Experiment(int repeats, SingleUnwrappingChoice<Problem<PT>> problems, SingleUnwrappingChoice<Algorithm<ST, PT>> algorithms) {
        this(problems, algorithms);
        this.repeats = repeats;
    }


    /**
     * Helper function that takes the problem genes and creates a Configurable out of it. It will not prepare any
     * choices
     *
     * @param name    Human Readable name given to problem
     * @param problem Single Gene To be turned into a Choice. If problem does not extend Configurable it will be a
     *                single choice
     * @param <O>     anything that is a Problem Gene
     * @return the choice
     */
    public static <O> Choice<Problem<O>> createProblemChoices(String name, O problem) {
        return createProblemChoices(name, problem, false, false);
    }

    /**
     * Helper function that takes the problem genes and creates a Configurable out of it.
     *
     * @param name                   Human Readable name given to problem
     * @param problem                Single Gene To be turned into a Choice. If problem does not extend Configurable it
     *                               will be a single choice
     * @param prepareChoices         if true all choices will be prepared with as many choices as possible. If false
     *                               everything will be set to FixedChoice
     * @param makeValuesExchangeable if true for any complex configuration (incl. Configurable) a SingleUnwrappingChoice
     *                               is used to allow switching of classes (ex. multiple Mutators)
     * @param <O>                    anything that is a Problem Gene
     * @return the choice
     */
    public static <O> Choice<Problem<O>> createProblemChoices(String name, O problem, boolean prepareChoices, boolean makeValuesExchangeable) {
        List<O> list = new ArrayList<>();
        list.add(problem);
        return createProblemChoices(name, list, prepareChoices, makeValuesExchangeable);
    }

    /**
     * Helper function that takes the problem genes and creates a Configurable out of it. It will not prepare any
     * options
     *
     * @param name    Human Readable name given to problem
     * @param problem To be turned into a Choice. If problem does not extend Configurable it will be a single choice
     * @param <O>     anything that is a Problem Gene
     * @return the choice
     */
    public static <O> Choice<Problem<O>> createProblemChoices(String name, List<O> problem) {
        return createProblemChoices(name, problem, false, false);
    }

    /**
     * Helper function that takes the problem genes and creates a Configurable out of it.
     *
     * @param name                   Human Readable name given to problem
     * @param problem                To be turned into a Choice. If problem does not extend Configurable it will be a
     *                               single choice
     * @param prepareChoices         if true all choices will be prepared with as many choices as possible. If false
     *                               everything will be set to FixedChoice
     * @param makeValuesExchangeable if true for any complex configuration (incl. Configurable) a SingleUnwrappingChoice
     *                               is used to allow switching of classes (ex. multiple Mutators)
     * @param <O>                    anything that is a Problem Gene
     * @return the choice
     */
    public static <O> Choice<Problem<O>> createProblemChoices(String name, List<O> problem, boolean prepareChoices, boolean makeValuesExchangeable) {
        if (problem == null || problem.isEmpty()) {
            return null;
        }
        if (!(problem.get(0) instanceof Configurable)) {
            // just create a static choice
            List<ProblemGene<O>> problemGenes = new ArrayList<>();
            problem.forEach(x -> problemGenes.add(new ProblemGene<>(x)));
            Problem<O> prob = new Problem<>(problemGenes);
            return new FixedChoice<>(name, prob);
        }

        // turn genes into configurable
        ProblemChoice<O> problemChoice = new ProblemChoice<>(name, problem.get(0).getClass());
        int i = 0;
        for (O gene : problem) {
            problemChoice.addConfigurationOption(createConfigurationChoices("[" + i++ + "]", (Configurable) gene, prepareChoices, makeValuesExchangeable));
        }
        return problemChoice;
    }


    /**
     * Helper function that takes any configurable (Ex. an Algorithm) and creates the configuration hierarchy
     *
     * @param name Name that is given to the configurable (to find it again later)
     * @param c    object that shall be used in experiment
     * @param <O>  anything that extends Configurable
     * @return Choice to be used in Experiment -&gt; All non-Configurables will be returned as "FixedChoice"
     */
    public static <O extends Configurable> ConfigurableChoice<O> createConfigurationChoices(String name, O c) {
        return createConfigurationChoices(name, c, false, false);
    }

    /**
     * Helper function that takes any configurable (Ex. an Algorithm) and creates the configuration hierarchy
     *
     * @param c                      object that shall be used in experiment
     * @param <O>                    anything that extends Configurable
     * @param prepareChoices         if true all choices will be prepared with as many choices as possible. If false
     *                               everything will be set to FixedChoice
     * @param makeValuesExchangeable if true for any complex configuration (incl. Configurable) a SingleUnwrappingChoice
     *                               is used to allow switching of classes (ex. multiple Mutators)
     * @return Choice to be used in Experiment
     */
    public static <O extends Configurable> ConfigurableChoice<O> createConfigurationChoices(String name, O c, boolean prepareChoices, boolean makeValuesExchangeable) {
        ConfigurableChoice<O> result = new ConfigurableChoice<>(name, c.getClass());

        c.getOptions().forEach((key, value) -> {
            Choice subChoice;
            if (value.getValue() instanceof Configurable) {
                ConfigurableChoice subConfigurableChoice = createConfigurationChoices(key, (Configurable) value.getValue(), prepareChoices, makeValuesExchangeable);
                if (makeValuesExchangeable) {
                    subChoice = new WrappingChoice(subConfigurableChoice);
                } else {
                    subChoice = new FixedChoice<>(key, value);
                }
            } else if (prepareChoices && value.getValue() != null && value.getClass().equals(Descriptor.class)) {
                // we don't automate list or minmaxdescriptors as they are too specific
                switch (value.getValue().getClass().getName()) {
                    case "java.lang.Integer":
                        subChoice = createRangeChoice(key, new Object[]{1, 3, 5});
                        break;
                    case "java.lang.Double":
                        subChoice = createRangeChoice(key, new Object[]{0.1, 0.3, 0.5});
                        break;
                    case "java.lang.Long":
                        subChoice = createRangeChoice(key, new Object[]{1L, 3L, 5L,});
                        break;
                    default:
                        // we don't know what to do with unknowns, so just create defaults
                        if (makeValuesExchangeable) {
                            subChoice = new SingleChoiceConfig(key);
                            ((SingleChoiceConfig) subChoice).addChoice(value);
                        } else {
                            subChoice = new FixedChoice<>(key, value);
                        }
                }
            } else {
                if (makeValuesExchangeable) {
                    subChoice = new SingleChoiceConfig(key);
                    ((SingleChoiceConfig) subChoice).addChoice(value);
                } else {
                    subChoice = new FixedChoice<>(key, value);
                }
            }

            result.addConfigurationOption(subChoice);
        });

        return result;
    }

    private static Choice<Descriptor> createRangeChoice(String name, Object[] values) {
        SingleChoiceConfig<Descriptor> choice = new SingleChoiceConfig<>(name);
        for (Object value : values) {
            choice.addChoice(new Descriptor(value));
        }
        return choice;
    }

    /**
     * Runs all of the algorithms with the given problem genes
     *
     * @return result of all runs
     */
    public ExperimentResult<ST, PT> conductExperiment() {

        // run the experiment
        ExperimentResult<ST, PT> result = new ExperimentResult<>();

        while (problems.hasNext()) {
            problems.next();
            ExperimentIdentifier problemIdentifier = problems.getCurrentIdentifier();
            Problem<PT> problem = problems.current();
            algorithms.reset();
            while (algorithms.hasNext()) {
                algorithms.next();
                ExperimentIdentifier algorithmIdentifier = algorithms.getCurrentIdentifier();
                Map<String, Object> identifiers = new HashMap<>(problemIdentifier.getIdentifier());
                identifiers.putAll(algorithmIdentifier.getIdentifier());
                ExperimentIdentifier identifier = new ExperimentIdentifier(identifiers);
                for (int i = 0; i < repeats; i++) {
                    Algorithm<ST, PT> algorithm = algorithms.current();
                    Solution<ST, PT> solution = algorithm.solve(problem);
                    result.add(identifier, solution);
                }
            }
        }

        return result;
    }

    public void addProblem(Choice<Problem<PT>> problem) {
        this.problems.addChoice(problem);
    }

    public void addAlgorithm(ConfigurableChoice<Algorithm<ST, PT>> algorithm) {
        this.algorithms.addChoice(algorithm);
    }

    public Long getId() {
        return id;
    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public SingleUnwrappingChoice<Problem<PT>> getProblems() {
        return problems;
    }

    public void setProblems(SingleUnwrappingChoice<Problem<PT>> problems) {
        this.problems = problems;
    }

    public SingleUnwrappingChoice<Algorithm<ST, PT>> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(SingleUnwrappingChoice<Algorithm<ST, PT>> algorithms) {
        this.algorithms = algorithms;
    }

}