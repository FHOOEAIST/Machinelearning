package science.aist.machinelearning.algorithm.amalgam;

import org.apache.log4j.Logger;
import science.aist.machinelearning.core.AbstractAlgorithm;
import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Algorithm that searches by calling other algorithms. Will then hand the best mapping over to the next algorithm.
 * Stops after all algorithms finished and hands this best mapping back to the user.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamAlgorithm<GT, PT> extends AbstractAlgorithm<GT, PT> {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(AmalgamAlgorithm.class);
    /**
     * Map of algorithms, that will be used during calculation.
     */
    private List<Algorithm<GT, PT>> algorithms = new ArrayList<>();

    @Override
    public Solution<GT, PT> solve(Problem<PT> problem) {
        return solve(problem, null);
    }

    @Override
    public Solution<GT, PT> solve(Problem<PT> problem, Solution<GT, PT> bestSolution) {

        if (problem == null) {
            return null;
        }

        int start = 0;
        if (bestSolution == null) {
            logger.debug("Starting to get a first best solution with: " + algorithms.get(0).getClass().getCanonicalName());
            bestSolution = algorithms.get(0).solve(problem);
            start++;
        }

        for (Algorithm<GT, PT> algorithm : algorithms.subList(start, algorithms.size())) {
            logger.debug("Starting to search with: " + algorithm.getClass().getCanonicalName());

            bestSolution = algorithm.solve(problem, bestSolution);
        }
        logger.debug("Found best mapping");
        return bestSolution;
    }

    public void setAlgorithms(List<Algorithm<GT, PT>> algorithms) {
        this.algorithms = algorithms;
    }

    @Override
    protected Map<String, Descriptor> getSpecificOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("algorithms", new Descriptor<>(algorithms));

        return options;
    }

    @Override
    protected boolean setSpecificOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("algorithms")) {
                setAlgorithms((List<Algorithm<GT, PT>>) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
