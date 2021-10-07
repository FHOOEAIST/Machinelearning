package science.aist.machinelearning.algorithm.ga;


import science.aist.machinelearning.core.Solution;

import java.util.List;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Selector<ST, PT> {

    /**
     * @param population the population
     * @return the solution
     */
    Solution<ST, PT> select(List<Solution<ST, PT>> population);
}
