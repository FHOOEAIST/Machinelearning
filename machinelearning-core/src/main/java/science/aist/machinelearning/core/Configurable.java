package science.aist.machinelearning.core;

import science.aist.machinelearning.core.options.Descriptor;

import java.util.Map;

/**
 * Defines generic configuration options for classes in Machinelearning
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Configurable {

    /**
     * Get the settings for this algorithm
     *
     * @return map of names and descriptors
     */
    Map<String, Descriptor> getOptions();

    /**
     * Set settings for this algorithm using a map of strings and descriptors.
     *
     * @param options options to set
     * @return true = setting the option was successful, false = failed to set the options
     */
    default boolean setOptions(Map<String, Descriptor> options) {
        return options.entrySet()
                .stream()
                .allMatch(p -> setOption(p.getKey(), p.getValue()));
    }

    /**
     * Set a single descriptor for this algorithm
     *
     * @param name       name of the parameter
     * @param descriptor descriptor to set
     * @return true = setting the option was successful, false = failed to set the options
     */
    boolean setOption(String name, Descriptor descriptor);
}
