/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example;

import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.mapping.GeneCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Turns each schedulable project into a projectslot. Will then shuffle the list.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class FisherThompsonGeneCreator implements GeneCreator<List<Integer>, Integer> {

    public static int runs = 0;
    private Integer n = 10;

    public void setN(Integer n) {
        this.n = n;
    }

    @Override
    public List<Integer> createGene(ProblemGene<Integer> problem) {

        runs++;

        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            slots.add(i);
        }

        //shuffle the list
        Collections.shuffle(slots);

        return slots;
    }
}

