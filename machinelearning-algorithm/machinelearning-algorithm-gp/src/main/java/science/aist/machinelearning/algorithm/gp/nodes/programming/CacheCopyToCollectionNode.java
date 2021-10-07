package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Copies the cache of the first node into the second node. Will then return the value of the first node.
 * <p>
 * Deals with collections of objects.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class CacheCopyToCollectionNode<T> extends GenericFunctionalCollectionGPGraphNode<T> {

    public CacheCopyToCollectionNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        if (getChildNodes().size() == 2 &&
                getChildNodes().get(0) instanceof GenericFunctionalCollectionGPGraphNode &&
                getChildNodes().get(1) instanceof GenericFunctionalCollectionGPGraphNode
        ) {

            GenericFunctionalCollectionGPGraphNode node1Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(0);
            GenericFunctionalCollectionGPGraphNode node2Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(1);

            //check if the entries inside the collections returned are the same type we want
            return node1Casted.getClazz().equals(clazz) && node2Casted.getClazz().equals(clazz);
        }

        return false;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Collection.class);
        classes.add(clazz);
        classes.add(Collection.class);
        classes.add(clazz);
        return classes;
    }

    @Override
    public ArrayList<T> calculateValue() {
        CacheableGPGraphNode node1 = (CacheableGPGraphNode) getChildNodes().get(0);
        CacheableGPGraphNode node2 = (CacheableGPGraphNode) getChildNodes().get(1);

        ArrayList<T> cache = (ArrayList<T>) node1.execute();
        node2.setCachedValue(cache);

        return cache;
    }

    @Override
    public ArrayList<T> simpleReturnType() {
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
}
