/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.common.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * Utilities for time operations.
 */
public final class TimeUtils {

    /**
     * Utility class does not have to be instantiated.
     */
    private TimeUtils() {
    }

    /**
     * Convert a string to a {@link ZonedDateTime}.
     *
     * @param calendar The time as string.
     * @return The new ZonedDateTime object.
     * @throws DateTimeParseException if its not a time.
     */
    public static ZonedDateTime getDateOf(final String calendar) throws DateTimeParseException {
        return ZonedDateTime.parse(calendar);
    }
}
