package science.aist.machinelearning.algorithm.ga.crossover;

import science.aist.machinelearning.algorithm.ga.Crossover;
import science.aist.machinelearning.algorithm.ga.Selector;
import science.aist.machinelearning.core.Solution;

import java.util.List;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public abstract class AbstractCrossover<ST, PT> implements Crossover<ST, PT> {

    @Override
    public Solution<ST, PT> breed(List<Solution<ST, PT>> population, Selector<ST, PT> selector) {

        if (population == null || selector == null) {
            return null;
        }

        Solution<ST, PT> individualA = selector.select(population);
        Solution<ST, PT> individualB = selector.select(population);
        return breedTwo(individualA, individualB);
    }

    public abstract Solution<ST, PT> breedTwo(Solution<ST, PT> a, Solution<ST, PT> b);
}

