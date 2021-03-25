package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the message response type was not expected.
 */
public class UnexpectedResponseType extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a UnexpectedMessageType with the specified detail message.
     *
     * @param msg The detail message.
     */
    public UnexpectedResponseType(final String msg) {
        super(msg);
    }

    /**
     * Construct a UnexpectedMessageType with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public UnexpectedResponseType(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
