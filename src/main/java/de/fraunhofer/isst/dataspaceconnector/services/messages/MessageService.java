package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnexpectedResponseType;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RequestedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.services.TemplateService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ArtifactRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.TemplateBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyManagementService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final @NonNull DeserializationService service;

    /**
     * Template builder.
     */
    private final @NonNull TemplateBuilder<RequestedResource, RequestedResourceDesc> templateBuilder;

    /**
     * Service for current connector configuration.
     */
    private final @NonNull IdsConnectorService connectorService;

    /**
     * Service for creating templates using the template builder.
     */
    private final @NonNull TemplateService templateService;

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyManagementService.class);

    /**
     * Requested resource view assembler.
     */
    private final @NonNull RequestedResourceViewAssembler viewAssembler;

    private final @NonNull ArtifactService artifactService;

    /**
     * Build and send a description request message. Validate response.
     *
     * @param recipient The recipient.
     * @param elementId The requested element.
     * @return The response map.
     * @throws MessageException       If message handling failed.
     * @throws UnexpectedResponseType If the validation failed.
     */
    public Map<String, String> sendDescriptionRequestMessage(final URI recipient,
                                                             final URI elementId) throws MessageException {
        final var desc = new DescriptionRequestMessageDesc(elementId);
        desc.setRecipient(recipient);

        return descriptionRequestService.sendMessage(desc, "");
    }

    public void validateDescriptionResponseMessage(final Map<String, String> response)
            throws MessageResponseException, UnexpectedResponseType {
        descriptionRequestService.validateResponse(response);
    }

    /**
     * Build and send a description request message. Validate response.
     *
     * @param recipient       The recipient.
     * @param contractRequest The contract request.
     * @return The response map.
     * @throws MessageException If message handling failed.
     */
    public Map<String, String> sendContractRequestMessage(final URI recipient,
                                                          final ContractRequest contractRequest)
            throws MessageException, ConstraintViolationException {
        final var contractId = contractRequest.getId();
        final var contractRdf = IdsUtils.toRdf(contractRequest);

        final var desc = new ContractRequestMessageDesc(contractId);
        desc.setRecipient(recipient);

        return contractRequestService.sendMessage(desc, contractRdf);
    }

    public void validateContractRequestResponseMessage(final Map<String, String> response)
            throws MessageResponseException, UnexpectedResponseType {
        contractRequestService.validateResponse(response);
    }

    public Map<String, String> sendContractAgreementMessage(final URI recipient,
                                                            final ContractAgreement contractAgreement)
            throws MessageException, ConstraintViolationException {
        final var contractId = contractAgreement.getId();
        final var contractRdf = IdsUtils.toRdf(contractAgreement);

        final var desc = new ContractAgreementMessageDesc(contractId);
        desc.setRecipient(recipient);

        return contractAgreementService.sendMessage(desc, contractRdf);
    }

    public void validateContractAgreementResponseMessage(final Map<String, String> response)
            throws MessageResponseException, UnexpectedResponseType {
        contractAgreementService.validateResponse(response);
    }

    public Map<String, String> sendArtifactRequestMessage(final URI recipient,
                                                          final URI elementId,
                                                          final URI agreementId) throws MessageException {
        final var desc = new ArtifactRequestMessageDesc(elementId, agreementId);
        desc.setRecipient(recipient);

        return artifactRequestService.sendMessage(desc, "");
    }

    public void validateArtifactResponseMessage(final Map<String, String> response)
            throws MessageResponseException, UnexpectedResponseType {
        artifactRequestService.validateResponse(response);
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

        final var modelVersion = MessageUtils.extractModelVersionFromMessage(message);
        final var inboundVersions = connectorService.getInboundModelVersion();
        MessageUtils.checkForVersionSupport(modelVersion, inboundVersions);
    }

    /**
     * Show response message that was not expected.
     *
     * @param response The response map.
     * @return ResponseEntity with status code 200 or 500.
     */
    public ResponseEntity<Object> returnResponseMessageContent(final Map<String, String> response) {
        try {
            final var message = getResponseMessageContent(response);
            return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
        } catch (MessageResponseException exception) {
            return ControllerUtils.respondReceivedInvalidResponse(exception);
        }
    }

    /**
     * If the response message is not of the expected type, message type, rejection reason, and the
     * payload are returned as an object.
     *
     * @param message The ids multipart message as map.
     * @return The object.
     * @throws MessageResponseException Of the response could not be read or deserialized.
     */
    public Object getResponseMessageContent(final Map<String, String> message) throws MessageResponseException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(message);

        final var idsMessage = service.deserializeResponseMessage(header);
        var responseMap = new HashMap<String, String>() {{
            put("type", String.valueOf(idsMessage.getClass()));
        }};

        // If the message is of type exception, add the reason to the response object.
        if (idsMessage instanceof RejectionMessage) {
            final var rejectionMessage = (RejectionMessage) idsMessage;
            final var reason = MessageUtils.extractRejectionReasonFromMessage(rejectionMessage);
            responseMap.put("reason", reason.toString());
        }

        responseMap.put("payload", payload);
        return responseMap;
    }

    /**
     * Turns the payload into an ids infrastructure component.
     *
     * @param payload The message payload as string.
     * @return The ids infrastructure component.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public InfrastructureComponent getComponentFromPayload(final String payload) throws IllegalArgumentException {
        return service.deserializeInfrastructureComponent(payload);
    }

    /**
     * Turns the payload into an ids resource.
     *
     * @param payload The message payload as string.
     * @return The ids resource.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Resource getResourceFromPayload(final String payload) throws IllegalArgumentException {
        return service.deserializeResource(payload);
    }

    /**
     * Validate response and save resource to database.
     *
     * @param response The response message map.
     * @param artifactList List of requested artifacts.
     * @return The persisted resource.
     *
     */
    public URI saveResource(final Map<String, String> response, final List<URI> artifactList)
            throws PersistenceException, MessageResponseException, IllegalArgumentException {
        // Exceptions handled at a higher level.
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var resource = getResourceFromPayload(payload);

        try {
            final var resourceTemplate = templateService.getResourceTemplate(resource);

            // Read all contract offers.
            final var contractTemplateList = templateService.getContractTemplates(resource);
            resourceTemplate.setContracts(contractTemplateList);

            // Read all representations.
            final var representationTemplateList =
                    templateService.getRepresentationTemplates(resource);
            resourceTemplate.setRepresentations(representationTemplateList);

            // Save all entities.
            final var requestedResource = templateBuilder.build(resourceTemplate);
            final var entity = viewAssembler.toModel(requestedResource);
            return entity.getLink("self").get().toUri();
        } catch (Exception e) {
            LOGGER.warn("Could not store resource. [exception=({})]", e.getMessage());
            throw new PersistenceException("Could not store resource.", e);
        }
    }

    public URI saveData(final Map<String, String> response, final URI artifactId) throws MessageResponseException {
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        // artifactService.update()
        // add data to artifact TODO

        return null;
    }
}
