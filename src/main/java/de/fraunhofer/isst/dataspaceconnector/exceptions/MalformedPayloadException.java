package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the message could not be build.
 */
public class MalformedPayloadException extends MessageException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MalformedPayloadException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public MalformedPayloadException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MalformedPayloadException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MalformedPayloadException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
