package science.aist.machinelearning.core.mapping;

import science.aist.machinelearning.core.*;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Solution Creator that produces a n:1 mapping of ST to PT by using {@link GeneCreator}. Will randomly create multiple
 * solutionGenes using the same problemGene
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class NToOneSolutionCreator<ST, PT> implements SolutionCreator<ST, PT>, Configurable {

    private final Random r = new Random();
    private GeneCreator<ST, PT> geneCreator;
    private int minGenes = 1;
    private int maxGenes = 10;

    @Override
    public Solution<ST, PT> createSolution(Problem<PT> problem) {
        Solution<ST, PT> solution = new Solution<>();
        if (problem != null && problem.getProblemGenes() != null) {
            for (int i = r.nextInt(maxGenes - minGenes) + minGenes; i >= 0; i--) {
                ProblemGene<PT> problemGene = problem.getProblemGenes().get(r.nextInt(problem.getProblemGenes().size()));
                SolutionGene<ST, PT> solutionGene = new SolutionGene<>(geneCreator.createGene(problemGene));
                solutionGene.addProblemGene(problemGene);
                solution.addGene(solutionGene);
            }
        }
        return solution;
    }

    @Override
    public void setGeneCreator(GeneCreator<ST, PT> geneCreator) {
        this.geneCreator = geneCreator;
    }

    public int getMinGenes() {
        return minGenes;
    }

    public void setMinGenes(int minGenes) {
        this.minGenes = minGenes;
    }

    public int getMaxGenes() {
        return maxGenes;
    }

    public void setMaxGenes(int maxGenes) {
        this.maxGenes = maxGenes;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();
        options.put("geneCreator", new Descriptor<>(geneCreator));
        options.put("minGenes", new Descriptor<>(minGenes));
        options.put("maxGenes", new Descriptor<>(maxGenes));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "geneCreator":
                    setGeneCreator((GeneCreator<ST, PT>) descriptor.getValue());
                    break;
                case "minGenes":
                    setMinGenes((Integer) descriptor.getValue());
                    break;
                case "maxGenes":
                    setMaxGenes((Integer) descriptor.getValue());
                    break;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
