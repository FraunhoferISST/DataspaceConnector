package de.fraunhofer.isst.dataspaceconnector.exceptions.handler;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;

/**
 * Thrown to indicate that the message response could not be built.
 */
public class MessageResponseBuilderException extends MessageResponseException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageResponseBuilderException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageResponseBuilderException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MessageResponseBuilderException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageResponseBuilderException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
