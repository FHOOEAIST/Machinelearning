package science.aist.machinelearning.algorithm.gene;


import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class ShortestPathProblemGene<NT, WT> {
    /**
     * Graph as adjacency matrix Contains all Nodes of NodeType NT and their edges to Nodes of NodeType, with the weight
     * of WeightType WT
     */
    private Map<NT, Map<NT, WT>> graph;

    /**
     * Where to start the path
     */
    private NT from;

    /**
     * Where to end the path
     */
    private Predicate<NT> to;


    public Map<NT, Map<NT, WT>> getGraph() {
        return graph;
    }

    public void setGraph(Map<NT, Map<NT, WT>> graph) {
        this.graph = graph;
    }

    public NT getFrom() {
        return from;
    }

    public void setFrom(NT from) {
        this.from = from;
    }

    public Predicate<NT> getTo() {
        return to;
    }

    public void setTo(Predicate<NT> to) {
        this.to = to;
    }
}
