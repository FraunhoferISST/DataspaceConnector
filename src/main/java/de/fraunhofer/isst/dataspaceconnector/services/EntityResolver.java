package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsViewUtils;
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

    /**
     * Service for ids artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Service for ids representations.
     */
    private final @NonNull RepresentationService representationService;

    /**
     * Service for offered resources.
     */
    private final @NonNull ResourceService<OfferedResource, OfferedResourceDesc> offeredResourceService;

    /**
     * Service for ids catalogs.
     */
    private final @NonNull CatalogService catalogService;

    /**
     * Return any connector entity by its id.
     *
     * @param elementId The entity id.
     * @return The respective object.
     * @throws ResourceNotFoundException If the resource could not be found.
     */
    public AbstractEntity getEntityById(final URI elementId) throws ResourceNotFoundException {
        final var endpointId = EndpointUtils.getEndpointIdFromPath(elementId);
        final var basePath = endpointId.getBasePath();
        final var entityId = endpointId.getResourceId();

        // Get type of requested element.
        final var pathEnum = EndpointUtils.getBasePathEnumFromString(basePath);

        // Find the right service and return the requested element.
        switch (pathEnum) {
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
     * Translate a connector entity to an ids rdf string. TODO Add catalogs
     *
     * @param entity The connector's entity.
     * @return A rdf string of an ids object.
     */
    public String getEntityAsIdsRdfString(final AbstractEntity entity) throws
            ConstraintViolationException, InvalidResourceException {
        if (entity instanceof Artifact) {
            final var artifact = IdsViewUtils.create((Artifact) entity);
            return IdsUtils.getArtifactAsRdf(artifact);
        } else if (entity instanceof Resource) {
            final var resource = IdsViewUtils.create((Resource) entity);
            return IdsUtils.getResourceAsRdf(resource);
        } else if (entity instanceof Representation) {
            final var representation = IdsViewUtils.create((Representation) entity);
            return IdsUtils.getRepresentationAsRdf(representation);
        } else {
            throw new InvalidResourceException("No provided description for requested element.");
        }
    }
}
