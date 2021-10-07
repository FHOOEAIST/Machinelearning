package science.aist.machinelearning.example.mockup;

import science.aist.machinelearning.algorithm.mapping.WeightCalculator;

import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class EstimateWeightCalculator implements WeightCalculator<Node, Double> {

    @Override
    public Double weight(Number accumulatedWeight, Number weightForOneNode, Number estimatedWeight) {
        return estimatedWeight.doubleValue();
    }

    @Override
    public Double estimatedWeight(Node from, Node to, Map<Node, Map<Node, Double>> graph) {
        return to.getBeeLine();
    }
}
