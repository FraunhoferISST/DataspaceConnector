package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.view.ids.IdsViewer;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
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

    private final @NonNull IdsViewer idsViewer;

    public AbstractEntity getEntityById(final URI elementId) throws ResourceNotFoundException {
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

    /**
     * TODO Add catalog
     * @param entity The connector's entity.
     * @return A rdf string of an ids object.
     */
    public String getEntityAsIdsRdfString(final AbstractEntity entity) throws ConstraintViolationException, InvalidResourceException {
        String rdf;

        if (entity instanceof Artifact) {
            final var artifact = idsViewer.create((Artifact) entity);
            rdf = IdsUtils.convertArtifactToRdf(artifact);
        } else if (entity instanceof Resource) {
            final var resource = idsViewer.create((Resource) entity);
            rdf = IdsUtils.convertResourceToRdf(resource);
        } else if (entity instanceof Representation) {
            final var representation = idsViewer.create((Representation) entity);
            rdf = IdsUtils.convertRepresentationToRdf(representation);
        } else {
            throw new InvalidResourceException("No provided IDS description for requested element.");
        }

        return rdf;
    }
}
