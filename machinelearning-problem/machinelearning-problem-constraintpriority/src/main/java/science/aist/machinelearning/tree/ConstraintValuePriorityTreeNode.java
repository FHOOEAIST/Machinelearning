package science.aist.machinelearning.tree;

/**
 * Same basic functionality as {@link ConstraintPriorityTreeNode}. After successfully evaluating the object, manipulates
 * it.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class ConstraintValuePriorityTreeNode<RT, CT> extends ConstraintPriorityTreeNode<RT, CT> {

    @Override
    public RT evaluate(CT object) {

        //check if we fulfill the constraint of the current node
        if (this.getConstraint().evaluate(object)) {

            //change value of the object
            object = changeValue(object);

            RT result = null;
            //look trough the child nodes
            for (PriorityTreeNode<RT, CT> node : getChildNodes()) {
                result = node.evaluate(object);
                //if one of the child nodes returns a result that is not null
                //then we've found a better constrained solution and stop looking
                if (result != null) {
                    break;
                }
            }
            //check if one of the children found a solution
            //calculate own result, if children couldn't calculate the result
            return result == null ? getCalculation().calculate(object) : result;
        }

        //constraint is not satisfied, return null
        return null;
    }

    /**
     * Allows to change or set values of the object.
     *
     * @param object object to manipulate
     * @return changed object
     */
    protected abstract CT changeValue(CT object);
}
