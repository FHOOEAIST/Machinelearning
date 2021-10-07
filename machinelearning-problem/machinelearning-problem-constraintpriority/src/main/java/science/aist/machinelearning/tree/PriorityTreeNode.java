package science.aist.machinelearning.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * PriorityTreeNode interface offering required methods for the implementation of a Priority Tree.
 *
 * @param <RT> Return Type
 * @param <CT> Constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class PriorityTreeNode<RT, CT> implements Serializable {

    /**
     * Children of the current node. Constraints will usually passed to these children, to find better constrained
     * calculations.
     */
    private List<PriorityTreeNode<RT, CT>> childNodes = new ArrayList<>();

    /**
     * Evaluates the object of returns value of type RT.
     *
     * @param object object to evaluate
     * @return calculated result
     */
    public abstract RT evaluate(CT object);

    public List<PriorityTreeNode<RT, CT>> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<PriorityTreeNode<RT, CT>> childNodes) {
        this.childNodes = childNodes;
    }

}
