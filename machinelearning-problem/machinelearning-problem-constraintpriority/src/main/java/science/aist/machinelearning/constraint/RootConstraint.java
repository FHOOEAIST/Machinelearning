package science.aist.machinelearning.constraint;

/**
 * Constraint for the root of a ConstraintPriorityTree. Will always return true, forcing calculation of the root node,
 * if no other result has been found in the tree.
 *
 * @param <CT> constraint Type
 * @author Daniel Wilfing
 * @since 1.0
 */
public class RootConstraint<CT> implements Constraint<CT> {

    @Override
    public boolean evaluate(CT object) {
        return true;
    }
}
