package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem occurred while creating an uuid.
 */
public class VersionNotSupportedException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a InfoModelVersionNotSupportedException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public VersionNotSupportedException(final String msg) {
        super(msg);
    }
}
