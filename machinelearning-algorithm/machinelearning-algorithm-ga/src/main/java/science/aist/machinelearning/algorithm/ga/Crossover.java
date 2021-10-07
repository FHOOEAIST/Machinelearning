package science.aist.machinelearning.algorithm.ga;

import science.aist.machinelearning.core.Solution;

import java.util.List;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Crossover<ST, PT> {

    Solution<ST, PT> breed(List<Solution<ST, PT>> population, Selector<ST, PT> selector);

}
