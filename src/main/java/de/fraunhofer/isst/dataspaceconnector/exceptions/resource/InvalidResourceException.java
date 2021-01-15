package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class InvalidResourceException extends ResourceException {

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public InvalidResourceException(String msg) {
        super(msg);
    }
}
