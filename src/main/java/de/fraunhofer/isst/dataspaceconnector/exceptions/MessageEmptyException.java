package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the message could not be build.
 */
public class MessageEmptyException extends MessageRequestException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageEmptyException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public MessageEmptyException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MessageEmptyException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageEmptyException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
