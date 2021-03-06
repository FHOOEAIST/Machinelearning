/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.experiment;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Identifier encapsulation for a specific experiment
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class ExperimentIdentifier {

    /**
     * human readable name, autogenerated from "identifier"
     */
    private final String name;

    /**
     * Identifier for groupings / comparisons of results
     */
    private final Map<String, Object> identifier;

    public ExperimentIdentifier(Map<String, Object> identifier) {
        this.identifier = identifier;
        this.name = identifier.entrySet().stream().map(x -> x.getKey() + ":" + (x.getValue() == null ? "null" : x.getValue().toString())).collect(Collectors.joining("_"));
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentIdentifier that = (ExperimentIdentifier) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
