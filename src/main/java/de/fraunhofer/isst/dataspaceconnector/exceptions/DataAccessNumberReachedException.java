package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the data access has not been successfully reported.
 */
public class DataAccessNumberReachedException extends PolicyRestrictionException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without params.
     */
    public DataAccessNumberReachedException() { }

    /**
     * Construct a DataAccessNumberReachedException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public DataAccessNumberReachedException(final String msg) {
        super(msg);
    }

    /**
     * Construct a DataAccessNumberReachedException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public DataAccessNumberReachedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
