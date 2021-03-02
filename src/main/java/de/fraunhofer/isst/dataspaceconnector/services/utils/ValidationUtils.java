package de.fraunhofer.isst.dataspaceconnector.services.utils;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;

import java.util.Map;

/**
 * This class provides methods to validate values.
 */
public final class ValidationUtils {

    /**
     * Checks a given query input. If any of the keys or values in the headers or params maps are
     * null, blank, or empty, an  exception is thrown.
     *
     * @param queryInput the query input to validate.
     * @throws IllegalArgumentException if any of the keys or values are null, blank, or empty.
     */
    public static void validateQueryInput(QueryInput queryInput) {
        if (queryInput != null && queryInput.getHeaders() != null) {
            for (Map.Entry<String, String> entry : queryInput.getHeaders().entrySet()) {
                if (entry.getKey() == null || entry.getKey().trim().isEmpty()
                        || entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                    throw new IllegalArgumentException("Header key or value should not be null, blank or empty " +
                            "(key:" + entry.getKey() + ", value: " + entry.getValue() + ").");
                }
            }
        }
        if (queryInput != null && queryInput.getParams() != null) {
            for (Map.Entry<String, String> entry : queryInput.getParams().entrySet()) {
                if (entry.getKey() == null || entry.getKey().trim().isEmpty()
                        || entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                    throw new IllegalArgumentException("Param key or value should not be null, blank or empty.");
                }
            }
        }

        if (queryInput != null && queryInput.getPathVariables() != null) {
            for (Map.Entry<String, String> entry : queryInput.getPathVariables().entrySet()) {
                if (entry.getKey() == null || entry.getKey().trim().isEmpty()
                        || entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                    throw new IllegalArgumentException("Path variables key or value should not be null, blank or empty.");
                }
            }
        }
    }
}
