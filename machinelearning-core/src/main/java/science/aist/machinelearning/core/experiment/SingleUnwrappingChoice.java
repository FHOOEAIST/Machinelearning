/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

import java.util.*;

/**
 * Helper class for experiment. The Single Choice Option selects one of the given options
 *
 * @param <O> options that can be chosen
 * @author Oliver Krauss
 * @since 1.0
 */
public class SingleUnwrappingChoice<O> extends AbstractChoice<O> {

    private List<Choice<O>> choices = new ArrayList<>();

    private int current = -1;

    public SingleUnwrappingChoice(String name) {
        super(name);
    }

    @Override
    public ExperimentIdentifier getCurrentIdentifier() {
        // in the encapsulating choice we want ONLY the the values of the current item, not all items
        Map<String, Object> identifier = this.choices.get(current).getCurrentIdentifier().getIdentifier();
        identifier.put(this.getName(), this.choices.get(current).getName());
        return new ExperimentIdentifier(identifier);
    }

    @Override
    public O next() {
        if (choices == null || current >= choices.size()) {
            return null;
        }
        if (current < 0 || !choices.get(current).hasNext() && current + 1 < choices.size()) {
            current++;
        }
        return choices.get(current).hasNext() ? choices.get(current).next() : null;
    }

    @Override
    public O current() {
        return choices == null || choices.size() < current || current < 0 ? null : choices.get(current).current();
    }


    @Override
    public boolean hasNext() {
        return choices != null && (choices.size() > current + 1 || (choices.size() > current && choices.get(current).hasNext()));
    }

    @Override
    public void reset() {
        current = -1;
    }

    public void setChoices(List<Choice<O>> choices) {
        choices.forEach(x -> x.setParent(this));
        this.choices = choices;
    }

    public void addChoice(Choice<O> choice) {
        choice.setParent(this);
        this.choices.add(choice);
    }

    @Override
    public Collection<Choice> getChildren() {
        return new LinkedList<>(choices);
    }

    @Override
    protected void replaceChild(Choice<O> oldChoice, Choice<O> newChoice) {
        oldChoice.setParent(null);
        choices.remove(oldChoice);
        addChoice(newChoice);
    }


}
