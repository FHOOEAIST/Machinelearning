package science.aist.machinelearning.problem.autooptimization.fitness;


import science.aist.machinelearning.algorithm.amalgam.AmalgamAlgorithm;
import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.problem.autooptimization.AmalgamProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Cachet that evaluates the quality of an AmalgamAlgorithm using the different algorithms given in the mapping.
 * Calculates this quality by solving the amalgamAlgorithm, then taking the quality of the best mapping.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class AmalgamEvaluationCachet<GT, PT> implements CachetEvaluator<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> {

    private final AmalgamAlgorithm<GT, PT> amalgamAlgorithm = new AmalgamAlgorithm<>();

    private Problem<PT> problem;

    @Override
    public double evaluateQuality(Solution<Algorithm<GT, PT>, AmalgamProblem<GT, PT>> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            return 100000000; //super bad quality
        }

        List<Algorithm<GT, PT>> algorithmList = new ArrayList<>();

        solution.getSolutionGenes().forEach(s -> algorithmList.add(s.getGene()));

        amalgamAlgorithm.setAlgorithms(algorithmList);

        return amalgamAlgorithm.solve(problem).getQuality();
    }

    @Override
    public String getName() {
        return "AmalgamEvaluationCachet";
    }

    public void setProblem(Problem<PT> problem) {
        this.problem = problem;
    }


}
