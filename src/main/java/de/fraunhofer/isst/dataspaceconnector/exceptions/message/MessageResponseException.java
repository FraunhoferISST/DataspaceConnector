package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

public class MessageResponseException extends MessageException {

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
