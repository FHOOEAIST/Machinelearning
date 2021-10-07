package science.aist.machinelearning.core.mapping;

import science.aist.machinelearning.core.*;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic solution creator that produces a 1:1 mapping of ST to PT by using {@link GeneCreator}
 *
 * @param <ST> Solution Type
 * @param <PT> Problem Type
 * @author Daniel Wilfing
 * @since 1.0
 */

public class OneToOneSolutionCreator<ST, PT> implements SolutionCreator<ST, PT>, Configurable {

    public static int runs = 0;
    private GeneCreator<ST, PT> geneCreator;

    @Override
    public Solution<ST, PT> createSolution(Problem<PT> problem) {

        runs++;

        Solution<ST, PT> solution = new Solution<>();

        if (problem != null && problem.getProblemGenes() != null) {
            for (ProblemGene<PT> problemGene : problem.getProblemGenes()) {
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

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();
        options.put("geneCreator", new Descriptor<>(geneCreator));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("geneCreator")) {
                setGeneCreator((GeneCreator<ST, PT>) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
