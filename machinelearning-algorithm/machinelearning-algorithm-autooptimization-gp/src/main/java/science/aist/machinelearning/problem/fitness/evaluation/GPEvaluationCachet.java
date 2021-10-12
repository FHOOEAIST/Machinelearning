/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.fitness.evaluation;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
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
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPEvaluationCachet<ST, PT> implements CachetEvaluator<ResultNode, GPProblem> {

    /**
     * Defines how often each problem gets solved.
     */
    private int runsPerProblem = 1;

    private Collection<Problem<PT>> problems;

    private Evaluator<ST, PT> evaluator;

    @Override
    public double evaluateQuality(Solution<ResultNode, GPProblem> solution) {

        if (solution == null || solution.getSolutionGenes() == null || solution.getSolutionGenes().size() == 0) {
            return 1_000_000; //super bad quality
        }

        SolutionGene<ResultNode, GPProblem> currentGene = solution.getSolutionGenes().get(0);

        double quality = 0.0;
        for (Problem<PT> problem : problems) {

            //find SolutionCreators and give them the problem to solve
            checkForSolutionCreator(currentGene.getGene(), problem);

            //solve the problem x times and add the average to the quality
            double problemQuality = 0.0;
            for (int i = 0; i < runsPerProblem; i++) {
                problemQuality += evaluator.evaluateQuality(currentGene.getGene().execute());
            }
            quality += problemQuality / runsPerProblem;
        }

        if (quality < 0 || quality > 1_000_000) {
            quality = 1_000_000;
        }

        solution.getCachets().add(new Cachet(quality, "GPEvaluationCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "GPEvaluationCachet";
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
