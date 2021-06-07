/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.utils;

import java.util.Map;

import io.dataspaceconnector.model.QueryInput;

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
        for (final var entry : map.entrySet()) {
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
