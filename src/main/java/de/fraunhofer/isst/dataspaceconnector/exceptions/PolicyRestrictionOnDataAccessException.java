package de.fraunhofer.isst.dataspaceconnector.exceptions;

import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;

/**
 * Thrown to indicate that the data access has not been successfully logged.
 */
public class PolicyRestrictionOnDataAccessException extends PolicyRestrictionException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without params.
     */
    public PolicyRestrictionOnDataAccessException() { }

    /**
     * Construct a PolicyRestrictionOnDataAccessException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public PolicyRestrictionOnDataAccessException(final String msg) {
        super(msg);
    }

    /**
     * Construct a PolicyRestrictionOnDataAccessException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public PolicyRestrictionOnDataAccessException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
