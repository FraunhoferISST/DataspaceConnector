package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class ResourceException extends RuntimeException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

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
}
