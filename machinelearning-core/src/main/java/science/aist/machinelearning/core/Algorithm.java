package science.aist.machinelearning.core;

import science.aist.machinelearning.core.analytics.Analytics;

/**
 * Interface for machine learning algorithm that solves a problem It is based on the concept of Problem and Solution
 * spaces, where one or more Problem Genomes PT can be transformed into one or more Solution Genomes GT
 *
 * @param <ST> Solution Genome Type
 * @param <PT> Problem Genome Type
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Algorithm<ST, PT> extends Configurable {

    /**
     * Solves a problem and returns best found mapping
     *
     * @param problem to be solved
     * @return best mapping that was found with alg
     */
    Solution<ST, PT> solve(Problem<PT> problem);

    /**
     * Solves a problem and returns best found mapping. Starts search with the given mapping.
     *
     * @param problem      problem to be solved
     * @param bestSolution start search with this mapping
     * @return best mapping that was found with alg
     */
    Solution<ST, PT> solve(Problem<PT> problem, Solution<ST, PT> bestSolution);

    /**
     * gets the current analytics tool for the algorithm If an analytics tool is set the algorithm will automatically
     * log its parameters and all of its steps to the tool for scientific analysis.
     *
     * @return analytics or null
     */
    Analytics getAnalytics();

    /**
     * sets an analytics tool for the algorithm.
     *
     * @param analytics the analytics
     */
    void setAnalytics(Analytics analytics);
}
