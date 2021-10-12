/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a solution using the defined problem and creator.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class SolutionCreatorNode extends CacheableGPGraphNode<Solution> {

    private SolutionCreator solutionCreator;

    private Problem problem;

    private Evaluator evaluator;

    @Override
    public Solution calculateValue() {
        Solution solution = getSolutionCreator().createSolution(problem);
        evaluator.evaluateQuality(solution);
        return solution;
    }

    @Override
    public Solution simpleReturnType() {
        return new Solution();
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("creator", new Descriptor<>(solutionCreator));
        options.put("problem", new Descriptor<>(problem));
        options.put("evaluator", new Descriptor<>(evaluator));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "creator":
                    setSolutionCreator((SolutionCreator) descriptor.getValue());
                    break;
                case "problem":
                    setProblem((Problem) descriptor.getValue());
                    break;
                case "evaluator":
                    setEvaluator((Evaluator) descriptor.getValue());
                    break;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public SolutionCreator getSolutionCreator() {
        return solutionCreator;
    }

    public void setSolutionCreator(SolutionCreator solutionCreator) {
        this.solutionCreator = solutionCreator;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
}
