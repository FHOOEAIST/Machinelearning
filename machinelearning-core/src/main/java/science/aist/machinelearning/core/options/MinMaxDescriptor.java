package science.aist.machinelearning.core.options;

/**
 * Used for defining primitive settings. Can contain either a fixed value or min/max-settings.
 * <p>
 * Very useful for defining mutation settings when optimizing algorithms.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class MinMaxDescriptor<T> extends Descriptor<T> {

    /**
     * Minimum value the descriptor can be.
     */
    private final T min;

    /**
     * Maximum value the descriptor can be.
     */
    private final T max;

    public MinMaxDescriptor(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
