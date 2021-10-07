package science.aist.machinelearning.problem;

import java.io.Serializable;

/**
 * Container for a double value
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class DoubleElement implements Serializable {

    // value
    private double value;

    public DoubleElement(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + (value != '\u0000' ? value : "NULL") +
                '}';
    }
}