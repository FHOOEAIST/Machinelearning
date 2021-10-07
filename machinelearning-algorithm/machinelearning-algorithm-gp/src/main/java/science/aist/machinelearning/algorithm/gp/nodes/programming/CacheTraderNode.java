package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GenericFunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Trades the caches of the objects and then returns cache of the first object.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class CacheTraderNode<T> extends GenericFunctionalGPGraphNode<T> {
    public CacheTraderNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 2 &&
                getChildNodes().get(0) instanceof CacheableGPGraphNode &&
                getChildNodes().get(1) instanceof CacheableGPGraphNode &&
                getChildNodes().get(0).simpleReturnType().getClass().equals(getChildNodes().get(1).simpleReturnType().getClass());
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(clazz);
        classes.add(clazz);
        return classes;
    }

    @Override
    public T calculateValue() {
        CacheableGPGraphNode node1 = (CacheableGPGraphNode) getChildNodes().get(0);
        CacheableGPGraphNode node2 = (CacheableGPGraphNode) getChildNodes().get(1);

        T cache = (T) node1.execute();
        node1.setCachedValue(node2.execute());
        node2.setCachedValue(cache);

        return (T) node1.getCachedValue();
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
