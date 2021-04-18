package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.SelfLinkCreationException;
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
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsArtifactBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsCatalogBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsContractBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsRepresentationBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsResourceBuilder;
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
    private final @NonNull IdsCatalogBuilder catalogBuilder;

    /**
     * Service for building ids resource.
     */
    private final @NonNull IdsResourceBuilder<OfferedResource> offerBuilder;

    /**
     * Service for building ids artifact.
     */
    private final @NonNull IdsArtifactBuilder artifactBuilder;

    /**
     * Service for building ids representation.
     */
    private final @NonNull IdsRepresentationBuilder representationBuilder;

    /**
     * Service for building ids contract.
     */
    private final @NonNull IdsContractBuilder contractBuilder;

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
                case OFFERS:
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
    public <T extends AbstractEntity> String getEntityAsRdfString(final T entity) throws InvalidResourceException {
        // NOTE Maybe the builder class could be found without the ugly if array?
        try {
            if (entity instanceof Artifact) {
                final var artifact = artifactBuilder.create((Artifact) entity);
                return Objects.requireNonNull(artifact).toRdf();
            } else if (entity instanceof OfferedResource) {
                final var resource = offerBuilder.create((OfferedResource) entity);
                return Objects.requireNonNull(resource).toRdf();
            } else if (entity instanceof Representation) {
                final var representation = representationBuilder.create((Representation) entity);
                return Objects.requireNonNull(representation).toRdf();
            } else if (entity instanceof Catalog) {
                final var catalog = catalogBuilder.create((Catalog) entity);
                return Objects.requireNonNull(catalog).toRdf();
            } else if (entity instanceof Contract) {
                final var catalog = contractBuilder.create((Contract) entity);
                return Objects.requireNonNull(catalog).toRdf();
            } else if (entity instanceof Agreement) {
                final var agreement = (Agreement) entity;
                return agreement.getValue();
            } else if (entity instanceof ContractRule) {
                final var rule = (ContractRule) entity;
                return rule.getValue();
            }
        } catch (SelfLinkCreationException exception) {
            LOGGER.warn("Could not provide ids object. [entity=({})]", entity);
            throw exception;
        } catch (Exception exception) {
            // If we do not allow requesting an object type, respond with exception.
            LOGGER.warn("Could not provide ids object. [entity=({})]", entity);
            throw new InvalidResourceException("No provided description for requested element.");
        }

        // If we do not allow requesting an object type, respond with exception.
        LOGGER.debug("Not a requestable ids object. [entity=({})]", entity);
        throw new InvalidResourceException("No provided description for requested element.");
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
