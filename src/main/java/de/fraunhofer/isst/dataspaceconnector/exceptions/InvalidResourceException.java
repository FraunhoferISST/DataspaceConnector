package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class InvalidResourceException extends ResourceException {

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public InvalidResourceException(String msg) {
        super(msg);
    }

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public InvalidResourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Construct an InvalidResourceException with the specified detail cause.
     *
     * @param cause The cause.
     */
    public InvalidResourceException(Throwable cause) {
        super(cause);
    }
}
