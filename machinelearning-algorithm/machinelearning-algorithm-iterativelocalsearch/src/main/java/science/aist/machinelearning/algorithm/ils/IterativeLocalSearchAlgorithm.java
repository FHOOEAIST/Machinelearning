package science.aist.machinelearning.algorithm.ils;


import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.algorithm.mutation.Mutator;
import science.aist.machinelearning.core.AbstractAlgorithm;
import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * Implementation for a local search.
 * <p>
 * Creates a single mapping and creates a mutated copy from it. Will keep the better mapping and start mutating again.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class IterativeLocalSearchAlgorithm<GT, PT> extends AbstractAlgorithm<GT, PT> {

    private final Random r = new Random();
    /**
     * Mutation strategy for mutating new children
     */
    private Mutator<GT, PT> mutator;
    /**
     * Termination Criteria on how many generations may be evaluated
     */
    private int maximumGenerations;
    /**
     * search algorithm, that takes care of the underlying search
     */
    private Algorithm<GT, PT> searchAlgorithm;

    private void analyticsStep(Solution<GT, PT> bestSolution) {
        if (getAnalytics() != null) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(bestSolution.getQuality()));
            getAnalytics().logAlgorithmStep(values);
        }
    }

    @Override
    public Solution<GT, PT> solve(Problem<PT> problem) {

        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().size() == 0) {
            return null;
        }

        //start with a single mapping
        Solution<GT, PT> bestSolution = getSolutionCreator().createSolution(problem);
        getEvaluator().evaluateQuality(bestSolution);

        return solve(problem, bestSolution);
    }

    @Override
    public Solution<GT, PT> solve(Problem<PT> problem, Solution<GT, PT> bestSolution) {

        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().size() == 0 || bestSolution == null) {
            return null;
        }

        writeStartAnalyticsData(problem);

        for (int i = 0; i < getMaximumGenerations(); i++) {
            //Kick
            Solution<GT, PT> newSolution = mutator.mutate(bestSolution);

            //local search
            newSolution = getSearchAlgorithm().solve(problem, newSolution);
            newSolution.getCachets().clear();

            //check quality
            bestSolution = bestQuality(newSolution, bestSolution);

            analyticsStep(bestSolution);
        }

        writeEndAnalyticsData(problem, bestSolution);

        return bestSolution;
    }


    /**
     * Calculate the bestQuality of the two solutions. Returns the mapping with the better quality.
     *
     * @param solution     mapping to compare
     * @param bestSolution previous best mapping to compare
     * @return new best mapping
     */
    public Solution<GT, PT> bestQuality(Solution<GT, PT> solution, Solution<GT, PT> bestSolution) {
        getEvaluator().evaluateQuality(solution);

        if (solution.getQuality() < bestSolution.getQuality()) {
            bestSolution = solution;
        }

        return bestSolution;
    }

    /**
     * Writes the analyticsData for the begin of a new file.
     *
     * @param problem problemData for the analytics
     */
    private void writeStartAnalyticsData(Problem<PT> problem) {
        if (getAnalytics() != null) {
            getAnalytics().startAnalytics();
            getAnalytics().logParam("problemSize", problem.getProblemSize());
            getAnalytics().logParam("kickmutator", getMutator().getClass().getName());
            getAnalytics().logParam("searchAlgorithm", getSearchAlgorithm().getClass().getName());
            getAnalytics().logParam("creator", getSolutionCreator().getClass().getName());
            getAnalytics().logParam("maximumGenerations", String.valueOf(getMaximumGenerations()));
            List<String> headers = new ArrayList<>();
            headers.add("best quality");
            getAnalytics().logAlgorithmStepHeaders(headers);
        }
    }

    /**
     * Writes the analyticsData for the end of a new file.
     *
     * @param problem      problemData for the analytics
     * @param bestSolution result of the search algorithm
     */
    private void writeEndAnalyticsData(Problem<PT> problem, Solution<GT, PT> bestSolution) {
        if (getAnalytics() != null) {
            getAnalytics().logProblem(problem);
            logSolution(bestSolution);
            getAnalytics().finishAnalytics();
        }
    }

    /**
     * Log the given mapping with analytics.
     *
     * @param solution mapping to log
     */
    public void logSolution(Solution<GT, PT> solution) {
        getAnalytics().logSolution(solution);
    }

    public int getMaximumGenerations() {
        return maximumGenerations;
    }

    @Required
    public void setMaximumGenerations(int maximumGenerations) {
        this.maximumGenerations = maximumGenerations;
    }

    @Override
    protected Map<String, Descriptor> getSpecificOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("mutator", new Descriptor<>(mutator));
        options.put("maximumGenerations", new Descriptor<>(maximumGenerations));
        options.put("searchAlgorithm", new Descriptor<>(searchAlgorithm));

        return options;
    }

    @Override
    protected boolean setSpecificOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "mutator":
                    setMutator((Mutator<GT, PT>) descriptor.getValue());
                    break;
                case "maximumGenerations":
                    setMaximumGenerations((Integer) descriptor.getValue());
                    break;
                case "searchAlgorithm":
                    setSearchAlgorithm((Algorithm<GT, PT>) descriptor.getValue());
                    break;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Mutator<GT, PT> getMutator() {
        return mutator;
    }

    public void setMutator(Mutator<GT, PT> mutator) {
        this.mutator = mutator;
    }

    public Algorithm<GT, PT> getSearchAlgorithm() {
        return searchAlgorithm;
    }

    @Required
    public void setSearchAlgorithm(Algorithm<GT, PT> searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }
}

