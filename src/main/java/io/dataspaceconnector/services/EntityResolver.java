/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.services;

import de.fraunhofer.iais.eis.ContractAgreement;
import io.dataspaceconnector.exceptions.InvalidResourceException;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.exceptions.SelfLinkCreationException;
import io.dataspaceconnector.model.AbstractEntity;
import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.ContractRule;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.ids.builder.IdsArtifactBuilder;
import io.dataspaceconnector.services.ids.builder.IdsCatalogBuilder;
import io.dataspaceconnector.services.ids.builder.IdsContractBuilder;
import io.dataspaceconnector.services.ids.builder.IdsRepresentationBuilder;
import io.dataspaceconnector.services.ids.builder.IdsResourceBuilder;
import io.dataspaceconnector.services.resources.AgreementService;
import io.dataspaceconnector.services.resources.ArtifactService;
import io.dataspaceconnector.services.resources.CatalogService;
import io.dataspaceconnector.services.resources.ContractService;
import io.dataspaceconnector.services.resources.RepresentationService;
import io.dataspaceconnector.services.resources.ResourceService;
import io.dataspaceconnector.services.resources.RuleService;
import io.dataspaceconnector.services.usagecontrol.AllowAccessVerifier;
import io.dataspaceconnector.utils.EndpointUtils;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.InputStream;

import org.springframework.stereotype.Service;

/**
 * This service offers methods for finding entities by their identifying URI.
 */
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
     * @throws IllegalArgumentException If the resource is null or the elementId.
     */
    public AbstractEntity getEntityById(final URI elementId) throws ResourceNotFoundException {
        Utils.requireNonNull(elementId, ErrorMessages.URI_NULL);

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
