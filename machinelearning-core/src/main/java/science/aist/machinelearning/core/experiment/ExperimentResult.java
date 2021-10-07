package science.aist.machinelearning.core.experiment;

import science.aist.machinelearning.core.Solution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Result of an {@link Experiment}
 * TODO #76 in the future we should also be able to compare the interim results. this is memory-consumptive though so that should be enabled exclusively over analytics (probably graph)
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class ExperimentResult<ST, PT> {

    /**
     * Results grouped by identifier, The Solutions are the repeats of each individual experiment
     */
    private final Map<ExperimentIdentifier, List<Solution<ST, PT>>> results = new HashMap<>();

    /**
     * Adds the results of a conducted experiment to the solution
     *
     * @param identifier identification of what was modified in the experiment parameters
     * @param solution   the actual solution
     */
    public void add(ExperimentIdentifier identifier, Solution<ST, PT> solution) {
        if (results.containsKey(identifier)) {
            results.get(identifier).add(solution);
        } else {
            List<Solution<ST, PT>> put = new LinkedList<>();
            put.add(solution);
            results.put(identifier, put);
        }
    }

    public Map<ExperimentIdentifier, List<Solution<ST, PT>>> getResults() {
        return results;
    }
}
