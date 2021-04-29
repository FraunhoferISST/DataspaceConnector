package de.fraunhofer.isst.dataspaceconnector.utils;

import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;

/**
 * This class provides methods to validate values.
 */
public final class ValidationUtils {

    /**
     * Utility class does not have to be instantiated.
     */
    private ValidationUtils() { }

    /**
     * Checks a given query input. If any of the keys or values in the headers or params maps are
     * null, blank, or empty, an  exception is thrown.
     *
     * @param queryInput the query input to validate.
     * @throws IllegalArgumentException if any of the keys or values are null, blank, or empty.
     */
    public static void validateQueryInput(final QueryInput queryInput) {

        // Validate headers from the query input, if they are present
        if (queryInput != null && queryInput.getHeaders() != null) {
            validateMapContent(queryInput.getHeaders());
        }

        // Validate query parameters from the query input, if they are present
        if (queryInput != null && queryInput.getParams() != null) {
            validateMapContent(queryInput.getParams());
        }

        // Validate path variables from the query input, if they are present
        if (queryInput != null && queryInput.getPathVariables() != null) {
            validateMapContent(queryInput.getPathVariables());
        }
    }

    /**
     * Iterates over all entries in a map and checks if key or value are null, empty or blank.
     *
     * @param map the map to validate
     * @throws IllegalArgumentException if any key or value in the map is null, emtpy or blank
     */
    private static void validateMapContent(final Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {

            // Check if key of the map entry is null, empty or blank
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new IllegalArgumentException("Map key in query input should not be "
                        + "null, blank or empty (key:" + entry.getKey()
                        + ", value: " + entry.getValue() + ").");
            }

            // Check if value of the map entry is null, empty or blank
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                throw new IllegalArgumentException("Map value in query input should not be "
                        + "null, blank or empty (key:" + entry.getKey()
                        + ", value: " + entry.getValue() + ").");
            }
        }
    }

}
