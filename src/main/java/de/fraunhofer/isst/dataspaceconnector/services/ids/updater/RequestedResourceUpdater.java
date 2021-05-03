package de.fraunhofer.isst.dataspaceconnector.services.ids.updater;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Updates a dsc requested resource based on a provided ids resource.
 */
@Component
@RequiredArgsConstructor
public final class RequestedResourceUpdater
        implements InfomodelUpdater<de.fraunhofer.iais.eis.Resource, RequestedResource> {

    /**
     * Service for requested resources.
     */
    private final @NonNull RequestedResourceService requestedResourceService;

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestedResource update(final de.fraunhofer.iais.eis.Resource entity)
            throws ResourceNotFoundException {
        final var entityId = requestedResourceService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var template = MappingUtils.fromIdsResource(entity);
        return requestedResourceService.update(entityId.get(), template.getDesc());
    }
}
