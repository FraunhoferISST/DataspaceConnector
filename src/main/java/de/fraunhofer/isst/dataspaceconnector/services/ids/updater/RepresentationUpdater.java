package de.fraunhofer.isst.dataspaceconnector.services.ids.updater;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Updates a dsc representation based on a provided ids representation.
 */
@Component
@RequiredArgsConstructor
public final class RepresentationUpdater implements InfomodelUpdater<Representation,
        de.fraunhofer.isst.dataspaceconnector.model.Representation> {

    /**
     * Service for representations.
     */
    private final @NonNull RepresentationService representationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public de.fraunhofer.isst.dataspaceconnector.model.Representation update(
            final Representation entity) throws ResourceNotFoundException {
        final var entityId = representationService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var template = MappingUtils.fromIdsRepresentation(entity);
        return representationService.update(entityId.get(), template.getDesc());
    }
}
