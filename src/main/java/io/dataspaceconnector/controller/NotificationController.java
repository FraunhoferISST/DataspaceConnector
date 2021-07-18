package io.dataspaceconnector.controller;

import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.subscription.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
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
    @GetMapping("/notify")
    @Operation(summary = "Notify all subscribers", description = "Can be used to manually notify "
            + "all subscribers about a resource offer, representation, or artifact update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The element id.", required = true)
            @RequestParam("elementId") final URI elementId) {
        try {
            final var entity = entityResolver.getEntityById(elementId);
            if (entity.isEmpty()) {
                return ControllerUtils.respondResourceNotFound(elementId);
            }

            final var subscriptions = subscriptionSvc.getByTarget(elementId);
            if (subscriptions.isEmpty()) {
                return ControllerUtils.respondNoSubscriptionsFound(elementId);
            }

            // Notify all subscribers.
            subscriberNotificationSvc.notifyAll(subscriptions, elementId, entity.get());

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>("Notification of subscribers failed.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
