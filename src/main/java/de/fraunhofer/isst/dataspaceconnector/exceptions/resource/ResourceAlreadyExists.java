package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class ResourceAlreadyExists extends ResourceException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceAlreadyExists(String msg) {
        super(msg);
    }
}
