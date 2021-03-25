package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
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

    private final @NonNull IdsViewService idsViewService;

    /**
     * Return any connector entity by its id.
     *
     * @param elementId The entity id.
     * @return The respective object.
     * @throws UnreachableLineException If the resource could not be found.
     */
    public AbstractEntity getEntityById(final URI elementId) throws UnreachableLineException {
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
            RdfBuilderException, InvalidResourceException {
        if (entity instanceof Artifact) {
            final var artifact = idsViewService.create((Artifact) entity);
            return IdsUtils.toRdf(artifact);
        } else if (entity instanceof Resource) {
            final var resource = idsViewService.create((Resource) entity);
            return IdsUtils.toRdf(resource);
        } else if (entity instanceof Representation) {
            final var representation = idsViewService.create((Representation) entity);
            return IdsUtils.toRdf(representation);
        } else {
            throw new InvalidResourceException("No provided description for requested element.");
        }
    }
}
