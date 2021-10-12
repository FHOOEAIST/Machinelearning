/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.fitness.evaluation;

import org.apache.log4j.Logger;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
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
 * Will run a timer for each evaluation process. If the evaluation process takes too long, will stop the thread.
 * Stopping like this will result in very bad results for the GP-heuristic.
 * <p>
 * Tests have shown that this evaluator is less stable and throws more OutOfMemory-Errors.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPEvaluationAbortingTimerCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    final static Logger logger = Logger.getLogger(GPEvaluationAbortingTimerCachet.class);
    /**
     * Time a specific thread waits until it has to check for the current evaluation time again.
     */
    private final long evaluationTimeStep = 10;
    /**
     * Defines how often each problem gets solved.
     */
    private int runsPerProblem = 1;
    /**
     * Max time an evaluation should take until it gets interrupted
     */
    private long evaluationTime = 1000;
    private Collection<Problem<PT>> problems;

    private Evaluator<ST, PT> evaluator;

    private Solution<ST, PT> newSolution = null;

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            solution.getCachets().add(new Cachet(1_000_000.0, "GPEvaluationAbortingTimerCachet"));
            return 1_000_000; //super bad quality
        }

        SolutionGene<ResultNode, GPProblem> currentGene = solution.getSolutionGenes().get(0);

        ResultNode trimedGraph = GPTrim.trimGraph(currentGene.getGene());

        double quality = 0.0;
        for (Problem<PT> problem : problems) {

            //find SolutionCreators and give them the problem to solve
            checkForSolutionCreator(trimedGraph, problem);

            //solve the problem x times and add the average to the quality
            double problemQuality = 0.0;
            newSolution = null;

            for (int i = 0; i < runsPerProblem; i++) {

                Thread executionThread = new Thread(() -> {

                    try {
                        newSolution = trimedGraph.execute();
                    } catch (OutOfMemoryError e) {
                        logger.info("Had to stop execution because of OutOfMemoryError");
                    }
                });

                executionThread.start();

                long sleptFor = 0;
                //sleep for a bit, then check if calculations are done
                while (sleptFor < evaluationTime && newSolution == null) {
                    try {
                        Thread.sleep(evaluationTimeStep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sleptFor += evaluationTimeStep;
                }

                //if we have waited, but the evaluation is not done yet, then try to stop the evaluation
                if (newSolution == null) {
                    executionThread.stop();
                    problemQuality = 1_000_000;
                    logger.info("Stopped execution of gp-graph-thread.");
                }
                problemQuality += evaluator.evaluateQuality(newSolution);
            }
            quality += problemQuality / runsPerProblem;
        }

        solution.getCachets().add(new Cachet(quality, "GPEvaluationAbortingTimerCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPEvaluationAbortingTimerCachet";
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

    public void setEvaluationTime(long evaluationTime) {
        this.evaluationTime = evaluationTime;
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
