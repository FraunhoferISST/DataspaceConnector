package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates a resource.
 */
@Component
public class RequestedResourceFactory extends ResourceFactory<RequestedResource, RequestedResourceDesc> {

    @Override
    protected RequestedResource createInternal(RequestedResourceDesc desc) {
        return new RequestedResource();
    }
}
