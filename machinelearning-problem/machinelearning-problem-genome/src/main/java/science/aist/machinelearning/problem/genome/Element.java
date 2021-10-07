package science.aist.machinelearning.problem.genome;

import java.io.Serializable;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class Element implements Serializable {

    // value
    private char value;

    public Element(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + (value != '\u0000' ? value : "NULL") +
                '}';
    }
}
