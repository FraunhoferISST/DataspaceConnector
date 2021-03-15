package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageResponseService {

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService service;

    /**
     * Service for requested resources.
     */
    private final @NonNull ResourceService<RequestedResource, RequestedResourceDesc> resourceService;

    /**
     * Checks if the response message is of the right type.
     *
     * @param message The received message response.
     * @return True if the response is valid, false if not.
     * @throws MessageResponseException If the header could not be extracted or deserialized.
     */
    public boolean isValidDescriptionResponse(final Map<String, String> message) throws MessageResponseException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var idsMessage = service.deserializeResponseMessage(header);
        return idsMessage instanceof DescriptionResponseMessage;
    }

    /**
     * Checks if the response message is of the right type.
     *
     * @param message The received message response.
     * @return True if the response is valid, false if not.
     * @throws MessageResponseException If the header could not be extracted or deserialized.
     */
    public boolean isValidArtifactResponse(final Map<String, String> message) throws MessageResponseException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var idsMessage = service.deserializeResponseMessage(header);
        return idsMessage instanceof ArtifactResponseMessage;
    }


    public ResponseEntity<Object> showRejectionMessage(final Map<String, String> response) {
        try {
            final var message = getResponseMessageContent(response);
            return new ResponseEntity<>(message, HttpStatus.OK);
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
     * @throws IllegalArgumentException If deserialization failes.
     */
    public InfrastructureComponent getComponentFromPayload(final String payload) throws IllegalArgumentException {
        return service.deserializeInfrastructureComponent(payload);
    }

    /**
     * Turns the payload into an ids resource.
     *
     * @param payload The message payload as string.
     * @return The ids resource.
     * @throws IllegalArgumentException If deserialization failes.
     */
    public Resource getResourceFromPayload(final String payload) throws IllegalArgumentException {
        return service.deserializeResource(payload);
    }

    /**
     *
     * @param metadata
     * @return
     */
    public URI saveMetadata(final Resource metadata) {
        final var resource = service.getIdsResourceAsRequestedResource(metadata);
        final var requestedResource = resourceService.create(resource);

        final var endpoint = EndpointUtils.getCurrentEndpoint(requestedResource.getId());
        return endpoint.toUri();
    }

    public void saveData(final String data) {

    }
}
