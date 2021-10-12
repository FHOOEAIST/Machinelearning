/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.mutation;

import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Mutator that contains an evaluator for comparing results.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class EvaluatingMutator<ST, PT> implements Mutator<ST, PT>, Configurable {

    /**
     * evaluator to check quality of the mutated solution
     */
    private Evaluator<ST, PT> evaluator;

    public Evaluator<ST, PT> getEvaluator() {
        return evaluator;
    }

    @Required
    public void setEvaluator(Evaluator<ST, PT> evaluator) {
        this.evaluator = evaluator;
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
                setEvaluator((Evaluator<ST, PT>) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
