package de.fraunhofer.isst.dataspaceconnector.exceptions.handled;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;

/**
 * Thrown to indicate that the message could not be deserialized.
 */
public class MessageDeserializationException extends MessageResponseException {
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
