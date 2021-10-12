/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for experiment. The configurable choice contains a CLASS that will be instantiated and configured as
 * given
 *
 * @param <O> options that can be chosen
 * @author Oliver Krauss
 * @since 1.0
 */
public class ProblemChoice<O> extends IteratingChoice<Problem<O>, O> {

    public ProblemChoice(String name, Class<?> optionClass) {
        super(name, optionClass);
    }

    @Override
    protected void rebuildObject() {
        // re-build the entire object to ENSURE that no global state is held
        List<ProblemGene<O>> collect = fixedOptions.stream().map(x -> {
            x.reset();
            return new ProblemGene<O>(x.next());
        }).collect(Collectors.toList());
        collect.addAll(configurationOptions.stream().map(x -> new ProblemGene<>(x.current() == null ? x.next() : x.current())).collect(Collectors.toList()));
        choice = new Problem<>(collect);
    }

}
