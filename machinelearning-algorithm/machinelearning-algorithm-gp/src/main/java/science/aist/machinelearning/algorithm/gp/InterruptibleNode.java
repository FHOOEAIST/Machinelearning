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
 * Interface for the creation of nodes that can be interrupted for calculation. Very useful for interrupting very time
 * consuming nodes (big for-loops or infinite loops).
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface InterruptibleNode {

    /**
     * Tries to stop the node from the current execution-method.
     *
     * @param value true = interrupt the node, false = don't interrupt or stop interruption
     */
    void interrupt(boolean value);
}
