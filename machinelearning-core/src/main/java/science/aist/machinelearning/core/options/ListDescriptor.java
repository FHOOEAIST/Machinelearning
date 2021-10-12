/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.options;

import java.util.List;

/**
 * Used for defining complex classes for the options. Can contain either a fixed value or a list of possible values.
 * <p>
 * Very useful for defining mutation settings when optimizing algorithms.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ListDescriptor<T> extends Descriptor<T> {

    /**
     * List of possible values for this descriptor.
     */
    private final List<T> valueList;

    public ListDescriptor(List<T> valueList) {
        this.valueList = valueList;
    }

    public List<T> getValueList() {
        return valueList;
    }
}
