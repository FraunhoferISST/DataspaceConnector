package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resources.SubscriberNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * This class provides endpoints for subscribing and unsubscribe urls to a requested resource.
 */
@RestController
@RequestMapping("/admin/api/resources")
@Tag(name = "Connector: Resource Handling", description = "Endpoints  for resource handling")
public class ResourceSubscriptionController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceSubscriptionController.class);
    
    private final SubscriberNotificationService subscriberNotificationService;
    
    @Autowired
    public ResourceSubscriptionController(SubscriberNotificationService subscriberNotificationService) {
        this.subscriberNotificationService = subscriberNotificationService;
    }

    /**
     * Subscribe a url to a requested resource.
     *
     * @param id The resource uuid.
     * @param data The url to a subscribers Rest endpoint.
     * @return ResponseEntity.
     */
    @Operation(summary = "Subscribe an URL for a resource ID",
            description = "Subscribe an URL for a resource ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscribed"),
            @ApiResponse(responseCode = "400", description = "Invalid resource"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping(value = "/subscribe/{resourceID}")
    @ResponseBody
    public ResponseEntity<String> subscribeUrl(
            @Parameter(description = "The resource uuid.", required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @PathVariable("resourceID") UUID id,
            @Parameter(description = "The URL that wants to subscribe to the resourceID.", required = true, example = "Data String")
            @RequestBody String data) {
    
        return subscriberNotificationService.subscribeUrl(id, data);
    }

    /**
     * Unsubscribe a url from a requested resource.
     *
     * @param id The resource uuid.
     * @param data The url to a subscribers Rest endpoint.
     * @return ResponseEntity.
     */
    @Operation(summary = "Delete an URL for a resource ID",
            description = "Delete an URL for a resource ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid resource"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @DeleteMapping(value = "/subscribe/{resourceID}")
    @ResponseBody
    public ResponseEntity<String> deleteSubscribedUrl(
            @Parameter(description = "The resource uuid.", required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @PathVariable("resourceID") UUID id,
            @Parameter(description = "The URL that has to unsubscribe of the resourceID.", required = true, example = "Data String")
            @RequestBody String data) {

        return subscriberNotificationService.deleteSubscribedUrl(id, data);

    }
    

}
