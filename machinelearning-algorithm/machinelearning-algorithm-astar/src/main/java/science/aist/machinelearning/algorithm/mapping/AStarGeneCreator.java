package science.aist.machinelearning.algorithm.mapping;

import science.aist.machinelearning.algorithm.gene.ShortestPathProblemGene;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.mapping.GeneCreator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class AStarGeneCreator<NT, WT extends Number> implements GeneCreator<List<NT>, ShortestPathProblemGene<NT, WT>> {
    /**
     * Implementation of AStar
     */
    private final AStarImpl<NT, WT> aStar = new AStarImpl<>();

    /**
     * options must be set before call of createGene
     */
    private Map<String, Descriptor> options;

    public Map<String, Descriptor> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Descriptor> options) {
        this.options = options;
    }

    /**
     * Create a solution options must be set before call
     *
     * @param problemGene problemGene
     * @return solution
     */
    @Override
    public List<NT> createGene(ProblemGene<ShortestPathProblemGene<NT, WT>> problemGene) {
        assert options != null;

        return aStar.findShortestPath(
                problemGene.getGene().getGraph(),
                problemGene.getGene().getFrom(),
                problemGene.getGene().getTo(),
                (Comparator<Number>) options.get("comparator").getValue(),
                (WeightCalculator<NT, WT>) options.get("weightCalculator").getValue()
        );
    }
}
