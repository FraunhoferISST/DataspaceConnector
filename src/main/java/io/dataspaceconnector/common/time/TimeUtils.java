package io.dataspaceconnector.common.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public final class TimeUtils {

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
