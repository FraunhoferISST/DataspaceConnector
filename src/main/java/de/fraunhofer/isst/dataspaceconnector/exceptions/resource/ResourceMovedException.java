package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;

public class ResourceMovedException extends RuntimeException {
    EndpointId newEndpoint;

    public ResourceMovedException(final Endpoint newEndpoint) {
        super(newEndpoint.getId().toString());
        this.newEndpoint = newEndpoint.getId();
    }

    public EndpointId getNewEndpoint() {
        return newEndpoint;
    }
}
