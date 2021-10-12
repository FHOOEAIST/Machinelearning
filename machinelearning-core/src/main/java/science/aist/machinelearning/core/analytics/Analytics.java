/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.analytics;


import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;

import java.util.List;

/**
 * Analytics tool for an {@link Algorithm}
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public interface Analytics {

    /**
     * Starts the analytics for an {@link Algorithm}
     */
    void startAnalytics();

    /**
     * Logs one parameter of an {@link Algorithm}
     *
     * @param name  of the paramter
     * @param value of the parameter
     */
    void logParam(String name, String value);

    /**
     * Logs the names of the algorithm data which can be given in {@link #logAlgorithmStep}
     *
     * @param names the names
     */
    void logAlgorithmStepHeaders(List<String> names);

    /**
     * Logs one step of the algorithm. In each step the stepnumber + current time is appended automatically!
     *
     * @param values the values
     */
    void logAlgorithmStep(List<String> values);

    /**
     * Logs the problem that should be solved by the algorithm
     *
     * @param problem given to be solved
     * @param <PT>    Class of the problem
     */
    <PT> void logProblem(Problem<PT> problem);

    /**
     * Logs a mapping that was found by the algorithm for a given problem
     *
     * @param solution found
     * @param <GT>     Class of the mapping
     * @param <PT>     Class of the mapping
     */
    <GT, PT> void logSolution(Solution<GT, PT> solution);

    /**
     * Finishes the analytics for an {@link Algorithm}
     */
    void finishAnalytics();

}
