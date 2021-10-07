package science.aist.machinelearning.algorithm.gp;


/**
 * Abstract class for the implementation of generic functional nodes. Generic functional nodes can return any class, but
 * should usually return numbers, booleans, solutions and collection of solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class GenericFunctionalGPGraphNode<T> extends FunctionalGPGraphNode<T> {

    protected Class<T> clazz;

    public GenericFunctionalGPGraphNode(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T simpleReturnType() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
