/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example.mockup;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class Node {
    private final String name;

    private boolean special = false;

    private double beeLine;

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, double beeLine) {
        this.name = name;
        this.beeLine = beeLine;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    @Override
    public String toString() {
        return name;
    }

    public Double getBeeLine() {
        return beeLine;
    }
}
