package science.aist.machinelearning.algorithm.gp;

import java.util.ArrayList;

/**
 * GP-GraphNode that requires children for execution. Number and classes of children depend on the implementation.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class FunctionalGPGraphNode<T> extends CacheableGPGraphNode<T> {

    /**
     * Child nodes the functional graph node requires for execution
     */
    private ArrayList<GPGraphNode> childNodes = new ArrayList<>();

    /**
     * Checks if the current number of classes and types have been set for the childNodes.
     *
     * @return true = correct childNodes set, false = wrong class, wrong amount or wrong sequence set for childNodes
     */
    public abstract boolean checkValidity();

    /**
     * Returns the number of children, as well as the type of class they have to be.
     *
     * @return Collection of Classes
     */
    public abstract ArrayList<Class> requiredClassesForChildren();

    public void addChildNode(GPGraphNode child) {
        childNodes.add(child);
    }

    public ArrayList<GPGraphNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(ArrayList<GPGraphNode> childNodes) {
        this.childNodes = childNodes;
    }
}
