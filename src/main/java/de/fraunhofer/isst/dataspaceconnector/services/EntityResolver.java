package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Objects;

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
    private final @NonNull ResourceService<OfferedResource, OfferedResourceDesc> offerService;

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
                    return offerService.get(entityId);
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
    public Object getEntityAsIdsObject(final AbstractEntity entity) throws InvalidResourceException {
        if (entity instanceof Artifact) {
            return viewService.create((Artifact) entity);
        } else if (entity instanceof Resource) {
            return viewService.create((Resource) entity);
        } else if (entity instanceof Representation) {
            return viewService.create((Representation) entity);
        } else if (entity instanceof Catalog) {
            return viewService.create((Catalog) entity);
        } else if (entity instanceof Contract) {
            return viewService.create((Contract) entity);
        } else if (entity instanceof Agreement) {
            final var agreement = (Agreement) entity;
            return agreement.getValue();
        } else if (entity instanceof ContractRule) {
            final var rule = (ContractRule) entity;
            return rule.getValue();
        } else {
            LOGGER.debug("Could not provide ids object. [entity=({})]", entity);
            throw new InvalidResourceException("No provided description for requested element.");
        }
    }

    /**
     * Return artifact by uri.
     *
     * @param requestedArtifact The artifact uri.
     * @param queryInput        Http query for data request.
     * @return Artifact from database.
     */
    public Object getDataByArtifactId(final URI requestedArtifact, final QueryInput queryInput) {
        final var endpoint = EndpointUtils.getUUIDFromPath(requestedArtifact);

        return artifactService.getData(endpoint, queryInput);
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
     * Get agreement by remote id.
     *
     * @param id The remote id (at provider side).
     * @return The artifact of the database.
     * @throws ResourceNotFoundException If the resource could not be found.
     */
    public Agreement getAgreementByUri(final URI id) throws ResourceNotFoundException {
        final var uuid = EndpointUtils.getUUIDFromPath(id);
        return agreementService.get(uuid);
    }
}
