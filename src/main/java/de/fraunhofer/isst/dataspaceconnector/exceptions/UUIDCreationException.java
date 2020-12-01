package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class UUIDCreationException extends RuntimeException{
    /**
     * Construct an UUIDCreationException with the specified detail message.
     *
     * @param msg   The detail message.
     */
    public UUIDCreationException(String msg) {
        super(msg);
    }
}
