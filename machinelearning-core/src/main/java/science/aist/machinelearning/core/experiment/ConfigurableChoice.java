/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.options.Descriptor;

/**
 * Helper class for experiment. The configurable choice contains a CLASS that will be instantiated and configured as
 * given
 *
 * @param <O> options that can be chosen
 * @author Oliver Krauss
 * @since 1.0
 */
public class ConfigurableChoice<O extends Configurable> extends IteratingChoice<O, Descriptor> {

    public ConfigurableChoice(String name, Class<? extends Configurable> optionClass) {
        super(name, optionClass);
    }

    @Override
    protected void rebuildObject() {
        // re-build the entire object to ENSURE that no global state is held
        try {
            choice = (O) optionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        fixedOptions.forEach(x -> {
                    x.reset();
                    choice.setOption(x.getName(), x.next());
                }
        );
        configurationOptions.forEach(x -> choice.setOption(x.getName(), x.current() == null ? x.next() : x.current()));
    }

}
