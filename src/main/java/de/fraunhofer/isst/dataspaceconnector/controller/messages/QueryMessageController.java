package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for sending ids query messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class QueryMessageController {

    /**
     * The service for communication with the ids broker.
     */
    private final @NonNull IDSBrokerService brokerService;

    /**
     * Sending an ids query message with a query message as payload.
     * TODO Validate response message and return OK or other status code.
     *
     * @param recipient The url of the recipient.
     * @param query     The query statement.
     * @return The response message or an error.
     */
    @PostMapping("/query")
    @Operation(summary = "Query message", description = "Can be used for querying an "
            + "IDS broker.")
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
            @Schema(description = "Database query (SparQL)", required = true,
                    example = "SELECT ?subject ?predicate ?object\n"
                            + "FROM <urn:x-arq:UnionGraph>\n"
                            + "WHERE {\n"
                            + "  ?subject ?predicate ?object\n"
                            + "};") @RequestBody final String query) {
        try {
            // Send the resource update message.
            final var response = brokerService.queryBroker(recipient, query,
                    null, null, null);
            final var responseToString = Objects.requireNonNull(response.body()).string();
            return ResponseEntity.ok(responseToString);
        } catch (IOException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        }
    }
}
