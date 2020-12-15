package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class ResourceException extends RuntimeException {

    /**
     * Construct a ResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceException(String msg) {
        super(msg);
    }

    /**
     * Construct a ResourceException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ResourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Construct a ResourceException with the specified detail cause.
     *
     * @param cause The cause.
     */
    public ResourceException(Throwable cause) {
        super(cause);
    }
}
