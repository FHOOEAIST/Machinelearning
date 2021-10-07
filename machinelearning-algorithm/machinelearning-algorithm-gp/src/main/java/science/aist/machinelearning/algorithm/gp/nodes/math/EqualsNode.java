package science.aist.machinelearning.algorithm.gp.nodes.math;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple node that implements the Equals-Operator.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class EqualsNode extends FunctionalGPGraphNode<Boolean> {

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Number &&
                getChildNodes().get(1).simpleReturnType() instanceof Number;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Number.class);
        classes.add(Number.class);
        return classes;
    }

    @Override
    public Boolean calculateValue() {
        return getChildNodes().get(0).execute().equals(getChildNodes().get(1).execute());
    }

    @Override
    public Boolean simpleReturnType() {
        return false;
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
