package science.aist.machinelearning.algorithm.gp;

/**
 * Interface for the creation of nodes that can be interrupted for calculation. Very useful for interrupting very time
 * consuming nodes (big for-loops or infinite loops).
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface InterruptibleNode {

    /**
     * Tries to stop the node from the current execution-method.
     *
     * @param value true = interrupt the node, false = don't interrupt or stop interruption
     */
    void interrupt(boolean value);
}
