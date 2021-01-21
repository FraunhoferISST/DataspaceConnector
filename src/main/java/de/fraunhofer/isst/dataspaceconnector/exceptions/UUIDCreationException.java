package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem occurred while creating an uuid.
 */
public class UUIDCreationException extends RuntimeException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a UUIDCreationException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public UUIDCreationException(String msg) {
        super(msg);
    }
}
