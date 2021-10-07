package science.aist.machinelearning.algorithm.gp.nodes.math;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple node that negates the given boolean value.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class NegationNode extends FunctionalGPGraphNode<Boolean> {
    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 1 &&
                getChildNodes().get(0).simpleReturnType() instanceof Boolean;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Boolean.class);
        return classes;
    }

    @Override
    public Boolean calculateValue() {
        return !((Boolean) getChildNodes().get(0).execute());
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
