package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
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

import java.util.UUID;

import static de.fraunhofer.isst.dataspaceconnector.services.utils.ControllerUtils.respondResourceNotFound;

/**
 * This class provides endpoints for the internal resource handling.
 */
@RestController
@RequestMapping("/admin/api/resources")
@Tag(name = "Backend: Resource Data Handling", description = "Endpoints  for resource data handling")
public class ResourceDataController { // Header: Content-Type: application/json

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDataController.class);

    private final ResourceService offeredResourceService, requestedResourceService;

    /**
     * Constructor for ResourceDataController.
     *
     * @param offeredResourceService The service for the offered resources
     * @param requestedResourceService The service for the requested resources
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public ResourceDataController(OfferedResourceServiceImpl offeredResourceService,
        RequestedResourceServiceImpl requestedResourceService) throws IllegalArgumentException {
        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The RequestedResourceService cannot be null.");

        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
    }

    /**
     * Publishes the resource's data as a string.
     *
     * @param id   The resource id.
     * @param data The data string.
     * @return Ok or error response.
     */
    @Operation(summary = "Publish Resource Data String",
        description = "Publish resource data as string.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Invalid resource"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/data", method = {RequestMethod.PUT})
    @ResponseBody
    public ResponseEntity<String> publishResource(
        @Parameter(description = "The resource uuid.", required = true,
            example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @PathVariable("resource-id") UUID id,
        @Parameter(description = "The resource data.", required = true, example = "Data String")
        @RequestParam("data") String data) {
        try {
            offeredResourceService.addData(id, data);
            return new ResponseEntity<>("", HttpStatus.CREATED);
        } catch (ResourceNotFoundException exception) {
            return respondResourceNotFound(id);
        } catch (InvalidResourceException exception) {
            LOGGER.debug("Resource is not valid. [id=({}), exception=({})]", id,
                exception.getMessage());
            return new ResponseEntity<>("Failed to store resource. Resource not valid.",
                HttpStatus.BAD_REQUEST);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("The resource could not be published.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets resource data as a string.
     *
     * @param id The resource id.
     * @return Raw data or an error response.
     */
    @Operation(summary = "Request Data String",
            description = "Get a resource's data as a string.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/data", method = RequestMethod.GET)
    // params = {"type=string"} NOT SUPPORTED with OpenAPI
    @ResponseBody
    public ResponseEntity<String> getDataById(@Parameter(description = "The resource uuid.",
            required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
                                              @PathVariable("resource-id") UUID id) {
        try {
            try {
                return new ResponseEntity<>(offeredResourceService.getData(id), HttpStatus.OK);
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                LOGGER.debug(
                        "Could not find resource in OfferedResourceService. [id=({}), exception=({})]",
                        id, offeredResourceServiceException.getMessage());

                try {
                    return new ResponseEntity<>(requestedResourceService.getData(id),
                            HttpStatus.OK);
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    LOGGER.debug(
                            "Could not find resource in RequestedResourceService. [id=({}), exception=({})]",
                            id, requestedResourceServiceException.getMessage());
                    LOGGER.debug("Could not find resource. [id=({})]", id);
                    return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
                }
            }
        } catch (InvalidResourceException exception) {
            LOGGER.debug("The resource could be found but was invalid. [id=({}), exception=({})]",
                    id, exception.getMessage());
            return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
        } catch (ContractException exception) {
            LOGGER.debug("The policy cannot be enforced. [id=({}), exception=({})]",
                    id, exception.getMessage());
            return new ResponseEntity<>("The deposited policy cannot be enforced.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            LOGGER.warn("Failed to load resource. [id=({}), exception=({})]", id,
                    exception.getMessage());
            return new ResponseEntity<>("Something went wrong.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets resource data as a string by representation id.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @return Raw data or an error response.
     */
    @Operation(summary = "Request Data String by Representation",
            description = "Get a resource's data as a string by representation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/{representation-id}/data", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getDataByRepresentation(
            @Parameter(description = "The resource uuid.", required = true,
                    example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true)
            @PathVariable("representation-id") UUID representationId,
            @Parameter(description = "The query parameters and headers to use when fetching the " +
                    "data from the backend system.")
            @RequestBody(required = false) QueryInput queryInput) {
        try {
            try {
                return new ResponseEntity<>(
                        offeredResourceService.getDataByRepresentation(resourceId, representationId, queryInput),
                        HttpStatus.OK);
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                LOGGER.debug(
                        "Could not find resource in OfferedResourceService. [id=({}), exception=({})]",
                        resourceId, offeredResourceServiceException.getMessage());

                try {
                    return new ResponseEntity<>(requestedResourceService.getData(resourceId),
                            HttpStatus.OK);
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    LOGGER.debug(
                            "Could not find resource in RequestedResourceService. [id=({}), exception=({})]",
                            resourceId, offeredResourceServiceException.getMessage());
                    LOGGER.debug("Could not find resource. [id=({})]", resourceId);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (InvalidResourceException exception) {
            LOGGER.debug("The resource could be found but was invalid. [id=({}), exception=({})]",
                    resourceId, exception.getMessage());
            return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
        } catch (ContractException exception) {
            LOGGER.debug("The policy cannot be enforced. [id=({}), exception=({})]",
                    resourceId, exception.getMessage());
            return new ResponseEntity<>("The deposited policy cannot be enforced.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException exception) {
            //TODO
            LOGGER.debug("Failed to fetch resource data. [id=({}), exception=({})]",
                    resourceId, exception.getMessage());
            return new ResponseEntity<>("Incorrect number of path variables supplied.",
                    HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            LOGGER.warn("Failed to load resource. [id=({}), exception=({})]", resourceId,
                    exception.getMessage());
            return new ResponseEntity<>("Something went wrong.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
