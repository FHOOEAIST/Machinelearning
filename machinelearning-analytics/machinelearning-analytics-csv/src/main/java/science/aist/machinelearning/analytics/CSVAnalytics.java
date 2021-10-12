/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.analytics;


import org.apache.log4j.Logger;
import science.aist.machinelearning.analytics.space.DateTimeFormats;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.analytics.Analytics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of {@link Analytics} producing a CSV file. WARNING: the CSV Analytics tool is not threadsafe! Use only
 * for one algorithm-run at a time!!!
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class CSVAnalytics implements Analytics {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(CSVAnalytics.class);
    private final DateTimeFormatter formatter = DateTimeFormats.getDateTimeFormat();
    /**
     * path to where the csv files will be stored. If not set the execution path will be used!
     */
    private String path;
    /**
     * File that is currently being written to
     */
    private PrintWriter file;
    /**
     * Amount of steps that was taken during the run
     */
    private int steps = 0;

    @Override
    public void startAnalytics() {
        try {
            String filename = (path != null ? path : "") + "AlgRun_" + LocalDateTime.now().format(formatter) + ".csv";
            File f = new File(filename);
            int i = 1;
            while (f.exists()) {
                f = new File(filename.substring(0, filename.length() - 4) + '_' + i + filename.substring(filename.length() - 4));
                i++;
            }
            f.createNewFile();
            file = new PrintWriter(f, StandardCharsets.UTF_8);
            file.println("start;" + LocalDateTime.now());
            steps = 0;
            logger.info("Created new file for analytics: " + f.getAbsolutePath());
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to create file due to mapping error", e);
            finishAnalytics();
        } catch (IOException e) {
            logger.error("Could not create file", e);
            finishAnalytics();
        }
    }

    @Override
    public void logParam(String name, String value) {
        file.println(name + ";" + value);
    }

    @Override
    public void logAlgorithmStepHeaders(List<String> names) {
        if (names == null || names.isEmpty()) {
            return;
        }

        StringBuilder line = new StringBuilder("step;time;");
        for (String header : names) {
            line.append(header).append(";");
        }
        line = new StringBuilder(line.substring(0, line.length() - 1));
        file.println(line);
    }

    @Override
    public void logAlgorithmStep(List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        StringBuilder line = new StringBuilder(steps + ";" + LocalDateTime.now() + ";");
        for (String header : values) {
            line.append(header).append(";");
        }
        line = new StringBuilder(line.substring(0, line.length() - 1));
        file.println(line);
        steps++;
    }

    @Override
    public <P> void logProblem(Problem<P> problem) {
        file.println("Problem: ");

        StringBuilder text = new StringBuilder("[");
        for (ProblemGene<P> gene : problem.getProblemGenes()) {
            text.append(gene.toString()).append(", ");
        }
        text.append("]");

        file.println(text);
    }

    @Override
    public <GT, P> void logSolution(Solution<GT, P> solution) {
        file.println("Solution: ");
        file.println(solution.toHumanReadableString());
    }

    @Override
    public void finishAnalytics() {
        logger.info("Saved file for analytics");
        file.flush();
        file.close();
        file = null;
    }

    /**
     * Setter for dependency injection.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
