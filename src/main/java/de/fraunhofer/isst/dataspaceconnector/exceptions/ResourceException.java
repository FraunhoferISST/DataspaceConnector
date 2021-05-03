package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that this problem regarding a resource occurred.
 */
public class ResourceException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a ResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceException(final String msg) {
        super(msg);
    }
}
