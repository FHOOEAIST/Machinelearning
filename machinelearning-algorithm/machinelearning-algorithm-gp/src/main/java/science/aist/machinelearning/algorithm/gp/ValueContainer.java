package science.aist.machinelearning.algorithm.gp;

/**
 * Interface for nodes that contain a certain value of something.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface ValueContainer<T> {

    T getValue();

    void setValue(T value);
}
