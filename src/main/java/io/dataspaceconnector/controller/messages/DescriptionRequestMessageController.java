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

import io.dataspaceconnector.exceptions.MessageException;
import io.dataspaceconnector.exceptions.MessageResponseException;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.messages.types.DescriptionRequestService;
import io.dataspaceconnector.utils.ControllerUtils;
import io.dataspaceconnector.utils.MessageUtils;
import io.dataspaceconnector.utils.Utils;
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
