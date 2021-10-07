package science.aist.machinelearning.algorithm.mapping;


import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public interface WeightCalculator<NT, WT> {

    /**
     * determines on how to sum up accumulatedWeight, weightForOneNode and estimatedWeight
     *
     * @param accumulatedWeight weight of the current path
     * @param weightForOneNode  weight from the last node of the current path to the next node
     * @param estimatedWeight   estimated weight of the next node
     * @return sum of accumulatedWeight, weightForOneNode and estimatedWeight
     */
    WT weight(Number accumulatedWeight, Number weightForOneNode, Number estimatedWeight);

    /**
     * calculates or estimates the weight of a node to the end of the path or between the nodes from and to
     *
     * @param from  node
     * @param to    to
     * @param graph graph
     * @return weight from the node to the end of the path
     */
    WT estimatedWeight(NT from, NT to, Map<NT, Map<NT, WT>> graph);

}
