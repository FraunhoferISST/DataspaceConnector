package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class ResourceAlreadyExists extends ResourceException {

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceAlreadyExists(String msg) {
        super(msg);
    }
}
