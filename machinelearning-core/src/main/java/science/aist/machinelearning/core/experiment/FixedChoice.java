/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

/**
 * Contains a choice to be statically injected. It will NOT count towards the permutations
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class FixedChoice<O> extends AbstractChoice<O> {

    /**
     * Choice to be statically injected PLEASE NOTE: IF your choice has a state (ex. Stateful Crossover) this will screw
     * up the experiment You should implement a specialized fixed choice that will implement the "reset"
     */
    O choice;

    public FixedChoice(String name, O choice) {
        super(name);
        this.choice = choice;
    }

    @Override
    public O next() {
        return choice;
    }

    @Override
    public O current() {
        return choice;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void reset() {
        // do nothing
    }
}
