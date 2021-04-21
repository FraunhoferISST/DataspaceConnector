package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestedResourceUpdater implements InfomodelUpdater<de.fraunhofer.iais.eis.Resource, RequestedResource> {

    /**
     * Service for requested resources.
     */
    private final @NonNull RequestedResourceService requestedResourceService;

    @Override
    public RequestedResource update(final de.fraunhofer.iais.eis.Resource entity) throws ResourceNotFoundException {
        final var entityId = requestedResourceService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var template = MappingUtils.fromIdsResource(entity);
        return requestedResourceService.update(entityId.get(), template.getDesc());
    }
}
