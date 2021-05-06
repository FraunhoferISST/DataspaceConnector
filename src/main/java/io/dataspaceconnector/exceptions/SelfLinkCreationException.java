package io.dataspaceconnector.exceptions;

import io.dataspaceconnector.utils.ErrorMessages;

/**
 * Thrown to indicate that the self link for an entity could not be created.
 */
public class SelfLinkCreationException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a SelfLinkCreationException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public SelfLinkCreationException(final ErrorMessages msg) {
        super(msg.toString());
    }
}
