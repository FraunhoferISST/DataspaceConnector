package de.fraunhofer.isst.dataspaceconnector.exceptions;

import de.fraunhofer.isst.dataspaceconnector.model.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;

public class ResourceMovedException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The resource's new location.
     */
    private final EndpointId newEndpoint;

    /**
     * All args constructor.
     *
     * @param newEndpoint The new endpoint.
     */
    public ResourceMovedException(final Endpoint newEndpoint) {
        super(newEndpoint.getId().toString());
        this.newEndpoint = newEndpoint.getId();
    }

    /**
     * Getter for new endpoint value.
     *
     * @return The new endpoint.
     */
    public EndpointId getNewEndpoint() {
        return newEndpoint;
    }
}
