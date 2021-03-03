package de.fraunhofer.isst.dataspaceconnector.exceptions.handler;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;

/**
 * Thrown to indicate that the message could not be deserialized.
 */
public class MessageDeserializationException extends MessageException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageDeserializationException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageDeserializationException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MessageDeserializationException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageDeserializationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
