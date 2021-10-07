package science.aist.machinelearning.core.options;

/**
 * Contains data for the options of algorithms.
 * <p>
 * Used for defining primitive settings. Can contain either a fixed value or min/max-settings.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class Descriptor<T> {

    /**
     * Fixed value for this descriptor.
     */
    private T value;

    public Descriptor() {
    }

    public Descriptor(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
