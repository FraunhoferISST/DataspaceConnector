package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the application has attempted to convert a string to a uuid, but that
 * the string does not have the appropriate format.
 */
public class UUIDFormatException extends IllegalArgumentException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a UUIDFormatException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public UUIDFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
