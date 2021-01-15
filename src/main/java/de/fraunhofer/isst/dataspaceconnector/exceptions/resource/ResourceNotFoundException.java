package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construct a ResourceNotFoundException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
