package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the requested resource could not be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a ResourceNotFoundException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceNotFoundException(final String msg) {
        super(msg);
    }

    /**
     * Construct a ResourceNotFoundException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ResourceNotFoundException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
