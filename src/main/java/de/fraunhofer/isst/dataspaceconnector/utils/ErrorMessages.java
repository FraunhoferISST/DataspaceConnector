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
    MISSING_TARGET("Missing target id in rules");

    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
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
