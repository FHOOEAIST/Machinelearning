package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates the child node and returns the quality as number value.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class EvaluatorQualityNode extends FunctionalGPGraphNode<Double> {

    private Evaluator evaluator;

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 1 &&
                getChildNodes().get(0).simpleReturnType() instanceof Solution &&
                evaluator != null;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public Double calculateValue() {
        return getEvaluator().evaluateQuality((Solution) getChildNodes().get(0).execute());
    }

    @Override
    public Double simpleReturnType() {
        return 0.0;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("evaluator", new Descriptor<>(evaluator));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("evaluator")) {
                setEvaluator((Evaluator) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
}
