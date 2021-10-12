/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp;

/**
 * Interface for nodes that contain a certain value of something.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface ValueContainer<T> {

    T getValue();

    void setValue(T value);
}
