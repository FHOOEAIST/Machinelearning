package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Will return the solution that was previously defined for this node. Suitable when heuristic optimisation should
 * already start at a specific solution.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class SpecificSolutionNode extends CacheableGPGraphNode<Solution> {

    private Solution solution;

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("solution", new Descriptor<>(solution));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("solution")) {
                setSolution((Solution) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Solution calculateValue() {
        return solution;
    }

    @Override
    public Solution simpleReturnType() {
        return new Solution();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
