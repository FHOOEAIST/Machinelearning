package science.aist.machinelearning.core.fitness;

import science.aist.machinelearning.core.Solution;

/**
 * A CachetEvaluator determines a single quality characteristic on a Solution and will be used by the {@link Evaluator}.
 * It needs to be implemented specifically for the needs of a given machine learning problem
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface CachetEvaluator<GT, PT> {

    /**
     * Evaluates a solutions quality. The closer to 0 a mapping is the better it is. Adds a {} to a Solution that
     * determines the quality of a single constraint.
     *
     * @param solution to be evaluated
     * @return quality between 0 (best) and infinite
     */
    double evaluateQuality(Solution<GT, PT> solution);

    /**
     * Defines the name of this cachet, for use in the Evaluator identity. All Cachets generated by this evaluator WILL
     * receive this as cachet.setName() Note that the name should be universally unique (so a version number in this
     * does make sense!)
     *
     * @return name
     */
    String getName();
}
