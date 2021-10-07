package science.aist.machinelearning.analytics.space;

import java.time.format.DateTimeFormatter;

/**
 * Helper-class containing different dateTime-Formats. Usually used by analytics-classes.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class DateTimeFormats {

    /**
     * Returns formatter for date and time.
     *
     * @return formatter for dateTime
     */
    public static DateTimeFormatter getDateTimeFormat() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    }

    /**
     * Returns formatter for date only.
     *
     * @return formatter for date
     */
    public static DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ofPattern("yyyyMMdd");
    }
}
