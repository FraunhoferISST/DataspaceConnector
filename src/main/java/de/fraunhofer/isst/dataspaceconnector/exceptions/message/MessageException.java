package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

public class MessageException extends RuntimeException {

    /**
     * Construct a MessageException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageException(String msg) {
        super(msg);
    }

    /**
     * Construct a MessageException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
