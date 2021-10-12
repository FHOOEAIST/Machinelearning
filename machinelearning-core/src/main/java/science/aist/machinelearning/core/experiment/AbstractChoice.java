/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

import science.aist.machinelearning.core.options.Descriptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for all choices taking care of the generic features
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public abstract class AbstractChoice<O> implements Choice<O> {

    /**
     * Name of choice. Either user given (ex. "Algorithm") or defined by the Configurable this is for
     */
    private final String name;

    /**
     * Parent object. Supposed to be set when a "child" object is set
     */
    protected AbstractChoice parent;

    public AbstractChoice(String name) {
        this.name = name;
    }

    @Override
    public ExperimentIdentifier getCurrentIdentifier() {
        Map<String, Object> identifierMap = new HashMap<>();

        if (this.getChildren() != null) {
            this.getChildren().stream().map(Choice::getCurrentIdentifier).forEach(x -> x.getIdentifier().forEach((key, value) -> identifierMap.put(this.getName() + "." + key, value)));
        } else {
            if (this.current() instanceof Descriptor) {
                identifierMap.put(this.name, ((Descriptor) this.current()).getValue());
            } else {
                identifierMap.put(this.name, this.current());
            }
        }

        return new ExperimentIdentifier(identifierMap);
    }

    @Override
    public Choice findConfig(String name) {
        if (this.name.equals(name)) {
            return this;
        }

        if (name.startsWith(this.name + ".")) {
            name = name.substring(this.name.length() + 1);
            String finalName = name;
            return getChildren().stream().map(x -> x.findConfig(finalName)).filter(Objects::nonNull).findFirst().orElse(null);
        }

        return null;
    }


    // convenience override for final choices. Choices that have children MUST OVERRIDE THIS IMPL
    @Override
    public Collection<Choice> getChildren() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Choice getParent() {
        return parent;
    }

    @Override
    public void setParent(Choice parent) {
        // AbstractChoice only plays nice with itself. IF you don't implement it this will fail :P
        this.parent = (AbstractChoice) parent;
    }

    @Override
    public void replace(Choice c) {
        if (this.parent != null && c != null && this.name.equals(c.getName()) && parent.getChildren() != null) {
            parent.replaceChild(this, c);
        }
    }

    /**
     * Helper function for replacing children. ALL classes with child objects MUST override this The same classes should
     * set the PARENT object of children when they are added.
     *
     * @param oldChoice choice to be replaced
     * @param newChoice choice that oldChoice will be replaced with
     */
    protected void replaceChild(Choice<O> oldChoice, Choice<O> newChoice) {
        // This error only happens if someone forgot to override this method -> replace actually prevents childless classes to get this error
        throw new UnsupportedOperationException("replaceChild should have been overridden by a specific implementation of AbstractCoice");
    }


}
