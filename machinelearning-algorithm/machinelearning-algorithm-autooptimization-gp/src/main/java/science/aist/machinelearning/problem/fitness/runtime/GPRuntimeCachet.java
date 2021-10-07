package science.aist.machinelearning.problem.fitness.runtime;

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
import science.aist.machinelearning.problem.GPProblem;

import java.util.Collection;
import java.util.List;

/**
 * Checks the quality of a GP-heuristic using the runtime for its execution. The longer the runtime, the worse the
 * quality.
 * <p>
 * Will newly calculate the runtime using a set of problems. A max runtime can be specified.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPRuntimeCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    final static Logger logger = Logger.getLogger(GPRuntimeCachet.class);

    /**
     * Defines how often each problem gets solved.
     */
    private int runsPerProblem = 1;

    /**
     * Max time an evaluation should take until it gets interrupted
     */
    private long evaluationTime = 1000;

    /**
     * Time a specific thread waits until it has to check for the current evaluation time again.
     */
    private long evaluationTimeStep = 50;

    private Collection<Problem<PT>> problems;

    /**
     * Required to check if we have to interrupt an evaluation process.
     */
    private boolean finishedCalculation = false;

    /**
     * To check if we had to interrupt the execution of the graph or not. If we have to interrupt, add some penalty to
     * the quality.
     */
    private boolean interrupted = false;

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {
        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            solution.getCachets().add(new Cachet(100_000_000.0, "GPEvaluationTimerCachet"));
            return 100_000_000; //super bad quality
        }

        SolutionGene<ResultNode, GPProblem> currentGene = solution.getSolutionGenes().get(0);

        ResultNode trimedGraph = GPTrim.trimGraph(currentGene.getGene());

        double quality = 0.0;
        for (Problem<PT> problem : problems) {

            //find SolutionCreators and give them the problem to solve
            checkForSolutionCreator(trimedGraph, problem);

            //solve the problem x times and add the average to the quality
            double problemQuality = 0.0;

            int i;
            for (i = 0; i < runsPerProblem; i++) {

                finishedCalculation = false;
                interrupted = false;

                Thread sleepThread = new Thread(() -> {
                    try {
                        long sleptFor = 0;
                        //sleep for a bit, then check if calculations are done
                        while (sleptFor < evaluationTime && !finishedCalculation) {
                            Thread.sleep(evaluationTimeStep);
                            sleptFor += evaluationTimeStep;
                        }

                        //if we have waited, but the evaluation is not done yet, then try to interrupt the evaluation
                        if (!finishedCalculation) {
                            interrupted = true;
                            BasicNodeUtil.interruptGraph(trimedGraph, true);
                            logger.info("Interrupted execution of a gp-graph.");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                long duration = System.currentTimeMillis();

                sleepThread.start();

                try {
                    trimedGraph.execute();
                    finishedCalculation = true;
                } catch (Error e) {
                    //the gp-graph created so many solutions, that we either ran into outOfMemory- or GCOverhead-Errors
                    //penalize the solution, then lets try to fix this problem by garbage collecting once more.
                    System.gc();
                    interrupted = true;
                    logger.info("Had to stop execution because of: " + e.getClass().getName());
                }

                problemQuality += System.currentTimeMillis() - duration;

                try {
                    sleepThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //if we have to interrupt calculation of the graph, then we have to penalize it severely
                if (interrupted) {
                    problemQuality += evaluationTime;

                    //if we had to interrupt the execution process, its unlikely that it will work on the next run
                    break;
                }

            }

            quality += problemQuality / (i + 1);
        }
        quality /= problems.size();

        if (quality < 0 || quality > 1_000_000) {
            quality = 1_000_000;
        }

        solution.getCachets().add(new Cachet(quality, "GPRuntimeCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPRuntimeCachet";
    }

    public void setRunsPerProblem(int runsPerProblem) {
        this.runsPerProblem = runsPerProblem;
    }

    public void setEvaluationTime(long evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public void setEvaluationTimeStep(long evaluationTimeStep) {
        this.evaluationTimeStep = evaluationTimeStep;
    }

    public void setProblems(Collection<Problem<PT>> problems) {
        this.problems = problems;
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
