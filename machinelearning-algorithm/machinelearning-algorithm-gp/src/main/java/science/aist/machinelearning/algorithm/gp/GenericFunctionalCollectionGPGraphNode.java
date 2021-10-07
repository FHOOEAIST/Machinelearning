package science.aist.machinelearning.algorithm.gp;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for the implementation of generic functional nodes, that return collections of generics. Generic
 * functional nodes can return any class, but should usually return numbers, booleans and solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class GenericFunctionalCollectionGPGraphNode<T> extends FunctionalGPGraphNode<Collection<T>> {

    protected Class<T> clazz;

    public GenericFunctionalCollectionGPGraphNode(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ArrayList<T> simpleReturnType() {
        return new ArrayList<>();
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
