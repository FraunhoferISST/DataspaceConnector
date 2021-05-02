package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.ContractAgreement;
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
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsArtifactBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsCatalogBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsContractBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsRepresentationBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsResourceBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RuleService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.AllowAccessVerifier;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityResolver {

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
     * Skips the data access verification.
     */
    private final @NonNull AllowAccessVerifier allowAccessVerifier;

    /**
     * Performs a artifact requests.
     */
    private final @NonNull BlockingArtifactReceiver artifactReceiver;

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

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
            if (log.isDebugEnabled()) {
                log.debug("Resource not found. [exception=({}), elementId=({})]",
                        exception.getMessage(), elementId, exception);
            }
            throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Translate a connector entity to an ids rdf string.
     *
     * @param <T>    Type of the entity.
     * @param entity The connector's entity.
     * @return A rdf string of an ids object.
     */
    public <T extends AbstractEntity> String getEntityAsRdfString(final T entity)
            throws InvalidResourceException {
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
            if (log.isWarnEnabled()) {
                log.warn("Could not provide ids object. [entity=({}), exception=({})]",
                        entity, exception.getMessage(), exception);
            }
            throw exception;
        } catch (Exception exception) {
            // If we do not allow requesting an object type, respond with exception.
            if (log.isWarnEnabled()) {
                log.warn("Could not provide ids object. [entity=({}), exception=({})]",
                        entity, exception.getMessage(), exception);
            }
            throw new InvalidResourceException("No provided description for requested element.");
        }

        // If we do not allow requesting an object type, respond with exception.
        if (log.isDebugEnabled()) {
            log.debug("Not a requestable ids object. [entity=({})]", entity);
        }
        throw new InvalidResourceException("No provided description for requested element.");
    }

    /**
     * Return artifact by uri. This will skip the access control.
     *
     * @param requestedArtifact The artifact uri.
     * @param queryInput        Http query for data request.
     * @return Artifact from database.
     */
    public InputStream getDataByArtifactId(final URI requestedArtifact,
                                           final QueryInput queryInput) {
        final var endpoint = EndpointUtils.getUUIDFromPath(requestedArtifact);
        return artifactService.getData(allowAccessVerifier, artifactReceiver, endpoint, queryInput);
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

    /**
     * Get stored contract agreement for requested element.
     *
     * @param target The requested element.
     * @return The respective contract agreement.
     */
    public List<ContractAgreement> getContractAgreementsByTarget(final URI target) {
        final var uuid = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(uuid);

        final var agreements = artifact.getAgreements();
        final var agreementList = new ArrayList<ContractAgreement>();
        for (final var agreement : agreements) {
            final var value = agreement.getValue();
            final var idsAgreement = deserializationService.getContractAgreement(value);
            agreementList.add(idsAgreement);
        }
        return agreementList;
    }
}
