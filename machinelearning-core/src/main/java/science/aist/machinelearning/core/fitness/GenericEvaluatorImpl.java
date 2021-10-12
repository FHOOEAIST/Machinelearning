/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.fitness;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generic version of the {@link Evaluator}. It executes all cachet evaluators and sums them up according to a given
 * multiplier concerning the cachet importance.
 *
 * @param <ST> Solution Type
 * @param <PT> Problem Type
 * @author Oliver Krauss
 * @since 1.0
 */
public class GenericEvaluatorImpl<ST, PT> implements Evaluator<ST, PT>, Configurable {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(GenericEvaluatorImpl.class);
    /**
     * Dictionary of all cachets to be evaluated for a mapping The double value in the map defines how important one
     * cachet is compared to the others
     */
    private Map<CachetEvaluator<ST, PT>, Double> cachetEvaluators;

    @Override
    public double evaluateQuality(Solution solution) {
        logger.trace("Evaluating quality");
        double quality = 0;

        if (solution == null) {
            return Double.MAX_VALUE;
        }

        if (solution.getCachets() != null) {
            solution.getCachets().clear();
        }

        // calculate quality by adding up all cachets
        for (Map.Entry<CachetEvaluator<ST, PT>, Double> evaluator : cachetEvaluators.entrySet()) {
            // cachet * priorty
            quality += evaluator.getKey().evaluateQuality(solution) * evaluator.getValue();
        }
        solution.setQuality(quality);

        logger.trace("Finished evaluating quality");
        return quality;
    }

    @Override
    public Map<CachetEvaluator<ST, PT>, Double> returnCachetDictionary() {
        return cachetEvaluators;
    }

    @Override
    public String evaluationIdentity() {
        return cachetEvaluators.keySet().stream()
                .map(i -> i.getName() + " * " + cachetEvaluators.get(i)).collect(Collectors.joining(" + "));
    }

    /**
     * Setter for dependency Injection
     *
     * @param cachetEvaluators the cachet evaluators
     */
    @Required
    public void setCachetEvaluators(Map<CachetEvaluator<ST, PT>, Double> cachetEvaluators) {
        this.cachetEvaluators = cachetEvaluators;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();
        options.put("cachetEvaluators", new Descriptor<>(cachetEvaluators));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("cachetEvaluators")) {
                setCachetEvaluators((Map<CachetEvaluator<ST, PT>, Double>) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
