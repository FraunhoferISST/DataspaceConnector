package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.repositories.RequestedResourcesRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for requested resources.
 */
@Service
@NoArgsConstructor
public class RequestedResourceService extends ResourceService<RequestedResource, RequestedResourceDesc>
        implements RemoteResolver {
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (RequestedResourcesRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }
}
