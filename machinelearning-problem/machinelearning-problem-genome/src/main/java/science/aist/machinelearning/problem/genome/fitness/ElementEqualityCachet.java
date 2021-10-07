package science.aist.machinelearning.problem.genome.fitness;


import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.problem.genome.Element;

import java.util.Objects;

/**
 * A cachet must calculate a specific quality (part) of a mapping. The better the quality is, the closer to 0 it is.
 * <p>
 * This cachet checks against a definitive sequence of a valid mapping. Each element in the sequence that is not equal
 * to our mapping decreases the quality of our mapping by 1;
 *
 * @author Oliver Krauss
 * @since 1.0
 */

public class ElementEqualityCachet implements CachetEvaluator<Element[], Element[]> {

    private String targetSequence;

    public ElementEqualityCachet() {
    }

    public String getTargetSequence() {
        return targetSequence;
    }

    public void setTargetSequence(String targetSequence) {
        this.targetSequence = targetSequence;
    }

    @Override
    public double evaluateQuality(Solution<Element[], Element[]> solution) {
        double quality = 0;

        if (solution != null && solution.getSolutionGenes() != null && solution.getSolutionGenes().size() > 0) {

            SolutionGene<Element[], Element[]> genes = solution.getSolutionGenes().get(0);

            for (int i = 0; i < targetSequence.length(); i++) {
                if (targetSequence.charAt(i) != genes.getGene()[i].getValue()) {
                    quality++;
                }
            }

            solution.getCachets().add(new Cachet(quality, "ElementEqualityCachet"));
        }

        return quality;
    }

    @Override
    public String getName() {
        return "ElementEqualityCachet";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementEqualityCachet that = (ElementEqualityCachet) o;

        return Objects.equals(targetSequence, that.targetSequence);

    }

    @Override
    public int hashCode() {
        return targetSequence != null ? targetSequence.hashCode() : 0;
    }
}
