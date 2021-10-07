package science.aist.machinelearning.algorithm.ga.crossover;

import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class UniformCrossover<ST, PT> extends AbstractCrossover<ST, PT> implements Configurable {

    private final Random random = new Random();

    protected double crossoverRate = 0.5;

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    @Override
    public Solution<ST, PT> breedTwo(Solution<ST, PT> a, Solution<ST, PT> b) {

        if ((a == null || a.getSolutionGenes() == null || a.getSolutionGenes().size() == 0)) {
            if (b == null || b.getSolutionGenes() == null || b.getSolutionGenes().size() == 0) {
                return null;
            }
            return b;
        } else if (b == null || b.getSolutionGenes() == null || b.getSolutionGenes().size() == 0) {
            return a;
        }

        List<SolutionGene<ST, PT>> genes = new ArrayList<>();

        //check if the solutions have different number of genes
        //the new solution contains has the same size as the shorter solution
        int length = Math.min(a.getSolutionGenes().size(), b.getSolutionGenes().size());

        for (int i = 0; i < length; i++) {
            if (random.nextDouble() < getCrossoverRate()) {
                genes.add(a.getSolutionGenes().get(i));
            } else {
                genes.add(b.getSolutionGenes().get(i));
            }
        }

        Solution<ST, PT> crossoverSolution = new Solution<>();
        crossoverSolution.setSolutionGenes(genes);

        return crossoverSolution;
    }


    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();
        options.put("crossoverRate", new Descriptor<>(crossoverRate));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("crossoverRate")) {
                setCrossoverRate((Double) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

