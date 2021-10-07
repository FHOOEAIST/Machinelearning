package science.aist.machinelearning.constraint;

import science.aist.machinelearning.tree.ConstraintPriorityTreeNode;

import java.io.Serializable;

/**
 * Interface for creating constraints, required by the {@link ConstraintPriorityTreeNode}. Evaluate constraints of the
 * type CT and return true or false.
 *
 * @param <CT> constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface Constraint<CT> extends Serializable {

    /**
     * Evaluates the given object.
     *
     * @param object object to evaluate
     * @return true = object satisfied, false = not satisfied
     */
    boolean evaluate(CT object);
}
