/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.fitness;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.io.Serializable;

/**
 * Partial Quality of a Solution. The sum of all cachets in a solution makes the total quality
 *
 * @author Oliver Krauss
 * @since 1.0
 */
@NodeEntity
public class Cachet implements Serializable {

    @Id
    private Long id;

    /**
     * Quality between 0 and infinity
     */
    private double quality;

    /**
     * human readable name of the cachet
     */
    private String name;

    public Cachet() {
    }

    public Cachet(double quality, String name) {
        this.quality = quality;
        this.name = name;
    }

    public double getQuality() {
        return quality;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
