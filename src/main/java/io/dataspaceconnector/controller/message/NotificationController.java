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

import io.dataspaceconnector.controller.message.tag.MessageDescription;
import io.dataspaceconnector.controller.message.tag.MessageName;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.type.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller for sending notifications to subscribers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = MessageName.MESSAGES, description = MessageDescription.MESSAGES)
public class NotificationController {

    /**
     * Service for handling subscriptions.
     */
    private final @NonNull SubscriptionService subscriptionSvc;

    /**
     * Service for resolving database entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for sending notifications to subscribers.
     */
    private final @NonNull SubscriberNotificationService subscriberNotificationSvc;

    /**
     * Notify all subscribers to a given element.
     *
     * @param elementId The entity id.
     * @return The response entity.
     */
    @PutMapping("/notify")
    @Operation(summary = "Notify all subscribers.", description = "Can be used to manually notify "
            + "all subscribers about a resource offer, representation, or artifact update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The element id.", required = true)
            @RequestParam("elementId") final URI elementId) {
        final var entity = entityResolver.getEntityById(elementId);
        if (entity.isEmpty()) {
            return ResponseUtils.respondResourceNotFound(elementId);
        }

        final var subscriptions = subscriptionSvc.getByTarget(elementId);
        if (subscriptions.isEmpty()) {
            return ResponseUtils.respondNoSubscriptionsFound(elementId);
        }

        // Notify all subscribers.
        subscriberNotificationSvc.notifyAll(subscriptions, elementId, entity.get());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
