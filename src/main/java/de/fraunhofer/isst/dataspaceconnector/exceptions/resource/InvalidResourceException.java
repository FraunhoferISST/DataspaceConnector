package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

/**
 * Thrown to indicate that that a problem with the resource composition occurred.
 */
public class InvalidResourceException extends ResourceException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public InvalidResourceException(String msg) {
        super(msg);
    }
}
