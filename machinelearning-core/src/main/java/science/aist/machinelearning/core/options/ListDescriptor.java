package science.aist.machinelearning.core.options;

import java.util.List;

/**
 * Used for defining complex classes for the options. Can contain either a fixed value or a list of possible values.
 * <p>
 * Very useful for defining mutation settings when optimizing algorithms.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ListDescriptor<T> extends Descriptor<T> {

    /**
     * List of possible values for this descriptor.
     */
    private final List<T> valueList;

    public ListDescriptor(List<T> valueList) {
        this.valueList = valueList;
    }

    public List<T> getValueList() {
        return valueList;
    }
}
