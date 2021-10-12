/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core;

import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;

/**
 * An abstract genome of a Type T
 *
 * @param <T> Type that the genome has
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class Gene<T> implements Serializable {

    @Relationship(type = "RWGENE", direction = "OUTGOING")
    protected T gene;

    public T getGene() {
        return gene;
    }

    public void setGene(T gene) {
        this.gene = gene;
    }
}
