package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the message could not be build.
 */
public class MissingPayloadException extends MessageException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MissingPayloadException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public MissingPayloadException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MissingPayloadException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MissingPayloadException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
