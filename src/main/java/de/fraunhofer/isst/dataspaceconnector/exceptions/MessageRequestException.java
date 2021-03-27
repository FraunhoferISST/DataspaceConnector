package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem with a message request occurred.
 */
public class MessageRequestException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageRequestException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageRequestException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MessageRequestException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageRequestException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
