package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

/**
 * Thrown to indicate that the message could not be send.
 */
public class MessageNotSentException extends MessageException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageNotSentException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageNotSentException(String msg) {
        super(msg);
    }

    /**
     * Construct a MessageNotSentException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageNotSentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
