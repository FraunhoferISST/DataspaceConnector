package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem regarding a message occurred.
 */
public class InvalidInputException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a InvalidInputException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public InvalidInputException(final String msg) {
        super(msg);
    }

    /**
     * Construct a InvalidInputException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public InvalidInputException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
