/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ga.Crossover;
import science.aist.machinelearning.algorithm.ga.GeneticAlgorithm;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.ils.IterativeLocalSearchAlgorithm;
import science.aist.machinelearning.algorithm.localsearch.LocalSearch;
import science.aist.machinelearning.algorithm.mutation.AmalgamOffsetMutator;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.*;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.logging.LoggingConf;
import science.aist.machinelearning.core.mapping.NToOneSolutionCreator;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;
import science.aist.machinelearning.problem.autooptimization.AmalgamProblem;
import science.aist.machinelearning.problem.autooptimization.fitness.AmalgamEvaluationCachet;
import science.aist.machinelearning.problem.autooptimization.fitness.AmalgamGenerationCachet;
import science.aist.machinelearning.problem.autooptimization.mapping.AmalgamGeneCreator;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;

import java.util.*;

/**
 * UnitTestClass to optimise algorithms for the amalgamAlgorithm.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamOptimisationTest {

    private final Random r = new Random();
    private final int problemSize = 10;

    //Settings for the ProblemLocalSearch
    private final IterativeLocalSearchAlgorithm<Element[], Element[]> problemIterativeLocalSearchAlgorithm = new IterativeLocalSearchAlgorithm<>();
    private final LocalSearch<Element[], Element[]> problemLocalSearch = new LocalSearch<>();

    //Settings for the ProblemGeneticAlgorithm
    private final GeneticAlgorithm<Element[], Element[]> problemGeneticAlgorithm = new GeneticAlgorithm<>();
    private final TournamentSelector<Element[], Element[]> problemTournamentSelector = new TournamentSelector<>();

    //Settings used by the problem algorithms
    private final RandomGeneMutator mutator = new RandomGeneMutator();
    private final SolutionCreator solutionCreator = new OneToOneSolutionCreator<>();
    private final RandomGeneCreator geneCreator = new RandomGeneCreator();
    private final GenericEvaluatorImpl<Element[], Element[]> evaluator = new GenericEvaluatorImpl<>();
    private final ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

    //Settings for the local search used for finding a good amalgam mapping
    private final IterativeLocalSearchAlgorithm<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> iterativeLocalSearchAlgorithm = new IterativeLocalSearchAlgorithm<>();
    private final LocalSearch<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> localSearch = new LocalSearch<>();

    //Settings for the genetic search used for finding a good amalgam
    private final GeneticAlgorithm<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> geneticAlgorithm = new GeneticAlgorithm<>();
    private final UniformCrossover crossover = new UniformCrossover();
    private final TournamentSelector<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> tournamentSelector = new TournamentSelector<>();

    //settings used for the mapping algorithms
    private final AmalgamOffsetMutator<Element[], Element[]> amalgamMutator = new AmalgamOffsetMutator();
    private final GenericEvaluatorImpl<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> amalgamEvaluator = new GenericEvaluatorImpl<>();
    private final SolutionCreator<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> amalgamCreator = new NToOneSolutionCreator<>();
    private final AmalgamGeneCreator<Element[], Element[]> amalgamGeneCreator = new AmalgamGeneCreator();
    private final Crossover<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> amalgamCrossover = new UniformCrossover<>();
    private final AmalgamEvaluationCachet<Element[], Element[]> evaluationCachet = new AmalgamEvaluationCachet();
    private final AmalgamGenerationCachet<Element[], Element[]> generationCachet = new AmalgamGenerationCachet();
    private final Problem<AmalgamProblem<Element[], Element[]>> problem = new Problem<>();

    @BeforeClass
    public void setUp() {

        LoggingConf.setLoggingToRootLevel();

        // configure mutator
        mutator.setEvaluator(evaluator);
        mutator.setMutationsPerSolution(3);

        // configure selector
        tournamentSelector.setTournamentSize(10);

        // configure evaluator
        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(geneCreator);

        Map<CachetEvaluator<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>>, Double> amalgamCachets = new HashMap<>();
        amalgamCachets.put(evaluationCachet, 1.0);
        amalgamCachets.put(generationCachet, 0.1);
        amalgamEvaluator.setCachetEvaluators(amalgamCachets);

        amalgamCreator.setGeneCreator(amalgamGeneCreator);

        //configure local search for problem
        problemLocalSearch.setMaximumGenerations(10);
        problemLocalSearch.setMutator(mutator);
        problemLocalSearch.setEvaluator(evaluator);

        // configure amalgam mutator
        amalgamMutator.setEvaluator(amalgamEvaluator);
        amalgamMutator.setMutationsPerSolution(3);

        // configure local search for amalgam
        solutionCreator.setGeneCreator(geneCreator);

        localSearch.setMaximumGenerations(10);
        localSearch.setMutator(amalgamMutator);
        localSearch.setEvaluator(amalgamEvaluator);

        iterativeLocalSearchAlgorithm.setMaximumGenerations(100);
        iterativeLocalSearchAlgorithm.setEvaluator(amalgamEvaluator);
        iterativeLocalSearchAlgorithm.setSolutionCreator(amalgamCreator);
        iterativeLocalSearchAlgorithm.setMutator(amalgamMutator);
        iterativeLocalSearchAlgorithm.setSearchAlgorithm(localSearch);

        // configure genetic alg for amalgam search
        geneticAlgorithm.setElites(1);
        geneticAlgorithm.setMaximumGenerations(50);
        geneticAlgorithm.setMutationProbability(0.05);
        geneticAlgorithm.setPopulationSize(50);

        geneticAlgorithm.setEvaluator(amalgamEvaluator);
        geneticAlgorithm.setSolutionCreator(amalgamCreator);
        geneticAlgorithm.setCrossover(amalgamCrossover);
        geneticAlgorithm.setGenMutator(amalgamMutator);
        geneticAlgorithm.setSelector(tournamentSelector);

        //create ProblemGenes
        AmalgamProblem<Element[], Element[]> amalgamProblem1 = new AmalgamProblem<>(problemIterativeLocalSearchAlgorithm, new HashMap<>());
        amalgamProblem1.getOptions().put("evaluator", new Descriptor<>(evaluator));
        amalgamProblem1.getOptions().put("solutionCreator", new Descriptor<>(solutionCreator));
        amalgamProblem1.getOptions().put("mutator", new Descriptor(mutator));
        amalgamProblem1.getOptions().put("searchAlgorithm", new Descriptor(problemLocalSearch));
        amalgamProblem1.getOptions().put("maximumGenerations", new MinMaxDescriptor<>(10, 100));

        AmalgamProblem<Element[], Element[]> amalgamProblem2 = new AmalgamProblem<>(problemGeneticAlgorithm, new HashMap<>());
        amalgamProblem2.getOptions().put("evaluator", new Descriptor<>(evaluator));
        amalgamProblem2.getOptions().put("solutionCreator", new Descriptor<>(solutionCreator));
        amalgamProblem2.getOptions().put("crossover", new Descriptor(crossover));
        amalgamProblem2.getOptions().put("mutator", new Descriptor(mutator));
        amalgamProblem2.getOptions().put("mutationProbability", new MinMaxDescriptor<>(0.0, 1.0));
        amalgamProblem2.getOptions().put("selector", new Descriptor<>(problemTournamentSelector));
        amalgamProblem2.getOptions().put("populationSize", new MinMaxDescriptor<>(10, 100));
        amalgamProblem2.getOptions().put("elites", new MinMaxDescriptor<>(1, 10));
        amalgamProblem2.getOptions().put("maximumGenerations", new MinMaxDescriptor<>(10, 100));

        problem.getProblemGenes().add(new ProblemGene<>(amalgamProblem1));
        problem.getProblemGenes().add(new ProblemGene<>(amalgamProblem2));

        Element[] geneProblemVal = new Element[problemSize];
        for (int i = 0; i < problemSize; i++) {
            if (i >= problemSize * 3 / 4) {
                geneProblemVal[i] = new Element('T');
            } else if (i >= problemSize / 2) {
                geneProblemVal[i] = new Element('G');
            } else if (i >= problemSize / 4) {
                geneProblemVal[i] = new Element('C');
            } else {
                geneProblemVal[i] = new Element('A');
            }
        }

        StringBuilder targetSequence = new StringBuilder();
        for (int i = 0; i < problemSize; i++) {
            int next = r.nextInt(4);
            if (next == 0) {
                targetSequence.append("T");
            } else if (next == 1) {
                targetSequence.append("G");
            } else if (next == 2) {
                targetSequence.append("C");
            } else {
                targetSequence.append("A");
            }
        }

        elementEqualityCachet.setTargetSequence(targetSequence.toString());

        List<ProblemGene<Element[]>> geneProblems = new ArrayList<>();
        geneProblems.add(new ProblemGene<>(geneProblemVal));

        Problem<Element[]> geneProblem = new Problem(geneProblems);

        evaluationCachet.setProblem(geneProblem);
    }

    @Test
    public void amalgamOptimisationLocal() {
        //given

        //when
        Solution<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> s = iterativeLocalSearchAlgorithm.solve(problem);

        //then
        for (SolutionGene<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> gene : s.getSolutionGenes()) {
            Map<String, Descriptor> options = gene.getGene().getOptions();
            Assert.assertTrue(options.get("maximumGenerations") == null || ((Integer) options.get("maximumGenerations").getValue() >= 10 && (Integer) options.get("maximumGenerations").getValue() <= 100));
            Assert.assertTrue(options.get("populationSize") == null || ((Integer) options.get("populationSize").getValue() >= 10 && (Integer) options.get("populationSize").getValue() <= 100));
            Assert.assertTrue(options.get("elites") == null || ((Integer) options.get("elites").getValue() >= 1 && (Integer) options.get("elites").getValue() <= 10));
        }
    }

    @Test
    public void amalgamOptimisationGenetic() {
        //given

        //when
        Solution<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> s = geneticAlgorithm.solve(problem);

        //then
        for (SolutionGene<Algorithm<Element[], Element[]>, AmalgamProblem<Element[], Element[]>> gene : s.getSolutionGenes()) {
            Map<String, Descriptor> options = gene.getGene().getOptions();
            Assert.assertTrue(options.get("maximumGenerations") == null || ((Integer) options.get("maximumGenerations").getValue() >= 10 && (Integer) options.get("maximumGenerations").getValue() <= 100));
            Assert.assertTrue(options.get("populationSize") == null || ((Integer) options.get("populationSize").getValue() >= 10 && (Integer) options.get("populationSize").getValue() <= 100));
            Assert.assertTrue(options.get("elites") == null || ((Integer) options.get("elites").getValue() >= 1 && (Integer) options.get("elites").getValue() <= 10));
        }
    }
}
