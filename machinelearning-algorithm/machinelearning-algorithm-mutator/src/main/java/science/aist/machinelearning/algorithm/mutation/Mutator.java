package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.core.Solution;

/**
 * Interface containing necessary methods for the mutation of solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface Mutator<ST, PT> {

    Solution<ST, PT> mutate(Solution<ST, PT> solution);
}
