package science.aist.machinelearning.problem.fitness.runtime;

import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.problem.GPProblem;
import science.aist.machinelearning.problem.fitness.evaluation.GPEvaluationTimerCachet;

/**
 * Checks the quality of a GP-heuristic using the runtime for its execution. The longer the runtime, the worse the
 * quality.
 * <p>
 * Requires the use of the {@link GPEvaluationTimerCachet}. Will take the last time calculated by this cachet and set it
 * for the new quality. Newly executing the heuristic is possible too, but would take way too much time.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPRuntimeFromEvaluationCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    /**
     * Takes the runtime value from this cachet.
     */
    private final GPEvaluationTimerCachet<ST, PT> timerCachet;

    public GPRuntimeFromEvaluationCachet(GPEvaluationTimerCachet<ST, PT> timerCachet) {
        this.timerCachet = timerCachet;
    }

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        double quality = timerCachet.getLastRuntimeCalculation();

        if (quality < 0 || quality > 1_000_000) {
            quality = 1_000_000;
        }

        solution.getCachets().add(new Cachet(quality, "GPRuntimeFromEvaluationCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPRuntimeFromEvaluationCachet";
    }
}
