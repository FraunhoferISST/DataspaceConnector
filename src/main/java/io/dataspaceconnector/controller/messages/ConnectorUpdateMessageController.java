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
package io.dataspaceconnector.controller.messages;

import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.core.util.MultipartParseException;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
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

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * Controller for sending ids connector update messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class ConnectorUpdateMessageController {

    /**
     * The service for communication with the ids broker.
     */
    private final @NonNull IDSBrokerService brokerService;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Sending an ids connector update message with the current connector as payload.
     * TODO Validate response message and return OK or other status code.
     *
     * @param recipient The url of the recipient.
     * @return The response message or an error.
     */
    @PostMapping("/connector/update")
    @Operation(summary = "Connector update message", description = "Can be used for registering or "
            + "updating the connector at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    public ResponseEntity<Object> sendConnectorUpdateMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final String recipient) {
        try {
            // Update the config model.
            connectorService.updateConfigModel();

            // Send the connector update message.
            final var response = brokerService.updateSelfDescriptionAtBroker(URI.create(recipient));
            if(response != null){
                return ResponseEntity.ok("Success");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ids message handling failed. null");
            }
        } catch (ConfigUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        } catch (NullPointerException | IOException | DapsTokenManagerException | ClaimsException | MultipartParseException exception) {
            //TODO: as the Messaging services added  DapsTokenManagerException, ClaimsException and MultipartParseException as ne Eceptions, check if a new type of Response Message is needed
            return ControllerUtils.respondIdsMessageFailed(exception);
        }
    }
}
