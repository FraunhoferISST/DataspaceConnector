package io.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the affected resource is missing in a ResourceUpdateMessage.
 */
public class NoAffectedResourceException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NoAffectedResourceException with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoAffectedResourceException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a NoAffectedResourceException with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the cause.
     */
    public NoAffectedResourceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
