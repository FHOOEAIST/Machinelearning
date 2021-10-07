package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates the solutions of the child node and returns the solution with the best quality.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class EvaluatorSolutionNode extends FunctionalGPGraphNode<Solution> {

    private Evaluator evaluator;

    @Override
    public boolean checkValidity() {
        if (getChildNodes().size() == 1 &&
                getChildNodes().get(0) instanceof GenericFunctionalCollectionGPGraphNode &&
                evaluator != null
        ) {

            GenericFunctionalCollectionGPGraphNode node1Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(0);

            return node1Casted.getClazz().equals(Solution.class);
        }

        return false;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Collection.class);
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public Solution calculateValue() {

        Solution bestSolution = null;
        double quality = 0.0;

        ArrayList<Solution> solutions = (ArrayList<Solution>) getChildNodes().get(0).execute();

        BasicNodeUtil.removeAllGivenValueFromCollection(solutions, null);

        for (Solution solution : solutions) {

            //if quality is 0, then it probably wasn't evaluated before
            if (solution.getQuality() == 0.0) {
                evaluator.evaluateQuality(solution);
            }

            if (bestSolution == null || solution.getQuality() < quality) {
                bestSolution = solution;
                quality = solution.getQuality();
            }
        }

        return bestSolution;
    }

    @Override
    public Solution simpleReturnType() {
        return new Solution();
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
