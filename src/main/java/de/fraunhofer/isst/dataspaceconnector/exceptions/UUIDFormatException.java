package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the application has attempted to convert a string to an uuid,
 * but that the string does not have the appropriate format.
 */
public class UUIDFormatException extends IllegalArgumentException {
    /**
     * Construct an UUIDFormatException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public UUIDFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
