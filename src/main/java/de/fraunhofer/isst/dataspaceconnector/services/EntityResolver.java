package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class EntityResolver {

    private final @NonNull ArtifactService artifactService;

    private final @NonNull RepresentationService representationService;

    private final @NonNull ResourceService<OfferedResource, OfferedResourceDesc> offeredResourceService;

    private final @NonNull CatalogService catalogService;

    public AbstractEntity getEntityById(final URI elementId) throws InvalidResourceException {
        final var endpointId = EndpointUtils.getEndpointIdFromPath(elementId);
        final var basePath = endpointId.getBasePathEnum();
        final var entityId = endpointId.getResourceId();

        switch (basePath) {
            case ARTIFACTS:
                return artifactService.get(entityId);
            case REPRESENTATIONS:
                return representationService.get(entityId);
            case RESOURCES:
                return offeredResourceService.get(entityId);
            case CATALOGS:
                return catalogService.get(entityId);
            default:
                return null;
        }
    }
}
