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
