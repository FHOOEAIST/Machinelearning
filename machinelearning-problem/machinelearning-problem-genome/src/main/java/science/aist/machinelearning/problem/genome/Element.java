/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.genome;

import java.io.Serializable;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class Element implements Serializable {

    // value
    private char value;

    public Element(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + (value != '\u0000' ? value : "NULL") +
                '}';
    }
}
