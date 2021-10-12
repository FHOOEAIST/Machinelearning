/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp;

import science.aist.machinelearning.core.options.Descriptor;

import java.io.Serializable;
import java.util.Map;

/**
 * Basic abstract interface for the creation of GP-GraphNodes.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface GPGraphNode<T> extends Serializable {

    /**
     * Method that executes the node and returns the result of this graph or subGraph.
     *
     * @return result of the execution of this node and following children
     */
    T execute();

    /**
     * Returns a simple object of the same class returned by the execute-method. Required for later instanceof-checks.
     *
     * @return class of the execute-method.
     */
    T simpleReturnType();

    /**
     * Get the settings for this graph
     *
     * @return map of names and descriptors
     */
    Map<String, Descriptor> getOptions();

    /**
     * Set settings for this graph using a map of strings and descriptors.
     *
     * @param options options to set
     * @return true = setting the option was successful, false = failed to set the options
     */
    boolean setOptions(Map<String, Descriptor> options);

    /**
     * Set a single descriptor for this graph
     *
     * @param name       name of the parameter
     * @param descriptor descriptor to set
     * @return true = setting the option was successful, false = failed to set the options
     */
    boolean setOption(String name, Descriptor descriptor);
}
