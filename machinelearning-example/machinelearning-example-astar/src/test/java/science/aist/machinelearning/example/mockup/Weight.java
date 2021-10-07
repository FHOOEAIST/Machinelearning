package science.aist.machinelearning.example.mockup;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class Weight extends Number {
    private Integer value;

    public Weight(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }


}
