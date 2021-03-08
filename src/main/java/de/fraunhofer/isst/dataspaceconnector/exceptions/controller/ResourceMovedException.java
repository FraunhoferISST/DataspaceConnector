package de.fraunhofer.isst.dataspaceconnector.exceptions.controller;

import de.fraunhofer.isst.dataspaceconnector.model.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;

public class ResourceMovedException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    private final EndpointId newEndpoint;

    public ResourceMovedException(final Endpoint newEndpoint) {
        super(newEndpoint.getId().toString());
        this.newEndpoint = newEndpoint.getId();
    }

    public EndpointId getNewEndpoint() {
        return newEndpoint;
    }
}
