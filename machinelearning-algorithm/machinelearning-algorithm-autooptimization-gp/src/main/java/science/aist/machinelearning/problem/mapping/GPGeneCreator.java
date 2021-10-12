/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.mapping;

import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.util.GPRepair;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.mapping.GeneCreator;
import science.aist.machinelearning.problem.GPProblem;

import java.lang.reflect.InvocationTargetException;

/**
 * Creates a GP-graph by adding nodes to the graph. Does this by creating a Root, then calling GPRepair to fix the
 * missing children.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPGeneCreator implements GeneCreator<ResultNode, GPProblem> {

    private GPRepair repair = null;

    @Override
    public ResultNode createGene(ProblemGene<GPProblem> problem) {

        ResultNode root = new ResultNode();

        try {
            //start creating the children for this node
            repair.repairGraph(
                    root,
                    problem.getGene().getValidGraphNodes(),
                    problem.getGene().getTerminalGraphNodes(),
                    problem.getGene().getFunctionalGraphNodes(),
                    problem.getGene().getNodeSettings()
            );
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return root;
    }

    public void setRepair(GPRepair repair) {
        this.repair = repair;
    }
}
