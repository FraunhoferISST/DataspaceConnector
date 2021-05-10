package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.repositories.RequestedResourcesRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles the basic logic for requested resources.
 */
@Service
@NoArgsConstructor
public final class RequestedResourceService extends ResourceService<RequestedResource,
        RequestedResourceDesc> implements RemoteResolver {
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (RequestedResourcesRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }
}
