package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the message could not be build.
 */
public class MalformedHeaderException extends MessageResponseException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MalformedHeaderException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public MalformedHeaderException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MalformedHeaderException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MalformedHeaderException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
