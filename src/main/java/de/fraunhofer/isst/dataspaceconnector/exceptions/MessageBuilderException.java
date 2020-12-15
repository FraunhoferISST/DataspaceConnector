package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class MessageBuilderException extends MessageException {

    /**
     * Construct a MessageBuilderException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageBuilderException(String msg) {
        super(msg);
    }

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
