package science.aist.machinelearning.problem.mapping;

import science.aist.machinelearning.problem.DoubleElement;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Maps double to DoubleElement
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class DoubleToDoubleElementMapper implements Function<Double, DoubleElement>, Serializable {
    @Override
    public DoubleElement apply(Double aDouble) {
        return new DoubleElement(aDouble);
    }
}
