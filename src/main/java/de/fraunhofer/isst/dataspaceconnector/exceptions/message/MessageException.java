package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

/**
 * Thrown to indicate that a problem regarding a message occurred.
 */
public class MessageException extends RuntimeException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

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
