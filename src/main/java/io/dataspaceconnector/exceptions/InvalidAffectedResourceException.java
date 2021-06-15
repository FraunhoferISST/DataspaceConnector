package io.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the affected resource of a ResourceUpdateMessage does not match the
 * resource in the payload.
 */
public class InvalidAffectedResourceException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an InvalidAffectedResourceException with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidAffectedResourceException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an InvalidAffectedResourceException with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the cause.
     */
    public InvalidAffectedResourceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
