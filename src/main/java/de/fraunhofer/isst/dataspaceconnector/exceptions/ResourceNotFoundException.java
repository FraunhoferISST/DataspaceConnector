package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    /**
     * Construct an ResourceNotFoundException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Construct an ResourceNotFoundException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ResourceNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
