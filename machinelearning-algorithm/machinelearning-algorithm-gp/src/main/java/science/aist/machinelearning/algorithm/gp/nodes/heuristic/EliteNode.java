package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * Evaluates the child node and and returns a collection containing the x best solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class EliteNode extends GenericFunctionalCollectionGPGraphNode<Solution> {

    public EliteNode(Class<Solution> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        if (getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Number &&
                getChildNodes().get(1) instanceof GenericFunctionalCollectionGPGraphNode
        ) {

            GenericFunctionalCollectionGPGraphNode node1Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(1);

            return node1Casted.getClazz().equals(Solution.class);
        }

        return false;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Number.class);
        classes.add(Collection.class);
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public ArrayList<Solution> calculateValue() {

        List<Solution> solutions = (ArrayList<Solution>) getChildNodes().get(1).execute();

        BasicNodeUtil.removeAllGivenValueFromCollection(solutions, null);

        solutions.sort(Comparator.comparingDouble(Solution::getQuality));

        int elite = ((Double) getChildNodes().get(0).execute()).intValue();
        if (elite > solutions.size()) {
            elite = solutions.size();
        } else if (elite < 0) {
            elite = 0;
        }

        return new ArrayList<>(solutions.subList(0, elite));
    }

    @Override
    public ArrayList<Solution> simpleReturnType() {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        return new HashMap<>();
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        return true;
    }

    @Override
    public Class getClazz() {
        return clazz;
    }
}
