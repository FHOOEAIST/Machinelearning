package science.aist.machinelearning.core.experiment;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface all choices must implement
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Choice<O> extends Iterator<O> {

    /**
     * returns the currently selected choice
     *
     * @return current
     */
    O current();

    /**
     * Resets the choice to the beginning (next will return first item)
     */
    void reset();

    /**
     * Generates an identifier for the object returned by "current" The Identifier is essentially a map of all the
     * current configuration values
     *
     * @return Identifier
     */
    ExperimentIdentifier getCurrentIdentifier();

    /**
     * Convenience function to find a specific config
     *
     * @param name name of config item you want. Chainable (ex. Algorithm-&gt;Mutator-&gt;MutationRate =
     *             ga.mutator.mutationRate)
     * @return config or NULL
     */
    Choice findConfig(String name);

    /**
     * Gets all child configs so the abstract method can work with them
     *
     * @return children or null if no sub-config exists
     */
    Collection<Choice> getChildren();

    /**
     * returns the name of the choice
     *
     * @return name of choice
     */
    String getName();

    /**
     * Returns the parent object of this one
     *
     * @return parent choice of this choice, or null if toplevel
     */
    Choice getParent();

    /**
     * sets the parent object to the given choice
     *
     * @param parent parent of this object, may be null.
     */
    void setParent(Choice parent);

    /**
     * Replaces THIS choice with the given choice c in the parent. If there is no parent, NOTHING will happen.
     *
     * @param c the choice
     */
    void replace(Choice c);
}
