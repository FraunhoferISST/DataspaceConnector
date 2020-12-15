package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.exceptions.*;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * This class provides endpoints for the internal resource handling. Resources can be created and
 * modified with it's {@link ResourceMetadata} including {@link de.fraunhofer.iais.eis.Contract} and
 * {@link ResourceRepresentation}.
 */
@RestController
@RequestMapping("/admin/api/resources")
@Tag(name = "Connector: Resource Handling", description = "Endpoints  for resource handling")
public class ResourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    private final ResourceService offeredResourceService, requestedResourceService;
    private final PolicyHandler policyHandler;

    /**
     * Constructor for ResourceController.
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public ResourceController(@NotNull OfferedResourceServiceImpl offeredResourceService,
        @NotNull PolicyHandler policyHandler,
        @NotNull RequestedResourceServiceImpl requestedResourceService)
        throws IllegalArgumentException {
        if (offeredResourceService == null) {
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");
        }

        if (policyHandler == null) {
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");
        }

        if (requestedResourceService == null) {
            throw new IllegalArgumentException("The RequestedResourceService cannot be null.");
        }

        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
        this.policyHandler = policyHandler;
    }

    /**
     * Registers a resource with its metadata and, if wanted, with an already existing id.
     *
     * @param resourceMetadata The resource metadata.
     * @param uuid             The resource uuid.
     * @return The added uuid.
     */
    @Operation(summary = "Register Resource", description = "Register a resource by its metadata.")
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createResource(@RequestBody ResourceMetadata resourceMetadata,
        @RequestParam(value = "id", required = false) UUID uuid) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/resource";
        final var endpointType = "POST";

        LOGGER.info("Received request for resource creation."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({}), metadata=({})]",
            requestId, endpointPath, endpointType, uuid, resourceMetadata);

        try {
            if (uuid != null) {
                ((OfferedResourceServiceImpl) offeredResourceService).addResourceWithId(resourceMetadata, uuid);
                LOGGER.info("Successfully added a new resource. [id=({}), creationId=({})]",
                    requestId, uuid);
                return new ResponseEntity<>("Resource registered with uuid: " + uuid,
                    HttpStatus.CREATED);
            } else {
                final var newUuid = offeredResourceService.addResource(resourceMetadata);
                LOGGER.info("Successfully added a new resource. [id=({}), creationId=({})]",
                    requestId, newUuid);
                return new ResponseEntity<>("Resource registered with uuid: " + newUuid.toString(),
                    HttpStatus.CREATED);
            }
        } catch (InvalidResourceException exception) {
            LOGGER.info(
                "Failed to add resource. The resource is not valid. [id=({}), exception=({})]",
                requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be added.",
                HttpStatus.NOT_ACCEPTABLE);
        } catch (ResourceAlreadyExists exception) {
            LOGGER.info(
                "Failed to add resource. The resource already exists. [id=({}), exception=({})]",
                requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be added. It already exits.",
                HttpStatus.FOUND);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to add resource. Something went wrong. [id=({}), exception=({})]",
                requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be added.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates resource metadata by id.
     *
     * @param resourceId       The resource id.
     * @param resourceMetadata The updated metadata.
     * @return OK or error response.
     */
    @Operation(summary = "Update Resource", description = "Update the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @RequestBody ResourceMetadata resourceMetadata) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/resource";
        final var endpointType = "PUT";

        LOGGER.info("Received request for resource updating."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({}), metadata=({})]",
            requestId, endpointPath, endpointType, resourceId, resourceMetadata);

        try {
            ((OfferedResourceServiceImpl) offeredResourceService).updateResource(resourceId, resourceMetadata);
            LOGGER.info("Successfully updated the resource. [id=({})]", requestId);
            return new ResponseEntity<>("Resource was updated successfully", HttpStatus.OK);
        } catch (InvalidResourceException exception) {
            LOGGER.info("Failed to update the resource. The resource is not valid. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceNotFoundException exception) {
            LOGGER.info("Failed to update the resource. The resource could not be found."
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Resource could not be updated.", HttpStatus.NOT_FOUND);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to update the resource. Something went wrong."
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Resource could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets resource metadata by id.
     *
     * @param resourceId The resource id.
     * @return Metadata or an error response.
     */
    @Operation(summary = "Get Resource", description = "Get the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}";
        final var endpointType = "GET";

        LOGGER.info("Received request for resource lookup."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({})]",
            requestId, endpointPath, endpointType, resourceId);

        try {
            try {
                // Try to find the data in the offeredResourceService
                final var responseEntity = new ResponseEntity<Object>(
                    offeredResourceService.getMetadata(resourceId), HttpStatus.OK);
                LOGGER.info("Successfully received the resource. [id=({})]", requestId);
                return responseEntity;
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                LOGGER.debug("Failed to receive the resource from the OfferedResourcesService."
                        + " [id=({}), exception=({})]",
                    requestId, offeredResourceServiceException.getMessage());
                try {
                    // Try to find the data in the requestedResourceService
                    final var responseEntity = new ResponseEntity<Object>(
                        requestedResourceService.getMetadata(resourceId), HttpStatus.OK);
                    LOGGER.info("Successfully received the resource. [id=({})]", requestId);
                    return responseEntity;
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    LOGGER
                        .debug("Failed to receive the resource from the RequestedResourcesService."
                                + " [id=({}), exception=({})]",
                            requestId, requestedResourceServiceException.getMessage());
                    // The data could not be found in the offeredResourceService and requestedResourceService
                    LOGGER.info(
                        "Failed to receive the resource. The resource does not exist. [id=({})]",
                        requestId);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to receive the resource. The resource is not valid."
                + " [id=({}), exception=({})]", resourceId, exception.getMessage());
            return new ResponseEntity<>(
                "The resource could not be received. Not a valid resource format.",
                HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to receive the resource. Something went wrong. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Resource could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes resource by id.
     *
     * @param resourceId The resource id.
     * @return OK or error response.
     */
    @Operation(summary = "Delete Resource", description = "Delete a resource by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}";
        final var endpointType = "DELETE";

        LOGGER.info("Received request for resource deletion."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({})]",
            requestId, endpointPath, endpointType, resourceId);

        if (offeredResourceService.deleteResource(resourceId)) {
            LOGGER.info("Successfully deleted resource from the OfferedResourcesService. [id=({})]",
                requestId);
            return new ResponseEntity<>("Resource was deleted successfully.",
                HttpStatus.OK);
        } else {
            LOGGER
                .debug("Failed to delete the resource from the OfferedResourcesService. [id=({})]",
                    requestId);
            if (requestedResourceService.deleteResource(resourceId)) {
                LOGGER.info(
                    "Successfully deleted resource from the RequestedResourcesService. [id=({})]",
                    requestId);
                return new ResponseEntity<>("Resource was deleted successfully.", HttpStatus.OK);
            } else {
                LOGGER.debug(
                    "Failed to delete the resource from the RequestedResourcesService. [id=({})]",
                    requestId);
                LOGGER.info("Failed to delete the resource. The resource does not exist. [id=({})]",
                    requestId);
                return new ResponseEntity<>("The resource could not be found.",
                    HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * Updates usage policy.
     *
     * @param resourceId The resource id.
     * @param policy     The resource's usage policy as string.
     * @return OK or an error response.
     */
    @Operation(summary = "Update Resource Contract", description = "Update the resource's usage policy.")
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateContract(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "A new resource contract.", required = true)
        @RequestBody String policy) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}";
        final var endpointType = "PUT";

        LOGGER.info("Received request for updating a resource contract."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({}), policy=({})]",
            requestId, endpointPath, endpointType, resourceId, policy);

        try {
            policyHandler.getPattern(policy);
            ((OfferedResourceServiceImpl) offeredResourceService).updateContract(resourceId, policy);
            LOGGER.info("Successfully updated a resource contract. [id=({})]", requestId);
            return new ResponseEntity<>("Contract was updated successfully", HttpStatus.OK);
        } catch (IOException exception) {
            // The policy is not in the correct format.
            LOGGER.info("Failed to update the resource contract. The policy is malformed. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Policy syntax error.", HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Failed to update the resource contract. The resource does not exist. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to update the resource contract. The resource is not valid. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to update the resource contract. Something went wrong. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Resource could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets usage policy.
     *
     * @param resourceId The resource id.
     * @return Contract or an error response.
     */
    @Operation(summary = "Get Resource Contract", description = "Get the resource's usage policy.")
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getContract(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}/contract";
        final var endpointType = "GET";

        LOGGER.info("Received request for a resource contract lookup."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({})]",
            requestId, endpointPath, endpointType, resourceId);

        try {
            try {
                // Try to find the data in the offeredResourceService
                final var policy = offeredResourceService.getMetadata(resourceId).getPolicy();
                LOGGER.info("Successfully received the resource contract. [id=({})]", requestId);
                return new ResponseEntity<>(policy, HttpStatus.OK);
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                LOGGER.debug("Failed to receive the resource from the OfferedResourcesService."
                        + " [id=({}), exception=({})]",
                    requestId, offeredResourceServiceException.getMessage());
                try {
                    // Try to find the data in the requestedResourceService
                    final var policy = requestedResourceService.getMetadata(resourceId).getPolicy();
                    LOGGER
                        .info("Successfully received the resource contract. [id=({})]", requestId);
                    return new ResponseEntity<>(policy, HttpStatus.OK);
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    // The data could not be found in the offeredResourceService and requestedResourceService
                    LOGGER
                        .debug("Failed to receive the resource from the RequestedResourcesService."
                                + " [id=({}), exception=({})]",
                            requestId, requestedResourceServiceException.getMessage());
                    LOGGER.info(
                        "Failed to receive the resource contract. The resource does not exist."
                            + " [id=({})]", requestId);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Failed to receive the resource contract. The resource does not exist."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be found.", HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to receive the resource contract. The resource is not valid. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to receive the resource contract. Something went wrong. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Contract could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get how often resource data has been accessed.
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Operation(summary = "Get Data Access", description = "Get the number of the resource's data access.")
    @RequestMapping(value = "/{resource-id}/access", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAccess(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}/access";
        final var endpointType = "GET";

        LOGGER.info("Received request for a resource access lookup."
                + " [id=({}), endpoint=({}), requestType=({}), uuid=({})]",
            requestId, endpointPath, endpointType, resourceId);

        try {
            final var resource = ((RequestedResourceServiceImpl) requestedResourceService).getResource(resourceId);
            if (resource == null) {
                LOGGER.info("Failed to received the resource access. The resource does not exist. "
                    + "[id=({})]", requestId);
                return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
            }

            LOGGER.info("Successfully received the resource access. [id=({})]", requestId);
            return new ResponseEntity<>(resource.getAccessed(), HttpStatus.OK);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to receive the resource access. The resource is not valid. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to receive the resource access. Something went wrong."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("Access counter could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId     The resource id.
     * @param representation A new representation.
     * @return OK or an error response.
     */
    @Operation(summary = "Add Representation", description = "Add a representation to a resource.")
    @RequestMapping(value = "/{resource-id}/representation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "A new resource representation.", required = true)
        @RequestBody ResourceRepresentation representation,
        @RequestParam(value = "id", required = false) UUID uuid) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}/representation";
        final var endpointType = "POST";
        LOGGER.info("Received request for adding a resource representation."
                + " [id=({}), endpoint=({}), requestType=({}), resourceId=({}), uuid=({}), representation=({})]",
            requestId, endpointPath, endpointType, resourceId, uuid, representation);

        try {
            UUID newUuid = null;
            if (uuid != null) {
                newUuid = ((OfferedResourceServiceImpl) offeredResourceService).addRepresentationWithId(resourceId, representation, uuid);
                LOGGER.info("Successfully added a new resource representation. "
                    + "[id=({}), creationId=({})]", requestId, newUuid);
            } else {
                uuid = ((OfferedResourceServiceImpl) offeredResourceService).addRepresentation(resourceId, representation);
                LOGGER.info("Successfully added a new resource representation. "
                    + "[id=({}), creationId=({})", requestId, newUuid);
                }

            return new ResponseEntity<>(
                "Representation was saved successfully with uuid " + newUuid,
                HttpStatus.CREATED);
        } catch (ResourceAlreadyExists exception) {
            LOGGER.info("Failed to add resource representation. The representation already exists."
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be added. It already exits.",
                HttpStatus.FOUND);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Failed to add resource representation. The resource does not exist."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to add resource representation. The resource is not valid."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to add resource representation. Something went wrong."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be added.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates resource representation.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @param representation   A new representation.
     * @return OK or an error response.
     */
    @Operation(summary = "Update representation",
        description = "Update a resource's representation by its uuid.")
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "The representation uuid.", required = true)
        @PathVariable("representation-id") UUID representationId,
        @Parameter(description = "A new resource representation.", required = true)
        @RequestBody ResourceRepresentation representation) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}/{representation-id}";
        final var endpointType = "PUT";

        LOGGER.info("Received request for updating a resource representation."
                + " [id=({}), endpoint=({}), requestType=({}), resourceId=({}), representationId=({}), representation=({})]",
            requestId, endpointPath, endpointType, resourceId, representationId, representation);

        try {
            ((OfferedResourceServiceImpl) offeredResourceService)
                .updateRepresentation(resourceId, representationId, representation);
            LOGGER.info("Successfully update a resource representation. [id=({})]", requestId);
            return new ResponseEntity<>("Representation was updated successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            LOGGER.info("Failed to update the resource representation. The resource does not exist."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to update the resource representation. The resource is not valid."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to update the resource representation. Something went wrong."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @return OK or an error response.
     */
    @Operation(summary = "Get Resource Representation", description = "Get the resource's representation by its uuid.")
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "The representation uuid.", required = true)
        @PathVariable("representation-id") UUID representationId) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}/{representation-id}";
        final var endpointType = "GET";

        LOGGER.info("Received request for a resource representation lookup."
                + " [id=({}), endpoint=({}), requestType=({}), resourceId=({}), representationId=({})]",
            requestId, endpointPath, endpointType, resourceId, representationId);

        try {
            final var representation =
                offeredResourceService.getRepresentation(resourceId, representationId);
            LOGGER.info("Successfully received the resource representation. [id=({})]", requestId);
            return new ResponseEntity<>(representation, HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info(
                "Failed to received the resource representation. The resource does not exist. "
                    + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER
                .info("Failed to received the resource representation. The resource is not valid. "
                    + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to received the resource representation. Something went wrong. "
                + "[id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes resource representation.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @return OK or an error response.
     */
    @Operation(summary = "Remove Resource Representation",
        description = "Remove a resource's representation by its uuid.")
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "The representation uuid.", required = true)
        @PathVariable("representation-id") UUID representationId) {
        final var requestId = UUIDUtils.generateUUID();
        final var endpointPath = "/admin/api/resources/{resource-id}/{representation-id}";
        final var endpointType = "DELETE";

        LOGGER.info("Received request for a resource representation deletion."
                + " [id=({}), endpoint=({}), requestType=({}), resourceId=({}), representationId=({})]",
            requestId, endpointPath, endpointType, resourceId, representationId);

        try {
            if (((OfferedResourceServiceImpl) offeredResourceService).deleteRepresentation(resourceId, representationId)) {
                LOGGER
                    .info("Successfully deleted the resource representation. [id=({})]", requestId);
            } else {
                LOGGER.info("Failed to delete the resource representation. [id=({})]", requestId);
            }
            return new ResponseEntity<>("Representation was deleted successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info(
                "Failed to delete the resource representation. The resource could not be found."
                    + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to delete the resource representation. The resource is not valid."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to delete the resource representation. Something went wrong."
                + " [id=({}), exception=({})]", requestId, exception.getMessage());
            return new ResponseEntity<>("The representation could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
