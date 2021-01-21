package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

/**
 * Thrown to indicate that a problem with a message response occurred.
 */
public class MessageResponseException extends MessageException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageResponseException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageResponseException(String msg) {
        super(msg);
    }

    /**
     * Construct a MessageResponseException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageResponseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
