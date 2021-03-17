package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.TemplateService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.TemplateBuilder42;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageProcessingService {

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService service;

    /**
     * Template builder.
     */
    private final @NonNull TemplateBuilder42<RequestedResource, RequestedResourceDesc> templateBuilder;

    /**
     * Service for current connector configuration.
     */
    private final @NonNull IdsConnectorService connectorService;

    /**
     * Service for creating templates using the template builder.
     */
    private final @NonNull TemplateService templateService;

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
            return new ResponseEntity<>(message, HttpStatus.OK); // TODO show ok here?
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
     * Map resource to internal data model and save in database.
     *
     * @param resource The ids resource.
     * @return The persisted resource.
     */
    public de.fraunhofer.isst.dataspaceconnector.model.Resource saveMetadata(final Resource resource) {
        final var resourceTemplate = templateService.getResourceTemplate(resource);

        // Read all contract offers.
        final var contractTemplateList = templateService.getContractTemplates(resource);
        resourceTemplate.setContracts(contractTemplateList);

        // Read all representations.
        final var representationTemplateList = templateService.getRepresentationTemplates(resource);
        resourceTemplate.setRepresentations(representationTemplateList);

        // Save all entities.
        return templateBuilder.build(resourceTemplate);
    }

    public void saveData(final String data) {
        // add data to artifact TODO
    }
}
