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
     * The passed entity id may not be null.
     */
    ENTITYID_NULL("The entity id may not be null."),

    /**
     * The passed pageable parameter may not be null.
     */
    PAGEABLE_NULL("The pageable parameter may not be null"),

    /**
     * The passed set of entities may not be null.
     */
    ENTITYSET_NULL("The set of entities may not be null"),

    /**
     * Rule is missing a target parameter.
     */
    MISSING_TARGET("Missing target id in rules"),

    /**
     * The response message type is not as expected.
     */
    UNEXPECTED_RESPONSE_TYPE("Unexpected ids response type."),

    /**
     * The passed list may not be null.
     */
    LIST_NULL("The passed list may not be null."),

    /**
     * One of the contracts is empty.
     */
    EMPTY_CONTRACT("Empty contracts cannot be compared."),

    /**
     * If the content of two contracts is not equal.
     */
    CONTRACT_MISMATCH("The contract's content do not match."),

    INVALID_RESPONSE("Invalid ids response message."),

    HEADER_BUILD_FAILED("Ids message header could not be built."),

    INVALID_RESPONSE_DAT("Invalid DAT in incoming message."),

    MESSAGE_NOT_SENT("Message could not be sent."),

    MISSING_RESPONSE_HEADER("Missing response header."),

    MALFORMED_RESPONSE_HEADER("Malformed response header."),

    MALFORMED_RESPONSE_PAYLOAD("Malformed response payload."),

    EMTPY_ENTITY("Element could not be found."),

    RDF_FAILED("Could not retrieve rdf string.");

    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param value The msg of the error message.
     */
    ErrorMessages(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
