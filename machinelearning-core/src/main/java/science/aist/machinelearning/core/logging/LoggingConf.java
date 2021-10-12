/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.logging;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Necessary to set logging level for all loggers that can't be accessed via properties-file.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class LoggingConf {

    /**
     * Checks which level the rootLogger has defined in properties. Takes this level and sets the other logger to this
     * level too.
     */
    public static void setLoggingToRootLevel() {
        Level level = LogManager.getRootLogger().getLevel();
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        for (Logger logger : loggers) {
            logger.setLevel(level);
        }
    }

    /**
     * Sets all the loggers to the given level.
     *
     * @param level level to set the loggers to
     */
    public static void setLoggingToLevel(Level level) {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(level);
        }
    }
}
