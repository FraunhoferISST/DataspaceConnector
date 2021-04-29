package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates a resource.
 */
@Component
public final class OfferedResourceFactory extends ResourceFactory<OfferedResource,
        OfferedResourceDesc> {
    @Override
    protected OfferedResource createInternal(final OfferedResourceDesc desc) {
        return new OfferedResource();
    }
}
