package de.fraunhofer.isst.dataspaceconnector.exceptions;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;

/**
 * Thrown if a policy restriction has been detected.
 */
public class PolicyRestrictionException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a PolicyRestrictionException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public PolicyRestrictionException(final ErrorMessages msg) {
        super(msg.toString());
    }

    /**
     * Construct a PolicyRestrictionException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public PolicyRestrictionException(final ErrorMessages msg, final Throwable cause) {
        super(msg.toString(), cause);
    }
}
