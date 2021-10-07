package science.aist.machinelearning.core.fitness;

import science.aist.machinelearning.core.Solution;

import java.util.Map;

/**
 * Evaluates the quality of a Solution according to a fitness function The fitness function consists of several cachets
 * that are summed up
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Evaluator<ST, PT> {

    /**
     * Evaluates a solutions quality. The closer to 0 a mapping is the better it is.
     *
     * @param solution to be evaluated
     * @return quality in double
     */
    double evaluateQuality(Solution solution);

    /**
     * Returns the dictionary of cachets used as part of this evaluation function
     *
     * @return Map of cachet|importance
     */
    Map<CachetEvaluator<ST, PT>, Double> returnCachetDictionary();

    /**
     * Provides a string readable identity of this evaluator for the purpose of identifying it through runs It should
     * use the names of the cachet evaluators, and ideally return the actual mathematical function that is the fitness
     * function Ex. Cachet Accuracy-1.0 x 0.5 and Cachet Performance-1.2 x 0.5 identity = "Accuracy-1.0 * 0.5 +
     * Performance-1.2 * 0.5"
     *
     * @return identity of the evaluation function
     */
    String evaluationIdentity();

}
