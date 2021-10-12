/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.ga.Crossover;
import science.aist.machinelearning.algorithm.ga.Selector;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Does a crossover-operation over the given list of solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class CrossoverNode extends FunctionalGPGraphNode<Solution> {

    private Crossover crossover = null;

    private Selector selector = null;

    private Evaluator evaluator = null;

    @Override
    public boolean checkValidity() {
        if (getChildNodes().size() == 1 &&
                getChildNodes().get(0) instanceof GenericFunctionalCollectionGPGraphNode &&
                crossover != null &&
                selector != null &&
                evaluator != null
        ) {

            GenericFunctionalCollectionGPGraphNode node1Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(0);

            return node1Casted.getClazz().equals(Solution.class);
        }

        return false;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Collection.class);
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public Solution calculateValue() {

        ArrayList<Solution> solutions = (ArrayList<Solution>) getChildNodes().get(0).execute();

        if (solutions == null) {
            return null;
        }

        BasicNodeUtil.removeAllGivenValueFromCollection(solutions, null);

        Solution crossoverSolution = getCrossover().breed(solutions, getSelector());
        evaluator.evaluateQuality(crossoverSolution);

        return crossoverSolution;
    }

    @Override
    public Solution simpleReturnType() {
        return new Solution();
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("crossover", new Descriptor<>(crossover));
        options.put("selector", new Descriptor<>(selector));
        options.put("evaluator", new Descriptor<>(evaluator));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "crossover":
                    setCrossover((Crossover) descriptor.getValue());
                    break;
                case "selector":
                    setSelector((Selector) descriptor.getValue());
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

    public Crossover getCrossover() {
        return crossover;
    }

    public void setCrossover(Crossover crossover) {
        this.crossover = crossover;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
}
