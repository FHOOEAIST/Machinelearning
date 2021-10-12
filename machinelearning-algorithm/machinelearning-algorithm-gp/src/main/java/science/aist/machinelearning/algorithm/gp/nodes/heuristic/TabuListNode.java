/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.nodes.heuristic;

import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * Implements functionality of Tabu list. Checks if solutions in a given collection are also contained in the tabu list
 * and removes them. Then adds the new best solution to the tabu list. Will also check if a maximum tabu list size has
 * been reached.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class TabuListNode extends GenericFunctionalCollectionGPGraphNode<Solution> {

    private final LinkedList<Solution> tabuList = new LinkedList<>();

    private Evaluator evaluator;

    public TabuListNode(Class<Solution> clazz) {
        //only works with solutions, so we set this everytime
        super(Solution.class);
    }

    @Override
    public boolean checkValidity() {
        if (getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Number &&
                getChildNodes().get(1) instanceof GenericFunctionalCollectionGPGraphNode &&
                evaluator != null
        ) {

            GenericFunctionalCollectionGPGraphNode node1Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(1);

            return node1Casted.getClazz().equals(Solution.class);
        }

        return false;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Number.class);
        classes.add(Collection.class);
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("evaluator", new Descriptor<>(evaluator));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("evaluator")) {
                setEvaluator((Evaluator) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Collection<Solution> calculateValue() {

        double tabuSize = Math.abs((Double) getChildNodes().get(0).execute());
        ArrayList<Solution> solutions = (ArrayList<Solution>) getChildNodes().get(1).execute();

        //remove all solutions that are null
        BasicNodeUtil.removeAllGivenValueFromCollection(solutions, null);

        //remove all the tabu solutions from the given list
        for (Solution tabuSolution : tabuList) {
            solutions.remove(tabuSolution);
        }

        //we removed all the solutions with this
        if (solutions.size() == 0) {
            return new ArrayList<>();
        }

        //find the current best solution in the remaining list
        Solution bestSolution = null;
        double quality = 0.0;

        for (Solution solution : solutions) {

            //if quality is 0, then it probably wasn't evaluated before
            if (solution.getQuality() == 0.0) {
                evaluator.evaluateQuality(solution);
            }

            if (bestSolution == null || solution.getQuality() < quality) {
                bestSolution = solution;
                quality = solution.getQuality();
            }
        }

        //add the current best solution to the tabu list
        tabuList.add(bestSolution);

        //check if tabu list is too large (depending on given tabu size) and remove the first entry if necessary
        for (int i = 0; i < tabuList.size() - tabuSize; i++) {
            tabuList.removeFirst();
        }

        return solutions;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
}
