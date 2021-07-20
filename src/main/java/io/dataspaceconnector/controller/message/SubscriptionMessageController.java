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
package io.dataspaceconnector.controller.message;

import java.net.URI;

import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.message.type.SubscriptionRequestService;
import io.dataspaceconnector.util.MessageUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for sending ids subscription (request) messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
public class SubscriptionMessageController {

    /**
     * Service for message handling.
     */
    private final @NonNull SubscriptionRequestService subscriptionReqSvc;

    /**
     * Subscribe to updates of an provided ids element.
     *
     * @param recipient    The target connector url.
     * @param subscription The subscription object.
     * @return The response entity.
     */
    @PostMapping("/subscribe")
    @Operation(summary = "Send IDS request message for element subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> subscribe(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The subscription object.")
            @RequestBody final SubscriptionDesc subscription) {
        // TODO IDSCPv2
        try {
            // Send and validate request/response message.
            final var response = subscriptionReqSvc.sendMessage(recipient,
                    subscription.getTarget(), subscription);

            // Read and process the response message.
            return ResponseEntity.ok(MessageUtils.extractPayloadFromMultipartMessage(response));
        } catch (MessageException exception) {
            // If the message could not be built.
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException e) {
            // If the response message is invalid or malformed.
            return ControllerUtils.respondReceivedInvalidResponse(e);
        } catch (UnexpectedResponseException e) {
            // If the response is not as expected.
            return ControllerUtils.respondWithContent(e.getContent());
        }
    }

    /**
     * Unsubscribe from updates of an provided ids element.
     *
     * @param recipient The target connector url.
     * @param elementId The target of the referred element.
     * @return The response entity.
     */
    @PostMapping("/unsubscribe")
    @Operation(summary = "Send IDS request message for element unsubscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> unsubscribe(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The subscription object.")
            @RequestParam("elementId") final URI elementId) {
        try {
            // Send and validate request/response message.
            final var response = subscriptionReqSvc.sendMessage(recipient, elementId, null);

            // Read and process the response message.
            return ResponseEntity.ok(MessageUtils.extractPayloadFromMultipartMessage(response));
        } catch (MessageException exception) {
            // If the message could not be built.
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException e) {
            // If the response message is invalid or malformed.
            return ControllerUtils.respondReceivedInvalidResponse(e);
        } catch (UnexpectedResponseException e) {
            // If the response is not as expected.
            return ControllerUtils.respondWithContent(e.getContent());
        }
    }
}
