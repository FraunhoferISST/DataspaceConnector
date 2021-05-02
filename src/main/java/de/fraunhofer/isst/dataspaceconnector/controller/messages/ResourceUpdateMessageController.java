package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * Controller for sending ids resource update messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class ResourceUpdateMessageController {

    /**
     * The service for communication with the ids broker.
     */
    private final @NonNull IDSBrokerService brokerService;

    /**
     * Service for ids resources.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Sending an ids resource update message with a resource as payload.
     * TODO Validate response message and return OK or other status code.
     *
     * @param recipient  The url of the recipient.
     * @param resourceId The resource id.
     * @return The response message or an error.
     */
    @PostMapping("/resource/update")
    @Operation(summary = "Resource update message", description = "Can be used for registering or "
            + "updating a resource at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    public ResponseEntity<Object> sendConnectorUpdateMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final String recipient,
            @Parameter(description = "The resource id.")
            @RequestParam(value = "resourceId") final URI resourceId) {
        try {
            final var resource = connectorService.getOfferedResourceById(resourceId);
            if (resource == null) {
                return ControllerUtils.respondResourceNotFound(resourceId);
            }

            // Send the resource update message.
            final var response = brokerService.updateResourceAtBroker(recipient, resource);
            final var responseToString = Objects.requireNonNull(response.body()).string();
            return ResponseEntity.ok(responseToString);
        } catch (ClassCastException exception) {
            return ControllerUtils.respondResourceCouldNotBeLoaded(resourceId);
        } catch (NullPointerException | IOException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        }
    }
}
