package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown if a policy restriction has been detected.
 */
public class PolicyRestrictionException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without params.
     */
    public PolicyRestrictionException() { }

    /**
     * Construct a PolicyRestrictionException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public PolicyRestrictionException(final String msg) {
        super(msg);
    }

    /**
     * Construct a PolicyRestrictionException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public PolicyRestrictionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
