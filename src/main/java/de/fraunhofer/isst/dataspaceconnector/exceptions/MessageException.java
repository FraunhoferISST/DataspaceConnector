package de.fraunhofer.isst.dataspaceconnector.exceptions;

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

    /**
     * Construct a MessageException with the specified detail cause.
     *
     * @param cause The cause.
     */
    public MessageException(Throwable cause) {
        super(cause);
    }
}
