package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class RequestFormatException extends IllegalArgumentException {

    /**
     * Construct an RequestFormatException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public RequestFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
