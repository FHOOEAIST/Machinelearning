package science.aist.machinelearning.algorithm.gp.util;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Checks if the given graph is valid. Will call the validation function of each node in the tree.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPValidator {

    /**
     * Check the graph for loops and if the node validation methods are statisfied.
     *
     * @param node node to check
     * @return true = graph is valid, false = graph is invalid, there is a loop or some settings are missing
     */
    public static boolean validateGraph(GPGraphNode node) {
        return validateGraph(node, new ArrayList<>(), true);
    }

    /**
     * Check the graph for loops only. Ignores the node validation methods.
     *
     * @param node node to check
     * @return true = graph is valid, false = graph is invalid and contains loops
     */
    public static boolean validateGraphLoopsOnly(GPGraphNode node) {
        return validateGraph(node, new ArrayList<>(), false);
    }

    /**
     * Takes the node, check if it is a functional node (requires children and specific settings) and checks if said
     * settings are correctly set. Will then also check the children.
     *
     * @param node          node to check
     * @param previousNodes nodes that have already been validated in the graph, required to check loops
     * @param fullCheck     true = check for loops and validation methods, false = check for loops only
     * @return true = graph is valid, false = graph is invalid, some settings are missing or wrong children are set
     */
    private static boolean validateGraph(GPGraphNode node, Collection<GPGraphNode> previousNodes, boolean fullCheck) {

        if (node instanceof FunctionalGPGraphNode) {

            previousNodes.add(node);

            FunctionalGPGraphNode cast = (FunctionalGPGraphNode) node;

            //if we have the fullCheck-boolean at true, check the validity of the node by calling its validation method
            if (fullCheck && !cast.checkValidity()) {
                return false;
            }

            //check if the settings of the children are correctly set
            for (GPGraphNode child : (List<GPGraphNode>) cast.getChildNodes()) {
                //if the previousNode contains the child, then we got a loop
                //otherwise, check if the children are functional and check their validity
                if (previousNodes.contains(child) ||
                        (child instanceof FunctionalGPGraphNode && !validateGraph(child, previousNodes, fullCheck))) {
                    return false;
                }
            }

            previousNodes.remove(node);
        }

        return true;
    }

}
