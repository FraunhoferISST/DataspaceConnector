package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDCreationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * This class offers support functions for working with uuids.
 */
public class UUIDUtils {
    /**
     * Finds all uuids in a string.
     *
     * @param input The string which maybe contains uuids.
     * @return The list of found uuids.
     */
    public static List<String> findUuids(@NotNull String input) {
        final var pairRegex = Pattern.compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");
        final var matcher = pairRegex.matcher(input);

        // Extract all uuids
        var output = new ArrayList<String>();
        while (matcher.find())
            output.add(matcher.group(0));

        return output;
    }

    /**
     * Extracts an uuid from an uri. If more then one uuid are found the last uuid is returned.
     * See also {@link #uuidFromUri}.
     *
     * @param uri The uri from which the uuid should be extracted.
     * @return the extracted uuid.
     * @throws UUIDFormatException - if the uri does not contain a parsable uuid.
     */
    public static UUID uuidFromUri(@NotNull URI uri) throws UUIDFormatException {
        try {
            return uuidFromUri(uri, -1);
        } catch (IndexOutOfBoundsException exception) {
            // Convert the exception to an expected format
            throw new UUIDFormatException("No uuid could be found in the uri.", exception);
        }
    }

    /**
     * Extracts an uuid from an uri at a given position.
     *
     * @param uri   The uri from which the uuid should be extracted.
     * @param index The index when more then one uuid is found. Set to a negative number when the
     *              last uuid should be extracted.
     * @return the extracted uuid.
     * @throws UUIDFormatException       - if the uri does not contain a parsable uuid.
     * @throws IndexOutOfBoundsException - if no uuid can be found at the given index.
     */
    public static UUID uuidFromUri(@NotNull URI uri, int index) throws UUIDFormatException,
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
            throw new UUIDFormatException("Could not convert string to uuid. This indicates a " +
                    "problem with the uuid pattern.", exception);
        }
    }

    /**
     * Generates a unique uuid, if it does not already exist.
     *
     * @param doesUuidExistFunc A function checking if a given uuid already exists
     * @return Generated uuid
     * @throws UUIDCreationException - if no unique uuid could be generated
     */
    public static UUID createUUID(Function<UUID, Boolean> doesUuidExistFunc) throws UUIDCreationException{
        return createUUID(doesUuidExistFunc, 32);
    }

    /**
     * Generates a unique uuid, if it does not already exist.
     *
     * @param doesUuidExistFunc A function checking if a given uuid already exists
     * @param maxNumTries A maximum number of retries for generating the uuid
     * @return Generated uuid
     * @throws UUIDCreationException - if no unique uuid could be generated
     */
    public static UUID createUUID(Function<UUID, Boolean> doesUuidExistFunc, long maxNumTries) throws IllegalArgumentException, UUIDCreationException{
        if(maxNumTries == 0)
            throw new IllegalArgumentException("The maximum number of tries must be at least 1.");

        long numTries = 0;
        while(numTries < maxNumTries) {
            final var uuid = UUID.randomUUID();

            // Check if the created uuid already exists
            if(!doesUuidExistFunc.apply(uuid)) {
                // It does not, the generated uuid is new
                return uuid;
            }

            numTries++;
        }

        throw new UUIDCreationException("Could not create a new uuid. No unused uuid could be " +
                "found.");
    }
}
