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
package io.dataspaceconnector.service;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.controller.resource.ResourceControllers;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.agreement.AgreementDesc;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.resource.AgreementService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.resource.RelationServices;
import io.dataspaceconnector.service.resource.TemplateBuilder;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.service.util.EndpointUtils;
import io.dataspaceconnector.util.IdsUtils;
import io.dataspaceconnector.util.MessageUtils;
import io.dataspaceconnector.util.TemplateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jose4j.base64url.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.PersistenceException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This service offers methods for saving contract agreements as well as metadata and data requested
 * from other connectors to the database.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EntityPersistenceService {

    /**
     * The HTTP base URL of the application.
     */
    @Value("${application.http.base-url}")
    private String baseUrl;

    /**
     * Service for contract agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for updating artifact data.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Service for linking agreements and artifacts.
     */
    private final @NonNull RelationServices.AgreementArtifactLinker linker;

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Template builder.
     */
    private final @NonNull TemplateBuilder<RequestedResource, RequestedResourceDesc> tempBuilder;

    /**
     * Save contract agreement to database (consumer side).
     *
     * @param agreement The ids contract agreement.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public UUID saveContractAgreement(final ContractAgreement agreement)
            throws PersistenceException {
        try {
            final var agreementId = agreement.getId();
            final var rdf = IdsUtils.toRdf(agreement);

            final var desc = new AgreementDesc(agreementId, true, rdf, null);

            // Save agreement to return its id.
            return agreementService.create(desc).getId();
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not store contract agreement. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }

    /**
     * Builds a contract agreement from a contract request and saves this agreement to the database
     * with relation to the targeted artifacts (provider side).
     *
     * @param request    The ids contract request.
     * @param targetList List of artifacts.
     * @param issuer     The issuer connector id.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    public ContractAgreement buildAndSaveContractAgreement(
            final ContractRequest request, final List<URI> targetList, final URI issuer)
            throws PersistenceException {
        UUID agreementUuid = null;
        try {
            // Get base URL of application and path to agreements API.
            final var applicationBaseUrl = getApplicationBaseUrl();
            final var path = ResourceControllers.AgreementController.class.getAnnotation(
                    RequestMapping.class).value()[0];

            // Persist empty agreement to generate UUID.
            agreementUuid = agreementService.create(new AgreementDesc()).getId();

            // Construct ID of contract agreement (URI) using base URL, path and the UUID.
            final var agreementId = URI.create(applicationBaseUrl + path + "/" + agreementUuid);

            // Build the contract agreement using the constructed ID
            final var agreement = contractManager.buildContractAgreement(request,
                    agreementId, issuer);

            // Iterate over all targets to get the UUIDs of the corresponding artifacts.
            final var artifactList = new ArrayList<UUID>();
            for (final var target : targetList) {
                final var uuid = EndpointUtils.getUUIDFromPath(target);
                artifactList.add(uuid);
            }

            final var rdf = IdsUtils.toRdf(agreement);

            final var desc = new AgreementDesc();
            desc.setConfirmed(false);
            desc.setValue(rdf);

            // Update agreement in database using its previously set id.
            agreementService.update(EndpointUtils.getUUIDFromPath(agreement.getId()), desc);

            // Add artifacts to agreement using the linker.
            linker.add(EndpointUtils.getUUIDFromPath(agreement.getId()),
                    new HashSet<>(artifactList));

            return agreement;
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not store agreement. [exception=({})]", e.getMessage(), e);
            }

            // if agreement cannot be saved, remove empty agreement from database
            if (agreementUuid != null) {
                agreementService.delete(agreementUuid);
            }

            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }

    /**
     * Returns the connector's base URL. As no request context is available to obtain
     * the application's base URL from when communicating via IDSCPv2, this method then
     * defaults to using the base URL set in application.properties.
     *
     * @return the base URL.
     */
    private String getApplicationBaseUrl() {
        String applicationBaseUrl;
        try {
            applicationBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        } catch (IllegalStateException e) {
            //communicating via IDSCPv2, no current request context available
            applicationBaseUrl = this.baseUrl;
        }
        return applicationBaseUrl;
    }

    /**
     * Validate response and save resource to database.
     *
     * @param response     The response message map.
     * @param artifactList List of requested artifacts.
     * @param download     Indicated whether the artifact is going to be downloaded automatically.
     * @param remoteUrl    The provider's url for receiving artifact request messages.
     */
    public void saveMetadata(final Map<String, String> response, final List<URI> artifactList,
                             final boolean download, final URI remoteUrl)
            throws PersistenceException, IllegalArgumentException {
        // Exceptions handled at a higher level.
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var resource = deserializationService.getResource(payload);

        try {
            final var resourceTemplate =
                    TemplateUtils.getResourceTemplate(resource);
            final var representationTemplateList =
                    TemplateUtils.getRepresentationTemplates(resource, artifactList, download,
                            remoteUrl);

            resourceTemplate.setRepresentations(representationTemplateList);

            // Save all entities.
            tempBuilder.build(resourceTemplate);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not store resource. [exception=({})]", e.getMessage(), e);
            }
            throw new PersistenceException("Could not store resource.", e);
        }
    }

    /**
     * Save data and return the uri of the respective artifact.
     *
     * @param response The response message.
     * @param remoteId The artifact id.
     * @throws IllegalArgumentException  if the message response could not be processed.
     * @throws ResourceNotFoundException if the artifact could not be found.
     * @throws IOException               if the data could not be stored.
     */
    public void saveData(final Map<String, String> response, final URI remoteId)
            throws ResourceNotFoundException, IllegalArgumentException, IOException {
        final var base64Data = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var artifactId = artifactService.identifyByRemoteId(remoteId);

        if (artifactId.isEmpty()) {
            throw new ResourceNotFoundException(remoteId.toString());
        }

        final var artifact = artifactService.get(artifactId.get());
        artifactService.setData(artifact.getId(),
                new ByteArrayInputStream(Base64.decode(base64Data)));
        if (log.isDebugEnabled()) {
            log.debug("Updated data from artifact. [target=({})]", artifactId);
        }
    }
}
