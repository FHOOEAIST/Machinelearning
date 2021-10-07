package science.aist.machinelearning.problem.fitness.evaluation;

import org.apache.log4j.Logger;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.algorithm.gp.util.GPTrim;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.problem.GPProblem;

import java.util.Collection;
import java.util.List;

/**
 * Checks the quality of the GP-heuristic by running different problems on them. Depending on the settings, will
 * calculate a single problem several times and average the quality out.
 * <p>
 * Will first check how many solutions the heuristic will create during the execution-process. If the heuristic creates
 * too many solutions, will not execute and heavily penalize the heuristic.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPEvaluationLimitCreatorCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {
    final static Logger logger = Logger.getLogger(GPEvaluationAbortingTimerCachet.class);

    /**
     * Defines how often each problem gets solved.
     */
    private int runsPerProblem = 1;

    /**
     * If a heuristic might create more solutions than this value, will not execute the heuristic.
     */
    private int maxSolutionCreations = 100_000;

    private Collection<Problem<PT>> problems;

    private Evaluator<ST, PT> evaluator;

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            return 100_000_000; //super bad quality
        }

        SolutionGene<ResultNode, GPProblem> currentGene = solution.getSolutionGenes().get(0);

        double quality = 0.0;

        ResultNode trimedGraph = GPTrim.trimGraph(currentGene.getGene());

        //check if the heuristic doesn't create too many solutions
        if (BasicNodeUtil.solutionsCreatedByGraph(trimedGraph) > maxSolutionCreations) {
            solution.getCachets().add(new Cachet(1_000_000.0, "GPEvaluationLimitCreatorCachet"));
            return 1_000_000;
        }

        for (Problem<PT> problem : problems) {

            //find SolutionCreators and give them the problem to solve
            checkForSolutionCreator(trimedGraph, problem);

            //solve the problem x times and add the average to the quality
            double problemQuality = 0.0;
            for (int i = 0; i < runsPerProblem; i++) {
                problemQuality += evaluator.evaluateQuality(trimedGraph.execute());
            }
            quality += problemQuality / runsPerProblem;
        }
        solution.getCachets().add(new Cachet(quality, "GPEvaluationLimitCreatorCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPEvaluationLimitCreatorCachet";
    }

    public void setRunsPerProblem(int runsPerProblem) {
        this.runsPerProblem = runsPerProblem;
    }

    public void setProblems(Collection<Problem<PT>> problems) {
        this.problems = problems;
    }

    public void setEvaluator(Evaluator<ST, PT> evaluator) {
        this.evaluator = evaluator;
    }

    public void setMaxSolutionCreations(int maxSolutionCreations) {
        this.maxSolutionCreations = maxSolutionCreations;
    }

    /**
     * Checks where the solutionCreator is and gives him the problem.
     *
     * @param currentNode currentNode to check
     * @param problem     problem to hand over to the solutionCreator
     */
    private void checkForSolutionCreator(GPGraphNode currentNode, Problem<PT> problem) {
        //the currentNode is a solutionCreator and we give him the problem
        if (currentNode instanceof SolutionCreatorNode) {
            SolutionCreatorNode castedNode = (SolutionCreatorNode) currentNode;
            castedNode.setProblem(problem);
        }
        //the currentNode is a functional node and we have to check its children
        else if (currentNode instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) currentNode;
            for (GPGraphNode nextNode : (List<GPGraphNode>) castedNode.getChildNodes()) {
                checkForSolutionCreator(nextNode, problem);
            }
        }
    }
}
