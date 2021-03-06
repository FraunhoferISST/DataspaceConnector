package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the data access has not been successfully logged.
 */
public class DataAccessInvalidTimeIntervalException extends PolicyRestrictionException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without params.
     */
    public DataAccessInvalidTimeIntervalException() { }

    /**
     * Construct a DataAccessInvalidTimeIntervalException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public DataAccessInvalidTimeIntervalException(final String msg) {
        super(msg);
    }

    /**
     * Construct a DataAccessInvalidTimeIntervalException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public DataAccessInvalidTimeIntervalException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
