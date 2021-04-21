package de.fraunhofer.isst.dataspaceconnector.services.messages;

import javax.persistence.PersistenceException;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidInputException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ArtifactRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.TemplateBuilder;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.SelfLinkHelper;
import de.fraunhofer.isst.dataspaceconnector.utils.TemplateUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageService {

    /**
     * Service for description request messages.
     */
    private final @NonNull DescriptionRequestService descriptionRequestService;

    /**
     * Service for contract request messages.
     */
    private final @NonNull ContractRequestService contractRequestService;

    /**
     * Service for contract agreement messages.
     */
    private final @NonNull ContractAgreementService contractAgreementService;

    /**
     * Service for artifact request messages.
     */
    private final @NonNull ArtifactRequestService artifactRequestService;

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Template builder.
     */
    private final @NonNull TemplateBuilder<RequestedResource, RequestedResourceDesc> templateBuilder;

    /**
     * Service for current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for reading or writing JSON objects.
     */
    private final @NonNull ObjectMapper objectMapper;

    /**
     * Service for updating artifact data.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Build and send a description request message.
     *
     * @param recipient The recipient.
     * @param elementId The requested element.
     * @return The response map.
     * @throws MessageException If message handling failed.
     */
    public Map<String, String> sendDescriptionRequestMessage(final URI recipient,
                                                             final URI elementId)
            throws MessageException {
        final var desc = new DescriptionRequestMessageDesc(recipient, elementId);
        return descriptionRequestService.sendMessage(desc, "");
    }

    /**
     * Check if the response message is of type description response.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException If the response could not be read.
     */
    public boolean validateDescriptionResponseMessage(final Map<String, String> response)
            throws MessageResponseException {
        return descriptionRequestService.isValidResponseType(response);
    }

    /**
     * Build and send a description request message.
     *
     * @param recipient       The recipient.
     * @param contractRequest The contract request.
     * @return The response map.
     * @throws MessageException    If message handling failed.
     * @throws RdfBuilderException If the contract request rdf string could not be built.
     */
    public Map<String, String> sendContractRequestMessage(final URI recipient,
                                                          final ContractRequest contractRequest)
            throws MessageException, RdfBuilderException {
        final var contractId = contractRequest.getId();
        final var contractRdf = IdsUtils.toRdf(contractRequest);

        final var desc = new ContractRequestMessageDesc(recipient, contractId);
        return contractRequestService.sendMessage(desc, contractRdf);
    }

    /**
     * Check if the response message is of type contract agreement.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException If the response could not be read.
     */
    public boolean validateContractRequestResponseMessage(final Map<String, String> response)
            throws MessageResponseException {
        return contractRequestService.isValidResponseType(response);
    }

    /**
     * Build and send a contract agreement message.
     *
     * @param recipient The recipient.
     * @param agreement The contract agreement.
     * @return The response map.
     * @throws MessageException    If message handling failed.
     * @throws RdfBuilderException If the contract agreement rdf string could not be built.
     */
    public Map<String, String> sendContractAgreementMessage(final URI recipient,
                                                            final ContractAgreement agreement)
            throws MessageException, ConstraintViolationException {
        final var contractId = agreement.getId();
        final var contractRdf = IdsUtils.toRdf(agreement);

        final var desc = new ContractAgreementMessageDesc(recipient, contractId);
        return contractAgreementService.sendMessage(desc, contractRdf);
    }

    /**
     * Check if the response message is of type message processed notification.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException If the response could not be read.
     */
    public boolean validateContractAgreementResponseMessage(final Map<String, String> response)
            throws MessageResponseException {
        return contractAgreementService.isValidResponseType(response);
    }

    /**
     * Build and send an artifact request message.
     *
     * @param recipient   The recipient.
     * @param elementId   The requested artifact.
     * @param agreementId The transfer contract.
     * @return The response map.
     * @throws MessageException If message handling failed.
     */
    public Map<String, String> sendArtifactRequestMessage(final URI recipient,
                                                          final URI elementId,
                                                          final URI agreementId)
            throws MessageException {
        final var desc = new ArtifactRequestMessageDesc(elementId, agreementId);
        desc.setRecipient(recipient);

        return artifactRequestService.sendMessage(desc, "");
    }

    /**
     * Check if the response message is of type artifact response.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException If the response could not be read.
     */
    public boolean validateArtifactResponseMessage(final Map<String, String> response)
            throws MessageResponseException {
        return artifactRequestService.isValidResponseType(response);
    }

    /**
     * The ids message.
     *
     * @param message The message that should be validated.
     * @throws MessageEmptyException        If the message is empty.
     * @throws VersionNotSupportedException If the message version is not supported.
     */
    public void validateIncomingRequestMessage(final Message message)
            throws MessageEmptyException, VersionNotSupportedException {
        MessageUtils.checkForEmptyMessage(message);

        final var modelVersion = MessageUtils.extractModelVersion(message);
        final var inboundVersions = connectorService.getInboundModelVersion();
        MessageUtils.checkForVersionSupport(modelVersion, inboundVersions);
    }

    /**
     * If the response message is not of the expected type, message type, rejection reason, and the
     * payload are returned as an object.
     *
     * @param message The ids multipart message as map.
     * @return The object.
     * @throws MessageResponseException Of the response could not be read or deserialized.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Map<String, Object> getContent(final Map<String, String> message)
            throws MessageResponseException, IllegalArgumentException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(message);

        final var idsMessage = deserializationService.getResponseMessage(header);
        var responseMap = new HashMap<String, Object>() {{
            put("type", idsMessage.getClass());
        }};

        // If the message is of type exception, add the reason to the response object.
        if (idsMessage instanceof RejectionMessage) {
            final var rejectionMessage = (RejectionMessage) idsMessage;
            final var reason = MessageUtils.extractRejectionReason(rejectionMessage);
            responseMap.put("reason", reason);
        }

        responseMap.put("payload", payload);
        return responseMap;
    }

    /**
     * Validate response and save resource to database.
     *
     * @param response     The response message map.
     * @param artifactList List of requested artifacts.
     * @param download     Indicated whether the artifact is going to be downloaded automatically.
     * @param remoteUrl    The provider's url for receiving artifact request messages.
     * @return The persisted resource.
     */
    public URI saveMetadata(final Map<String, String> response, final List<URI> artifactList,
                            final boolean download, final URI remoteUrl)
            throws PersistenceException, MessageResponseException, IllegalArgumentException {
        // Exceptions handled at a higher level.
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var resource = deserializationService.getResource(payload);

        try {
            final var resourceTemplate =
                    TemplateUtils.getResourceTemplate(resource);
//            final var contractTemplateList = TemplateUtils.getContractTemplates(resource);
            final var representationTemplateList =
                    TemplateUtils.getRepresentationTemplates(resource, artifactList, download,
                            remoteUrl);

//            resourceTemplate.setContracts(contractTemplateList);
            resourceTemplate.setRepresentations(representationTemplateList);

            // Save all entities.
            final var requestedResource = templateBuilder.build(resourceTemplate);
            return SelfLinkHelper.getSelfLink(requestedResource);
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
     * @param response   The response message.
     * @param remoteId The artifact id.
     * @return The artifact uri.
     * @throws MessageResponseException  If the message response could not be processed.
     * @throws ResourceNotFoundException If the artifact could not be found.
     */
    public URI saveData(final Map<String, String> response, final URI remoteId)
            throws MessageResponseException, ResourceNotFoundException {
        final var data = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var artifactId = artifactService.identifyByRemoteId(remoteId);
        final var artifact = artifactService.get(artifactId.get());

        artifactService.setData(artifact.getId(), new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
        if (log.isDebugEnabled()) {
            log.debug("Updated data from artifact. [target=({})]", artifactId);
        }

        // Return access url of the artifact's data.
        return URI.create(SelfLinkHelper.getSelfLink(artifact) + "/data");
    }

    /**
     * Read query parameters from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the query input.
     * @throws InvalidInputException If the query input is not empty but invalid.
     */
    public QueryInput getQueryInputFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("")) {
                // Query input is optional, so no rejection message will be sent. Query input will
                // be checked for null value in HttpService.class.
                return null;
            } else {
                return objectMapper.readValue(payload, QueryInput.class);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid query input. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid query input.", e);
        }
    }
}
