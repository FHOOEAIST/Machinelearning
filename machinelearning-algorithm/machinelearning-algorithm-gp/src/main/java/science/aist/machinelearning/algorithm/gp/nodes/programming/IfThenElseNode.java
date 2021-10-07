package science.aist.machinelearning.algorithm.gp.nodes.programming;


import science.aist.machinelearning.algorithm.gp.GenericFunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Depending on the boolean, will return either the first T or the second. true = return first T false = return second
 * T
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class IfThenElseNode<T> extends GenericFunctionalGPGraphNode<T> {

    public IfThenElseNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {

        return getChildNodes().size() == 3 &&
                getChildNodes().get(0).simpleReturnType() instanceof Boolean &&
                clazz.isAssignableFrom(getChildNodes().get(1).simpleReturnType().getClass()) &&
                clazz.isAssignableFrom(getChildNodes().get(2).simpleReturnType().getClass());
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Boolean.class);
        classes.add(clazz);
        classes.add(clazz);
        return classes;
    }

    @Override
    public T calculateValue() {
        if ((Boolean) getChildNodes().get(0).execute()) {
            return (T) getChildNodes().get(1).execute();
        } else {
            return (T) getChildNodes().get(2).execute();
        }
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
