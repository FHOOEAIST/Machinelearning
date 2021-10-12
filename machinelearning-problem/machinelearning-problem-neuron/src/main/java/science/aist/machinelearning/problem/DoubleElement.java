/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem;

import java.io.Serializable;

/**
 * Container for a double value
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class DoubleElement implements Serializable {

    // value
    private double value;

    public DoubleElement(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + (value != '\u0000' ? value : "NULL") +
                '}';
    }
}