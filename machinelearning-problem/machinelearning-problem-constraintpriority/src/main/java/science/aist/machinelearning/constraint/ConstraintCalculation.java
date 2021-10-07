package science.aist.machinelearning.constraint;

import java.io.Serializable;

/**
 * Interface for creating calculations on the type CT. Will return a value of the type RT.
 *
 * @param <RT> Return type
 * @param <CT> Constraint type
 * @author Oliver Krauss
 * @since 1.0
 */
public interface ConstraintCalculation<RT, CT> extends Serializable {

    /**
     * Calculates value for the given object.
     *
     * @param object object to calculate a value for.
     * @return calculated value
     */
    RT calculate(CT object);
}
