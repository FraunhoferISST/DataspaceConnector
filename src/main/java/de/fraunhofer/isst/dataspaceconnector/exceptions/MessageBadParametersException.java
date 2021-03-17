package de.fraunhofer.isst.dataspaceconnector.exceptions;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;

/**
 * Thrown to indicate that the message could not be build.
 */
public class MessageBadParametersException extends MessageException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageBadParametersException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public MessageBadParametersException(final String msg) {
        super(msg);
    }

    /**
     * Construct a MessageBadParametersException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public MessageBadParametersException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
