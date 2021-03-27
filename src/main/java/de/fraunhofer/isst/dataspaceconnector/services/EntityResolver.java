package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ViewService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RuleService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityResolver {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityResolver.class);

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Service for representations.
     */
    private final @NonNull RepresentationService representationService;

    /**
     * Service for offered resources.
     */
    private final @NonNull ResourceService<OfferedResource, OfferedResourceDesc> offeredResourceService;

    /**
     * Service for catalogs.
     */
    private final @NonNull CatalogService catalogService;

    /**
     * Service for contract offers.
     */
    private final @NonNull ContractService contractService;

    /**
     * Service for contract rules.
     */
    private final @NonNull RuleService ruleService;

    /**
     * Service for contract agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for building ids objects.
     */
    private final @NonNull ViewService viewService;

    /**
     * Return any connector entity by its id.
     *
     * @param elementId The entity id.
     * @return The respective object.
     * @throws ResourceNotFoundException If the resource could not be found.
     */
    public AbstractEntity getEntityById(final URI elementId) throws ResourceNotFoundException {
        try {
            final var endpointId = EndpointUtils.getEndpointIdFromPath(elementId);
            final var basePath = endpointId.getBasePath();
            final var entityId = endpointId.getResourceId();

            final var pathEnum = EndpointUtils.getBasePathEnumFromString(basePath);

            // Find the right service and return the requested element.
            switch (Objects.requireNonNull(pathEnum)) {
                case ARTIFACTS:
                    return artifactService.get(entityId);
                case REPRESENTATIONS:
                    return representationService.get(entityId);
                case RESOURCES:
                    return offeredResourceService.get(entityId);
                case CATALOGS:
                    return catalogService.get(entityId);
                case CONTRACTS:
                    return contractService.get(entityId);
                case RULES:
                    return ruleService.get(entityId);
                case AGREEMENTS:
                    return agreementService.get(entityId);
                default:
                    return null;
            }
        } catch (Exception exception) {
            LOGGER.debug("Resource not found. [exception=({}), elementId=({})]",
                    exception.getMessage(), elementId);
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Translate a connector entity to an ids rdf string.
     *
     * @param entity The connector's entity.
     * @return A rdf string of an ids object.
     */
    public String getEntityAsIdsRdfString(final AbstractEntity entity) throws
            RdfBuilderException, InvalidResourceException {
        if (entity instanceof Artifact) {
            final var artifact = viewService.create((Artifact) entity);
            return IdsUtils.toRdf(artifact);
        } else if (entity instanceof Resource) {
            final var resource = viewService.create((Resource) entity);
            return IdsUtils.toRdf(resource);
        } else if (entity instanceof Representation) {
            final var representation = viewService.create((Representation) entity);
            return IdsUtils.toRdf(representation);
        } else if (entity instanceof Catalog) {
            final var catalog = viewService.create((Catalog) entity);
            return IdsUtils.toRdf(catalog);
        } else if (entity instanceof Contract) {
            final var contract = viewService.create((Contract) entity);
            return IdsUtils.toRdf(contract);
        } else if (entity instanceof Agreement) {
            final var agreement = (Agreement) entity;
            return agreement.getValue();
        } else if (entity instanceof ContractRule) {
            final var rule = (ContractRule) entity;
            return rule.getValue();
        } else {
            LOGGER.debug("Could not provide rdf value. [entity=({})]", entity);
            throw new InvalidResourceException("No provided description for requested element.");
        }
    }

    /**
     * Get artifact by remote id.
     *
     * @param id The remote id (at provider side).
     * @return The artifact of the database.
     * @throws ResourceNotFoundException If the resource could not be found.
     */
    public Artifact getArtifactByRemoteId(final URI id) throws ResourceNotFoundException {
        final var artifacts = artifactService.getAll(Pageable.unpaged());

        for (final var artifact : artifacts) {
            final var remoteId = artifact.getRemoteId();
            if (remoteId.equals(id)) {
                return artifact;
            }
        }

        // Should not be reached.
        LOGGER.warn("Found no artifact with [remoteId=({})]", id);
        throw new ResourceNotFoundException("Found no artifact with this remote id: " + id);
    }

    /**
     * Update value of artifact.
     *
     * @param artifact The artifact.
     * @param data     The data string.
     */
    public void updateDataOfArtifact(final Artifact artifact, final String data) {
        final var desc = new ArtifactDesc();
        desc.setValue(data);

        final var artifactId = artifact.getId();
        artifactService.update(artifactId, desc);
    }

    /**
     * Update value of artifact by artifact id.
     *
     * @param artifactId The artifact id.
     * @param data       The data string.
     */
    public void updateDataOfArtifact(final UUID artifactId, final String data) {
        final var desc = new ArtifactDesc();
        desc.setValue(data);

        artifactService.update(artifactId, desc);
    }
}
