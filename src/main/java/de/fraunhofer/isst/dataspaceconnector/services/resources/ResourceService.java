package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.repositories.RequestedResourcesRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for resources.
 * @param <T> The resource type.
 * @param <D> The resource description type.
 */
@NoArgsConstructor
public class ResourceService<T extends Resource, D extends ResourceDesc<T>>
        extends BaseEntityService<T, D> { }

/**
 * Handles the basic logic for offered resources.
 */
@Service
@NoArgsConstructor
class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> { }

/**
 * Handles the basic logic for requested resources.
 */
@Service
@NoArgsConstructor
class RequestedResourceService extends ResourceService<RequestedResource, RequestedResourceDesc> implements RemoteResolver{
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (RequestedResourcesRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }
}
