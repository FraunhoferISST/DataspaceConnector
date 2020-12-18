package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class InvalidResourceException extends ResourceException {

    /**
     * Construct a InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public InvalidResourceException(String msg) {
        super(msg);
    }

    /**
     * Construct a InvalidResourceException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public InvalidResourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Construct a InvalidResourceException with the specified detail cause.
     *
     * @param cause The cause.
     */
    public InvalidResourceException(Throwable cause) {
        super(cause);
    }
}
