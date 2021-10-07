package science.aist.machinelearning.problem.mapping;

import science.aist.machinelearning.problem.DoubleElement;

import java.io.Serializable;
import java.util.function.ToDoubleFunction;

/**
 * Maps a DoubleElement to double
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class DoubleElementToDoubleMapper implements ToDoubleFunction<DoubleElement>, Serializable {
    @Override
    public double applyAsDouble(DoubleElement doubleElement) {
        return doubleElement.getValue();
    }
}
