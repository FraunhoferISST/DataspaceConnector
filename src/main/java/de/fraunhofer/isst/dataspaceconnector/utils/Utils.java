package de.fraunhofer.isst.dataspaceconnector.utils;

import lombok.experimental.UtilityClass;

/**
 * This utility class contains general purpose functions.
 */
@UtilityClass
public class Utils {

    /**
     * Check if an object is not null.
     * @param obj The object to check.
     * @param message The error message transmitted when the object is null.
     * @param <T> The type of the passed object.
     * @throws IllegalArgumentException if the passed object is null.
     * @return The passed object.
     */
    public static <T> T requireNonNull(final T obj, final ErrorMessages message) {
        if (obj == null) {
            throw new IllegalArgumentException(message.toString());
        }

        return obj;
    }
}
