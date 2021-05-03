package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RelationServices;
import de.fraunhofer.isst.dataspaceconnector.services.resources.TemplateBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.ContractManager;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.TemplateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jose4j.base64url.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.PersistenceException;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityPersistenceService {

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
    public UUID saveContractAgreement(final ContractAgreement agreement) throws PersistenceException {
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
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public ContractAgreement buildAndSaveContractAgreement(
            final ContractRequest request, final List<URI> targetList) throws PersistenceException {
        UUID agreementUuid = null;
        try {
            // Get base URL of application and path to agreements API.
            final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            final var path = ResourceControllers.AgreementController.class.getAnnotation(
                    RequestMapping.class).value()[0];

            // Persist empty agreement to generate UUID.
            agreementUuid = agreementService.create(new AgreementDesc()).getId();

            // Construct ID of contract agreement (URI) using base URL, path and the UUID.
            final var agreementId = URI.create(baseUrl + path + "/" + agreementUuid);

            // Build the contract agreement using the constructed ID
            final var agreement = contractManager.buildContractAgreement(request,
                    agreementId);

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
                log.warn("Could not store contract agreement. [exception=({})]",
                        e.getMessage(), e);
            }

            // if agreement cannot be saved, remove empty agreement from database
            if (agreementUuid != null) {
                agreementService.delete(agreementUuid);
            }

            throw new PersistenceException("Could not store contract agreement.", e);
        }
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
            throws PersistenceException, MessageResponseException, IllegalArgumentException {
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
     * @throws MessageResponseException  If the message response could not be processed.
     * @throws ResourceNotFoundException If the artifact could not be found.
     */
    public void saveData(final Map<String, String> response, final URI remoteId)
            throws MessageResponseException, ResourceNotFoundException {
        final var base64Data = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var artifactId = artifactService.identifyByRemoteId(remoteId);
        final var artifact = artifactService.get(artifactId.get());

        artifactService.setData(artifact.getId(),
                new ByteArrayInputStream(Base64.decode(base64Data)));
        if (log.isDebugEnabled()) {
            log.debug("Updated data from artifact. [target=({})]", artifactId);
        }
    }
}
