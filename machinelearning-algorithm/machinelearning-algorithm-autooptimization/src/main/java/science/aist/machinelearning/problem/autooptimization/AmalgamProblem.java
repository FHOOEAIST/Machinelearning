package science.aist.machinelearning.problem.autooptimization;

import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.Map;

/**
 * Problem for defining the necessary settings for amalgam optimization. Contains algorithms and options that will be
 * used during the machine learning process.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamProblem<ST, PT> {

    /**
     * Algorithms to use during the calculation
     */
    Algorithm<ST, PT> algorithm;

    /**
     * Options to use during the calculation
     */
    Map<String, Descriptor> options;

    public AmalgamProblem(Algorithm algorithm, Map<String, Descriptor> options) {
        this.algorithm = algorithm;
        this.options = options;
    }

    public Algorithm<ST, PT> getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm<ST, PT> algorithm) {
        this.algorithm = algorithm;
    }

    public Map<String, Descriptor> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Descriptor> options) {
        this.options = options;
    }
}
