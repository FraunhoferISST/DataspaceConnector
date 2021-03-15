package de.fraunhofer.isst.dataspaceconnector.controller.ids;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.EntityUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

/**
 * Controller for sending description request messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class DescriptionRequestMessageController {

    /**
     * Service for description request messages.
     */
    private final @NonNull DescriptionRequestService requestService;

    /**
     * Service for message responses.
     */
    private final @NonNull MessageResponseService responseService;

    /**
     * Requests metadata from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient The target connector url.
     * @param elementId The requested element id.
     * @return OK or error response.
     */
    @PostMapping("/request/description")
    @Operation(summary = "Send ids description request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendDescriptionRequestMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The id of the requested resource.")
            @RequestParam(value = "elementId", required = false) final URI elementId) {
        Map<String, String> response;
        try {
            // Build and send a description request message.
            final var desc = new DescriptionRequestDesc(elementId);
            response = requestService.sendMessage(recipient, desc, "");
        } catch (MessageException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        }

        String payload = null;
        try {
            // Read and process the response message.
            final var validResponse = responseService.isValidDescriptionResponse(response);
            if (validResponse) {
                payload = MessageUtils.extractPayloadFromMultipartMessage(response);

                // Handle response.
                if (!EntityUtils.parameterIsEmpty(elementId)) {
                    // Get payload as resource and save it as requested resource.
                    final var resource = responseService.getResourceFromPayload(payload);
                    final var uri = responseService.saveMetadata(resource);

                    // Return the uri of the saved resource. TODO return as header?
                    return new ResponseEntity<>(uri, HttpStatus.OK);
                } else {
                    // Get payload as component.
                    final var component = responseService.getComponentFromPayload(payload);
                    return new ResponseEntity<>(component, HttpStatus.OK);
                }
            } else {
                // If the response is not a description response message, show the response.
                return responseService.showRejectionMessage(response);
            }
        } catch (MessageResponseException exception) {
            return ControllerUtils.respondReceivedInvalidResponse(exception);
        } catch (IllegalArgumentException exception) {
            // If the response is not of type resource or base connector.
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }
    }
}
