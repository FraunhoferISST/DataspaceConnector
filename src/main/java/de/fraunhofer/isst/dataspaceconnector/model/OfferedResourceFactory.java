package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates a resource.
 */
@Component
public class OfferedResourceFactory extends ResourceFactory<OfferedResource, OfferedResourceDesc> {
    @Override
    protected OfferedResource createInternal(OfferedResourceDesc desc) {
        return new OfferedResource();
    }
}
