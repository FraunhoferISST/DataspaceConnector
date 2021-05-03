package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
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

/**
 * Controller for sending description request messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class DescriptionRequestMessageController {

    /**
     * Service for message handling.
     */
    private final @NonNull DescriptionRequestService messageService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Requests metadata from an external connector by building an DescriptionRequestMessage.
     *
     * @param recipient The target connector url.
     * @param elementId The requested element id.
     * @return The response entity.
     */
    @PostMapping("/description")
    @Operation(summary = "Send ids description request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendDescriptionRequestMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The id of the requested resource.")
            @RequestParam(value = "elementId", required = false) final URI elementId) {
        String payload = null;
        try {
            // Send and validate description request/response message.
            final var response = messageService.sendMessage(recipient, elementId);
            final var valid = messageService.validateResponse(response);
            if (!valid) {
                // If the response is not a description response message, show the response.
                final var content = messageService.getResponseContent(response);
                return ControllerUtils.respondWithMessageContent(content);
            }

            // Read and process the response message.
            payload = MessageUtils.extractPayloadFromMultipartMessage(response);
            if (!Utils.isEmptyOrNull(elementId)) {
                return new ResponseEntity<>(payload, HttpStatus.OK);
            } else {
                // Get payload as component.
                final var component =
                        deserializationService.getInfrastructureComponent(payload);
                return ResponseEntity.ok(component.toRdf());
            }
        } catch (MessageException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException exception) {
            return ControllerUtils.respondReceivedInvalidResponse(exception);
        } catch (IllegalArgumentException exception) {
            // If the response is not of type base connector.
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception exception) {
            return ControllerUtils.respondGlobalException(exception);
        }
    }
}
