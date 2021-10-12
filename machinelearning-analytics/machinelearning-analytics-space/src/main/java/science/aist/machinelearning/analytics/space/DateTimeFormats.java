/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
