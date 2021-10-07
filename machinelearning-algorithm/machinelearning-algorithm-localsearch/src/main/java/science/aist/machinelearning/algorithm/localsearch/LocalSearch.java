package science.aist.machinelearning.algorithm.localsearch;


import science.aist.machinelearning.algorithm.mutation.Mutator;
import science.aist.machinelearning.core.AbstractAlgorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.analytics.Analytics;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Iterative local search, that constantly mutates a mapping and takes the better mapping for further search.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class LocalSearch<ST, PT> extends AbstractAlgorithm<ST, PT> {

    protected Integer maximumGenerations;

    protected Mutator<ST, PT> mutator;

    protected Integer currentGeneration = 0;

    private void analyticsStep(Solution<ST, PT> bestSolution) {
        if (getAnalytics() != null) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(bestSolution.getQuality()));
            getAnalytics().logAlgorithmStep(values);
        }
    }

    @Override
    public Solution<ST, PT> solve(Problem<PT> problem) {
        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().size() == 0) {
            return null;
        }

        return solve(problem, getSolutionCreator().createSolution(problem));
    }

    @Override
    public Solution<ST, PT> solve(Problem<PT> problem, Solution<ST, PT> bestSolution) {
        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().size() == 0 || bestSolution == null) {
            return null;
        }

        for (int i = 0; i < getMaximumGenerations(); i++) {
            //Mutate
            bestSolution = mutator.mutate(bestSolution);
            analyticsStep(bestSolution);
        }

        return bestSolution;
    }

    @Override
    public Analytics getAnalytics() {
        return analytics;
    }

    @Override
    protected Map<String, Descriptor> getSpecificOptions() {

        Map<String, Descriptor> options = new HashMap<>();

        options.put("maximumGenerations", new Descriptor<>(maximumGenerations));
        options.put("mutator", new Descriptor<>(mutator));
        options.put("currentGeneration", new Descriptor(currentGeneration));

        return options;
    }

    @Override
    protected boolean setSpecificOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "maximumGenerations":
                    setMaximumGenerations((Integer) descriptor.getValue());
                    break;
                case "mutator":
                    setMutator((Mutator<ST, PT>) descriptor.getValue());
                    break;
                case "currentGeneration":
                    setCurrentGeneration((Integer) descriptor.getValue());
                    break;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Integer getMaximumGenerations() {
        return maximumGenerations;
    }

    public void setMaximumGenerations(Integer maximumGenerations) {
        this.maximumGenerations = maximumGenerations;
    }

    public Mutator<ST, PT> getMutator() {
        return mutator;
    }

    public void setMutator(Mutator<ST, PT> mutator) {
        this.mutator = mutator;
    }

    public Integer getCurrentGeneration() {
        return currentGeneration;
    }

    public void setCurrentGeneration(Integer currentGeneration) {
        this.currentGeneration = currentGeneration;
    }
}
