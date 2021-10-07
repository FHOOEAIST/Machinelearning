package science.aist.machinelearning.algorithm.mapping;


import java.util.*;
import java.util.function.Predicate;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class AStarImpl<NT, WT extends Number> {

    /**
     * calculates shortest path using A* algorithm
     *
     * @param graph      graph
     * @param from       starting pathNode
     * @param to         ending pathNode
     * @param comparator comparator
     * @return shortest path from starting node to ending node, empty List if no path is possible. <br/> If more than
     * one shortest path is possible it is undefined which one will be returned.
     */
    public List<NT> findShortestPath(Map<NT, Map<NT, WT>> graph, NT from, Predicate<NT> to, Comparator<Number> comparator, WeightCalculator<NT, WT> weightCalculator) {

        Set<NT> closed = new HashSet<>();
        Set<NT> open = new HashSet<>();
        open.add(from);
        Map<NT, NT> cameFrom = new HashMap<>();
        Map<NT, Number> accumulatedWeights = new HashMap<>();
        accumulatedWeights.put(from, 0d);

        while (!open.isEmpty()) {
            NT currentNode = pollLowest(open, accumulatedWeights, comparator);

            if (to.test(currentNode)) {
                return reconstructPath(cameFrom, currentNode);
            }
            closed.add(currentNode);

            graph.get(currentNode).forEach((neighbor, weight) -> {

                if (!closed.contains(neighbor)) {
                    open.add(neighbor);

                    Number weightToNeighbor = accumulatedWeights.get(neighbor);

                    Number newNeighborWeight = weightCalculator.weight(
                            accumulatedWeights.get(currentNode),
                            weight.doubleValue(),
                            weightCalculator.estimatedWeight(currentNode, neighbor, graph)
                    );
                    if (weightToNeighbor == null ||
                            comparator.compare(
                                    weightToNeighbor,
                                    newNeighborWeight
                            ) > 0) {
                        cameFrom.put(neighbor, currentNode);
                        accumulatedWeights.put(neighbor, newNeighborWeight);
                    }
                }
            });

        }
        return new ArrayList<>();
    }

    /**
     * backtrack from end node to start node to get the path
     *
     * @param cameFrom    connections between pathNodes
     * @param currentNode end pathNode
     * @return path from start pathNode to end pathNode
     */
    private List<NT> reconstructPath(Map<NT, NT> cameFrom, NT currentNode) {
        List<NT> path = new ArrayList<>();

        while (currentNode != null) {
            path.add(0, currentNode);
            currentNode = cameFrom.get(currentNode);
        }
        return path;
    }

    /**
     * Find node with lowest weight and remove it from the set
     *
     * @param open               set to search in
     * @param accumulatedWeights map with weights
     * @return pathNode with lowest weight
     */
    private NT pollLowest(Set<NT> open, Map<NT, Number> accumulatedWeights, Comparator<Number> comparator) {
        if (open.isEmpty()) {
            throw new IllegalStateException("open may not be empty");
        }
        NT lowest = null;

        boolean firstRound = true;

        for (NT current : open) {
            if (firstRound) {
                firstRound = false;
                lowest = current;
            } else if (comparator.compare(accumulatedWeights.get(lowest), accumulatedWeights.get(current)) > 0) {
                lowest = current;
            }
        }
        open.remove(lowest);
        return lowest;
    }


}
