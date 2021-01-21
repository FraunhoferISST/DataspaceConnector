package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

/**
 * Thrown to indicate that the resource already exists.
 */
public class ResourceAlreadyExistsException extends ResourceException {
    // Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceAlreadyExistsException(String msg) {
        super(msg);
    }

}
