package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem occurred while creating an uuid.
 */
public class InvalidDynamicAttributeTokenException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a InvalidDynamicAttributeTokenException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public InvalidDynamicAttributeTokenException(final String msg) {
        super(msg);
    }
}
