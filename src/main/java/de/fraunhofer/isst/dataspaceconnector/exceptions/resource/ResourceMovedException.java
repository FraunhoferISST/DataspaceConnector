package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;

public class ResourceMovedException extends RuntimeException {
    public ResourceMovedException(final Endpoint newEndpoint) {
        super(newEndpoint.getId().toString());
    }
}
