package science.aist.machinelearning.core.mapping;

import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;

/**
 * Transforms the problem from its problem space into the solution space
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface SolutionCreator<ST, PT> {

    /**
     * transforms a problem into a mapping.
     *
     * @param problem to be turned into a mapping
     * @return a mapping
     */
    Solution<ST, PT> createSolution(Problem<PT> problem);

    void setGeneCreator(GeneCreator<ST, PT> geneCreator);

}
