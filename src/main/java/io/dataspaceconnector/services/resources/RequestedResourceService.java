package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.repositories.RequestedResourcesRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

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

    /**
     * Updates the list of subscribers for an existing RequestedResource.
     *
     * @param resourceId the ID of the resource.
     * @param subscribers the updated list of subscribed URLs
     * @return the updated resource
     */
    public RequestedResource updateSubscriptions(final UUID resourceId,
                                                 final List<URI> subscribers) {
        final var resource = get(resourceId);
        resource.setSubscribers(subscribers);
        persist(resource);
        return resource;
    }
}
