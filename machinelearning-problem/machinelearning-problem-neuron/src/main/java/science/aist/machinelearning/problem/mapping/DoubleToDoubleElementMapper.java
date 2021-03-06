/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.problem.mapping;

import science.aist.machinelearning.problem.DoubleElement;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Maps double to DoubleElement
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class DoubleToDoubleElementMapper implements Function<Double, DoubleElement>, Serializable {
    @Override
    public DoubleElement apply(Double aDouble) {
        return new DoubleElement(aDouble);
    }
}
