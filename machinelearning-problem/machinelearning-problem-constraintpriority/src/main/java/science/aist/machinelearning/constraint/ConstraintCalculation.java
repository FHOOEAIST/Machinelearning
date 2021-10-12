/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.constraint;

import java.io.Serializable;

/**
 * Interface for creating calculations on the type CT. Will return a value of the type RT.
 *
 * @param <RT> Return type
 * @param <CT> Constraint type
 * @author Oliver Krauss
 * @since 1.0
 */
public interface ConstraintCalculation<RT, CT> extends Serializable {

    /**
     * Calculates value for the given object.
     *
     * @param object object to calculate a value for.
     * @return calculated value
     */
    RT calculate(CT object);
}
