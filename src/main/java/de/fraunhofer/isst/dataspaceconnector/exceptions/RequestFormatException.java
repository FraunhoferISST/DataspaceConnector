package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the request format did not match expectations.
 */
public class RequestFormatException extends IllegalArgumentException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a RequestFormatException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public RequestFormatException(String msg) {
        super(msg);
    }

    /**
     * Construct a RequestFormatException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public RequestFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
