package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * Returns the size of a collection.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class SizeOfCollectionNode extends FunctionalGPGraphNode<Double> {
    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 1 &&
                getChildNodes().get(0).simpleReturnType() instanceof Collection;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Collection.class);
        classes.add(Object.class);
        return classes;
    }

    @Override
    public Double calculateValue() {
        return (double) ((List<?>) getChildNodes().get(0).execute()).size();
    }

    @Override
    public Double simpleReturnType() {
        return 0.0;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        return new HashMap<>();
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        return true;
    }
}
