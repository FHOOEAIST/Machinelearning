package science.aist.machinelearning.algorithm.cmaes;

/**
 * This is an interface that all problems that shall be solved by {@link CovarianceMatrixAdaptionEvolutionStrategy} MUST
 * implement
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface FloatValueProblem {

    int getVariableCount();

}
