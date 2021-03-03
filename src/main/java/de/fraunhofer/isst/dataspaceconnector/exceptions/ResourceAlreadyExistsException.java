package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the resource already exists.
 */
public class ResourceAlreadyExistsException extends ResourceException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a ResourceAlreadyExistsException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceAlreadyExistsException(final String msg) {
        super(msg);
    }

}
