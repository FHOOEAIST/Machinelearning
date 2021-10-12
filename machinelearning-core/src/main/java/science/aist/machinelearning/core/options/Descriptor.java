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
 * Contains data for the options of algorithms.
 * <p>
 * Used for defining primitive settings. Can contain either a fixed value or min/max-settings.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class Descriptor<T> {

    /**
     * Fixed value for this descriptor.
     */
    private T value;

    public Descriptor() {
    }

    public Descriptor(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
