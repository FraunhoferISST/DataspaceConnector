package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown if a policy action could not be executed.
 */
public class PolicyExecutionException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without params.
     */
    public PolicyExecutionException() { }

    /**
     * Construct a PolicyExecutionException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public PolicyExecutionException(final String msg) {
        super(msg);
    }

    /**
     * Construct a PolicyExecutionException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public PolicyExecutionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
