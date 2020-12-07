package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class ResourceAlreadyExists extends ResourceException {

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceAlreadyExists(String msg) {
        super(msg);
    }

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ResourceAlreadyExists(String msg, Throwable cause) {
        super(msg, cause);
    }
}