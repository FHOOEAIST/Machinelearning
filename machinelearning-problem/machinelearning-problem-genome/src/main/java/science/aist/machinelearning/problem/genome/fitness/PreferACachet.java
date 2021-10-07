package science.aist.machinelearning.problem.genome.fitness;


import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.problem.genome.Element;

/**
 * Evaluation Cachet that prefers Sequences that contain A.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class PreferACachet implements CachetEvaluator<Element[], Element[]> {

    @Override
    public double evaluateQuality(Solution<Element[], Element[]> solution) {
        double quality = 0;

        if (solution != null &&
                solution.getSolutionGenes() != null &&
                solution.getSolutionGenes().size() > 0 &&
                solution.getSolutionGenes().get(0) != null) {

            Element[] elements = solution.getSolutionGenes().get(0).getGene();

            for (Element e : elements) {
                if (e.getValue() != 'A') {
                    quality++;
                }
            }

            solution.getCachets().add(new Cachet(quality, "PreferACachet"));
        }

        return quality;
    }

    @Override
    public String getName() {
        return "PreferACachet";
    }
}
