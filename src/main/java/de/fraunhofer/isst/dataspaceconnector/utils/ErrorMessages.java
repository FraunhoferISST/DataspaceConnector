package de.fraunhofer.isst.dataspaceconnector.utils;

/**
 * Contains a list of common error messages used by exceptions.
 */
public enum ErrorMessages {
    /**
     * The passed desc parameter may not be null.
     */
    DESC_NULL("The description parameter may not be null."),

    /**
     * The passed message may not be null.
     */
    MESSAGE_NULL("The message may not be null."),

    /**
     * The passed contract may not be null.
     */
    CONTRACT_NULL("The contract may not be null."),

    /**
     * The passed entity id may not be null.
     */
    ENTITYID_NULL("The entity id may not be null."),

    /**
     * The passed entity may not be null.
     */
    ENTITY_NULL("The entity may not be null."),

    /**
     * The passed pageable parameter may not be null.
     */
    PAGEABLE_NULL("The pageable parameter may not be null."),

    /**
     * The passed set of entities may not be null.
     */
    ENTITYSET_NULL("The set of entities may not be null."),

    /**
     * The exception parameter may not be null.
     */
    EXCEPTION_NULL("The exception parameter may not be null."),

    /**
     * Missing IDS version.
     */
    VERSION_NULL("The version must be set."),

    /**
     * Rule is missing a target parameter.
     */
    MISSING_TARGET("Missing target id in rules"),

    /**
     * The passed list may not be null.
     */
    LIST_NULL("The passed list may not be null."),

    /**
     * The passed uri may note be null.
     */
    URI_NULL("The passed uri may not be null"),

    /**
     * One of the contracts is empty.
     */
    EMPTY_CONTRACT("Empty contracts cannot be compared."),

    /**
     * If the content of two contracts is not equal.
     */
    CONTRACT_MISMATCH("The contract's content do not match."),

    /**
     * No behavior has been defined for the object type.
     */
    UNKNOWN_TYPE("No behavior has been defined for this type."),

    /**
     * Failed to read response message.
     */
    INVALID_RESPONSE("Invalid ids response message."),

    /**
     * Ids message building failed.
     */
    HEADER_BUILD_FAILED("Ids message header could not be built."),

    /**
     * Multipart message building failed.
     */
    MESSAGE_BUILD_FAILED("Multipart message could not be built."),

    /**
     * DAT in response ids header is invalid.
     */
    INVALID_RESPONSE_DAT("Invalid DAT in incoming message."),

    /**
     * Multipart message could not be sent.
     */
    MESSAGE_NOT_SENT("Message could not be sent."),

    /**
     * Header of multipart message is malformed.
     */
    MALFORMED_HEADER("Malformed message header."),

    /**
     * Payload of multipart message is malformed.
     */
    MALFORMED_PAYLOAD("Malformed message payload."),

    /**
     * Payload of multipart message is missing.
     */
    MISSING_PAYLOAD("Missing message payload."),

    /**
     * Entity is null.
     */
    EMTPY_ENTITY("Element could not be found."),

    /**
     * Failed to read rdf string from ids object.
     */
    RDF_FAILED("Could not retrieve rdf string."),

    /**
     * Number of data accesses has been reached.
     */
    DATA_ACCESS_NUMBER_REACHED("Valid access number reached."),

    /**
     * Data has been accessed in an invalid time interval.
     */
    DATA_ACCESS_INVALID_INTERVAL("Data access in invalid time interval."),

    /**
     * Data has been accessed by an invalid consumer.
     */
    DATA_ACCESS_INVALID_CONSUMER("Data access by invalid consumer connector."),

    /**
     * Application's base URL was retrieved without request context present.
     */
    NO_REQUEST_CONTEXT("No request context present for extracting base URL."),

    /**
     * Due to a prohibit access pattern, accessing the data is not allowed.
     */
    NOT_ALLOWED("Access is not allowed."),

    /**
     * A policy restriction has been detected.
     */
    POLICY_RESTRICTION("Policy restriction detected.");


    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param message The msg of the error message.
     */
    ErrorMessages(final String message) {
        this.value = message;
    }

    @Override
    public String toString() {
        return value;
    }
}
