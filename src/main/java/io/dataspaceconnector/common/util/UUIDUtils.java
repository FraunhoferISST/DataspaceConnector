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
package io.dataspaceconnector.common.util;

import io.dataspaceconnector.common.exception.UUIDCreationException;
import io.dataspaceconnector.common.exception.UUIDFormatException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * This class offers support functions for working with UUIDs.
 */
public final class UUIDUtils {

    /**
     * Default constructor.
     */
    private UUIDUtils() {
        // not used
    }

    /**
     * Finds all UUIDs in a string.
     *
     * @param input a string which maybe contains UUIDs.
     * @return the list of found UUIDs.
     */
    public static List<String> findUuids(final String input) {
        final var pairRegex = Pattern
                .compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit"
                        + "}{12}");
        final var matcher = pairRegex.matcher(input);

        // Extract all UUIDs
        final var output = new ArrayList<String>();
        while (matcher.find()) {
            output.add(matcher.group(0));
        }

        return output;
    }

    /**
     * Extracts a UUID from a URI. If more than one UUID is found the last UUID is returned. See
     * also {@link #uuidFromUri}.
     *
     * @param uri The URI from which the UUID should be extracted.
     * @return the extracted UUID.
     * @throws UUIDFormatException if the URI does not contain a parsable UUID.
     */
    public static UUID uuidFromUri(final URI uri) throws UUIDFormatException {
        try {
            return uuidFromUri(uri, -1);
        } catch (IndexOutOfBoundsException exception) {
            // Convert the exception to an expected format
            throw new UUIDFormatException("No uuid could be found in the uri.", exception);
        }
    }

    /**
     * Extracts a UUID from a URI at a given position.
     *
     * @param uri   the URI from which the UUID should be extracted.
     * @param index the index when more then one UUID is found. Set to a negative number when the
     *              last UUID should be extracted.
     * @return the extracted uuid.
     * @throws UUIDFormatException       if the URI does not contain a parsable UUID.
     * @throws IndexOutOfBoundsException if no UUID can be found at the given index.
     */
    public static UUID uuidFromUri(final URI uri, final int index) throws UUIDFormatException,
            IndexOutOfBoundsException {
        // Find all uuids in the uri
        final var uuids = findUuids(uri.toString());

        // Get only the uuid needed
        final var stringUuid = uuids.get(index < 0 ? uuids.size() - 1 : index);

        // Convert the string to uuid element
        try {
            return UUID.fromString(stringUuid);
        } catch (IllegalArgumentException exception) {
            // This exception should never be thrown since the pattern matcher (in splitUuids)
            // found the uuid.
            throw new UUIDFormatException("Could not convert string to uuid. This indicates a "
                    + "problem with the uuid pattern.", exception);
        }
    }

    /**
     * Generates a unique UUID, if it does not already exist.
     *
     * @param doesUuidExistFunc a function checking if a given UUID already exists
     * @return generated UUID
     * @throws UUIDCreationException if no unique UUID could be generated
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public static UUID createUUID(final Function<UUID, Boolean> doesUuidExistFunc)
            throws UUIDCreationException {
        final var maxNumTries = 32;
        return createUUID(doesUuidExistFunc, maxNumTries);
    }

    /**
     * Tries to generate a unique UUID, if it does not already exist, in a given number of tries.
     *
     * @param doesUuidExistFunc a function checking if a given UUID already exists
     * @param maxNumTries       a maximum number of retries for generating the UUID
     * @return generated UUID
     * @throws UUIDCreationException if no unique UUID could be generated
     */
    public static UUID createUUID(final Function<UUID, Boolean> doesUuidExistFunc,
                                  final long maxNumTries)
            throws IllegalArgumentException, UUIDCreationException {
        if (maxNumTries == 0) {
            throw new IllegalArgumentException("The maximum number of tries must be at least 1.");
        }

        long numTries = 0;
        while (numTries < maxNumTries) {
            final var uuid = UUID.randomUUID();

            // Check if the created uuid already exists
            if (!doesUuidExistFunc.apply(uuid)) {
                // It does not, the generated uuid is new
                return uuid;
            }

            numTries++;
        }

        throw new UUIDCreationException("Could not create a new uuid. No unused uuid could be "
                + "found.");
    }
}
