package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates a resource.
 */
@Component
public final class RequestedResourceFactory
        extends ResourceFactory<RequestedResource, RequestedResourceDesc> {

    /**
     * The default remote id assigned to all requested resources.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    @Override
    protected RequestedResource createInternal(final RequestedResourceDesc desc) {
        return new RequestedResource();
    }

    @Override
    protected boolean updateInternal(
            final RequestedResource resource, final RequestedResourceDesc desc) {
        return updateRemoteId(resource, desc.getRemoteId());
    }

    private boolean updateRemoteId(final RequestedResource resource, final URI remoteId) {
        final var newUri =
                MetadataUtils.updateUri(resource.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(resource::setRemoteId);

        return newUri.isPresent();
    }
}
