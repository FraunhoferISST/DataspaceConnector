package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepresentationUpdater
        implements InfomodelUpdater<Representation,
                de.fraunhofer.isst.dataspaceconnector.model.Representation> {

    /**
     * Service for representations.
     */
    private final @NonNull RepresentationService representationService;

    @Override
    public de.fraunhofer.isst.dataspaceconnector.model.Representation update(final Representation entity) throws ResourceNotFoundException {
        final var entityId = representationService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var template = MappingUtils.fromIdsRepresentation(entity);
        return representationService.update(entityId.get(), template.getDesc());
    }
}
