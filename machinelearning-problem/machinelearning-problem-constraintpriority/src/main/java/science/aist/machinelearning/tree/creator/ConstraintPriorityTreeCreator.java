package science.aist.machinelearning.tree.creator;

import science.aist.machinelearning.constraint.Constraint;
import science.aist.machinelearning.tree.ConstraintPriorityTreeNode;

import java.util.List;

/**
 * Interface for the implementation of trees with different constraints.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface ConstraintPriorityTreeCreator<RT, CT> {

    /**
     * Builds a PriorityTree using the given constraints.
     *
     * @param constraints behaviour to analyze when creating the tree
     * @return root of the new priority tree
     */
    ConstraintPriorityTreeNode<RT, CT> createTree(List<Constraint<CT>> constraints);
}
