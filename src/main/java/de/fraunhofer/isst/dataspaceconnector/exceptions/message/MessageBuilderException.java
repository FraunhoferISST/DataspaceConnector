package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

/**
 * Thrown to indicate that the message could not be build.
 */
public class MessageBuilderException extends MessageException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageBuilderException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageBuilderException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
