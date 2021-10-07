package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.mutation.Mutator;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Does a mutator-operation using the given solution.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class MutatorNode extends FunctionalGPGraphNode<Solution> {

    private Mutator mutator;

    private Evaluator evaluator = null;

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 1 &&
                getChildNodes().get(0).simpleReturnType() instanceof Solution &&
                mutator != null &&
                evaluator != null;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public Solution calculateValue() {

        Solution solution = (Solution) getChildNodes().get(0).execute();

        if (solution == null) {
            return null;
        }

        if (solution.getQuality() == 0.0) {
            evaluator.evaluateQuality(solution);
        }

        return getMutator().mutate(solution);
    }

    @Override
    public Solution simpleReturnType() {
        return new Solution();
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("evaluator", new Descriptor<>(evaluator));
        options.put("mutator", new Descriptor<>(mutator));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("evaluator")) {
                setEvaluator((Evaluator) descriptor.getValue());
            } else if (name.equals("mutator")) {
                setMutator((Mutator) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Mutator getMutator() {
        return mutator;
    }

    public void setMutator(Mutator mutator) {
        this.mutator = mutator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
}
