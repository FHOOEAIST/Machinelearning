/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.ga;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.algorithm.mutation.Mutator;
import science.aist.machinelearning.core.AbstractAlgorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * Standard genetic algorithm implementation
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class GeneticAlgorithm<GT, PT> extends AbstractAlgorithm<GT, PT> {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(GeneticAlgorithm.class);
    /**
     * Random for random number generation
     */
    private final Random random = new Random();
    /**
     * Best currently known solution
     */
    protected Solution<GT, PT> bestSolution;
    /**
     * Crossover strategy for building children out of parents
     */
    private Crossover<GT, PT> crossover;
    /**
     * Mutation strategy for mutating new children
     */
    private Mutator<GT, PT> mutator;
    /**
     * Double between 0 and 1 that determines the mutation probability
     */
    private double mutationProbability;
    /**
     * Selection strategy for selecting solutions for breeing
     */
    private Selector<GT, PT> selector;
    /**
     * Size of mapping population in each generation
     */
    private int populationSize;
    /**
     * Number of elites in generation that will be selected for next generation no matter what
     */
    private int elites;
    /**
     * Termination Criteria on how many generations may be evaluated
     */
    private int maximumGenerations;
    /**
     * Number of current generation, increasing with each algorithm step
     */
    private int currentGeneration;
    /**
     * Current population in the GA context
     */
    private List<Solution<GT, PT>> population = new ArrayList<>();
    /**
     * if the initializeLog function has already been called
     */
    private boolean logInitialized = false;
    /**
     * if the finalizeLog function has already been called
     */
    private boolean logFinalized = false;

    @Override
    public Solution<GT, PT> solve(Problem<PT> problem) {
        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().isEmpty()) {
            return null;
        }

        initializeLog(problem);
        bestSolution = solutionCreator.createSolution(problem);
        evaluator.evaluateQuality(bestSolution);

        return solve(problem, bestSolution);
    }

    @Override
    public Solution<GT, PT> solve(Problem<PT> problem, Solution<GT, PT> givenSolution) {
        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().isEmpty() || givenSolution == null) {
            return null;
        }

        initializeLog(problem);
        // make sure that the best solution was evaluated
        if (givenSolution.getCachets().size() == 0) {
            getEvaluator().evaluateQuality(givenSolution);
        }
        this.bestSolution = givenSolution;

        // if the first step was not as of yet executed -> do it now
        if (currentGeneration == 0) {
            fillPopulation(problem);
            analyticsStep(bestSolution, population);
            currentGeneration++;
        }

        // run all steps
        while (currentGeneration < maximumGenerations) {
            bestSolution = nextGeneration(problem);
        }
        finalizeLog(problem);

        return bestSolution;
    }

    /**
     * Helper function that resets the state of the genetic algorithm as if it never did anything NOTE: This does not
     * currently reset the analytics. Thus tests will look very weird
     */
    protected void reset() {
        bestSolution = null;
        population.clear();
        currentGeneration = 0;
        logInitialized = false;
        logFinalized = false;
    }

    /**
     * Creates the log headers, and logs the current algorithm configuration
     *
     * @param problem that is being solved in this log
     */
    protected void initializeLog(Problem<PT> problem) {
        if (analytics != null && !logInitialized) {
            logInitialized = true;
            analytics.startAnalytics();
            analytics.logParam("problemSize", problem.getProblemSize());
            analytics.logParam("crossover", crossover != null ? crossover.getClass().getName() : "no crossover");
            analytics.logParam("mutator", mutator.getClass().getName());
            analytics.logParam("selector", selector.getClass().getName());
            analytics.logParam("creator", solutionCreator.getClass().getName());
            analytics.logParam("mutationProbability", String.valueOf(mutationProbability));
            analytics.logParam("populationSize", String.valueOf(populationSize));
            analytics.logParam("elites", String.valueOf(elites));
            analytics.logParam("maximumGenerations", String.valueOf(maximumGenerations));
            List<String> headers = new ArrayList<>();
            headers.add("best quality");
            headers.add("worst quality");
            headers.add("average quality");
            analytics.logAlgorithmStepHeaders(headers);
            analytics.logProblem(problem);
        }
    }

    /**
     * Helper function that is called after the last generation was created
     */
    protected void finalizeLog(Problem<PT> problem) {
        if (logFinalized) {
            return;
        }

        if (analytics != null) {
            logSolution(bestSolution);
            analytics.finishAnalytics();
        }
        logFinalized = true;
    }

    /**
     * Helper function that fills the population if it is not full
     *
     * @param problem the problem
     */
    private void fillPopulation(Problem<PT> problem) {
        // generate initial population
        //logger.debug("Generating initial population");
        for (int i = population.size(); i < populationSize; i++) {
            Solution<GT, PT> s = solutionCreator.createSolution(problem);
            // evaluate population fitness
            bestSolution = bestQuality(s, bestSolution);
            population.add(s);
        }
    }

    /**
     * Calculates one single generation until all generations are full If all generations have been calculated, it
     * returns null
     *
     * @return best individual of the next generation, or null
     */
    public Solution<GT, PT> nextGeneration(Problem<PT> problem) {
        if (currentGeneration >= maximumGenerations) {
            finalizeLog(problem);
            return null;
        } else if (currentGeneration == 0) {
            initializeLog(problem);
        }
        currentGeneration++;

        // make sure that the best solution was evaluated
        if (bestSolution.getCachets().isEmpty()) {
            getEvaluator().evaluateQuality(bestSolution);
        }

        // check if the population is full (may not be if someone removed an individual, or this is the first iteration)
        fillPopulation(problem);

        // recheck the entire population for intruders that have not yet been assessed for quality:
        population.forEach(x -> {
            if (x.getCachets().size() == 0) {
                bestSolution = bestQuality(x, bestSolution);
            }
        });

        // evaluate generation
        //logger.debug("Evaluating generations");
        List<Solution<GT, PT>> childPopulation = new ArrayList<>();
        // take elites into next generation
        if (elites > 0) {
            population.sort(Comparator.comparingDouble(Solution::getQuality));
            int size = Math.min(elites, populationSize);
            childPopulation.addAll(population.subList(0, size));
        }

        // for all remaining spots in new population breed new
        while (childPopulation.size() < populationSize) {
            // breed
            Solution<GT, PT> s;
            if (crossover != null && mutationProbability < random.nextDouble()) {
                s = crossover.breed(population, selector);
            } else {
                s = selector.select(population);
                s = mutator.mutate(s);
            }

            bestSolution = bestQuality(s, bestSolution);
            childPopulation.add(s);
        }
        population = childPopulation;
        analyticsStep(bestSolution, population);


        return bestSolution;
    }

    /**
     * Logs one single step of the algorithm, in case of GA this means statistics about the current generation
     *
     * @param givenSolution currently best known solution
     * @param population    current populatino
     */
    private void analyticsStep(Solution<GT, PT> givenSolution, List<Solution<GT, PT>> population) {
        if (analytics != null) {
            Solution<GT, PT> worstSolution = population.stream().max(Comparator.comparingDouble(Solution::getQuality)).orElse(givenSolution);
            double averageQuality = population.stream().mapToDouble(Solution::getQuality).average().orElse(0.0);
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(givenSolution.getQuality()));
            values.add(String.valueOf(worstSolution.getQuality()));
            values.add(String.valueOf(averageQuality));
            analytics.logAlgorithmStep(values);
        }
    }

    /**
     * Calculate the bestQuality of the two solutions. Returns the mapping with the better quality.
     *
     * @param solution      mapping to compare
     * @param givenSolution previous best mapping to compare
     * @return new best mapping
     */
    public Solution<GT, PT> bestQuality(Solution<GT, PT> solution, Solution<GT, PT> givenSolution) {
        evaluator.evaluateQuality(solution);

        if (solution.getQuality() < givenSolution.getQuality()) {
            bestSolution = solution;
        }

        return bestSolution;
    }

    /**
     * Log the given mapping with analytics.
     *
     * @param solution mapping to log
     */
    public void logSolution(Solution<GT, PT> solution) {
        getAnalytics().logSolution(solution);
    }

    /**
     * Setter for dependency injection
     *
     * @param mutator // if not set algorithm will not mutate solutions
     */
    public void setGenMutator(Mutator<GT, PT> mutator) {
        this.mutator = mutator;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Setter for configuration
     *
     * @param populationSize the population size
     */
    @Required
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getMaximumGenerations() {
        return maximumGenerations;
    }

    /**
     * Setter for configuration
     *
     * @param maximumGenerations the maximum generations
     */
    @Required
    public void setMaximumGenerations(int maximumGenerations) {
        this.maximumGenerations = maximumGenerations;
    }

    public Crossover<GT, PT> getCrossover() {
        return crossover;
    }

    /**
     * Setter for dependency injection
     *
     * @param crossover the crossover
     */
    public void setCrossover(Crossover<GT, PT> crossover) {
        this.crossover = crossover;
    }

    public Mutator<GT, PT> getMutator() {
        return mutator;
    }

    public Selector<GT, PT> getSelector() {
        return selector;
    }

    /**
     * Setter for dependency injection
     *
     * @param selector the selector
     */
    @Required
    public void setSelector(Selector<GT, PT> selector) {
        this.selector = selector;
    }

    public int getElites() {
        return elites;
    }

    /**
     * Setter for configuration
     *
     * @param elites if not set no elites will be kept
     */
    public void setElites(int elites) {
        this.elites = elites;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    /**
     * Setter for configuration
     *
     * @param mutationProbability the mutation probability
     */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /**
     * Returns a COPY of the current population. The state of this is: - Before first step - Empty - After each step -
     * The population that will be the PARENT of the next generation - After Execution - The lastly created population
     *
     * @return a copy of the current population
     */
    public List<Solution<GT, PT>> getPopulation() {
        return new ArrayList<>(population);
    }

    /**
     * Adds an inidivual to the population only if the population size was not yet exceeded
     *
     * @param individual to be added
     * @return if the individual was added
     */
    public boolean addIndividual(Solution<GT, PT> individual) {
        if (population.size() < populationSize) {
            population.add(individual);
            return true;
        }
        return false;
    }

    /**
     * Removes an individual from the population.
     *
     * @param individual to be removed
     * @return if the individual was removed
     */
    public boolean removeIndividual(Solution<GT, PT> individual) {
        if (population.contains(individual)) {
            population.remove(individual);
            return true;
        }
        return false;
    }

    @Override
    protected Map<String, Descriptor> getSpecificOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("crossover", new Descriptor<>(crossover));
        options.put("mutator", new Descriptor<>(mutator));
        options.put("mutationProbability", new Descriptor<>(mutationProbability));
        options.put("selector", new Descriptor<>(selector));
        options.put("populationSize", new Descriptor<>(populationSize));
        options.put("elites", new Descriptor<>(elites));
        options.put("maximumGenerations", new Descriptor<>(maximumGenerations));

        return options;
    }

    @Override
    protected boolean setSpecificOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "crossover":
                    setCrossover((Crossover<GT, PT>) descriptor.getValue());
                    break;
                case "mutator":
                    setGenMutator((Mutator<GT, PT>) descriptor.getValue());
                    break;
                case "mutationProbability":
                    setMutationProbability((Double) descriptor.getValue());
                    break;
                case "selector":
                    setSelector((Selector<GT, PT>) descriptor.getValue());
                    break;
                case "populationSize":
                    setPopulationSize((Integer) descriptor.getValue());
                    break;
                case "elites":
                    setElites((Integer) descriptor.getValue());
                    break;
                case "maximumGenerations":
                    setMaximumGenerations((Integer) descriptor.getValue());
                    break;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
