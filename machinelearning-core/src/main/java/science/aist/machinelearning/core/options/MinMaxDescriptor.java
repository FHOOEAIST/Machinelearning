/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.options;

/**
 * Used for defining primitive settings. Can contain either a fixed value or min/max-settings.
 * <p>
 * Very useful for defining mutation settings when optimizing algorithms.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class MinMaxDescriptor<T> extends Descriptor<T> {

    /**
     * Minimum value the descriptor can be.
     */
    private final T min;

    /**
     * Maximum value the descriptor can be.
     */
    private final T max;

    public MinMaxDescriptor(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
