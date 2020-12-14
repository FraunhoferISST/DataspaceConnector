package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.exceptions.*;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService;
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

    private final OfferedResourceService offeredResourceService;
    private final RequestedResourceService requestedResourceService;
    private final PolicyHandler policyHandler;

    /**
     * Constructor for ResourceController.
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public ResourceController(@NotNull OfferedResourceService offeredResourceService,
        @NotNull PolicyHandler policyHandler,
        @NotNull RequestedResourceService requestedResourceService)
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
        final var endpointPath = "/admin/api/resources/resource";
        LOGGER.info(
            "Received request for resource creation. [endpoint=({}), uuid=({}), metadata=({})]",
            endpointPath, uuid, resourceMetadata);

        try {
            if (uuid != null) {
                offeredResourceService.addResourceWithId(resourceMetadata, uuid);
                LOGGER.info("Successfully added a new resource."
                        + " [endpoint=({}), uuid=({}), metadata=({})]",
                    endpointPath, uuid, resourceMetadata);
                return new ResponseEntity<>("Resource registered with uuid: " + uuid,
                    HttpStatus.CREATED);
            } else {
                final var new_uuid = offeredResourceService.addResource(resourceMetadata);
                LOGGER.info("Successfully added a new resource."
                        + " [endpoint=({}), uuid=({}), metadata=({})]",
                    endpointPath, null, resourceMetadata);
                return new ResponseEntity<>("Resource registered with uuid: " + new_uuid.toString(),
                    HttpStatus.CREATED);
            }
        } catch (InvalidResourceException exception) {
            LOGGER.info("Failed to add resource. The resource is not valid."
                    + " [endpoint=({}), exception=({}), uuid=({}), metadata=({})]",
                endpointPath, exception.getMessage(), uuid, resourceMetadata);
            return new ResponseEntity<>("The resource could not be added.",
                HttpStatus.NOT_ACCEPTABLE);
        } catch (ResourceAlreadyExists exception) {
            LOGGER.info("Failed to add resource. The resource already exists."
                    + " [endpoint=({}), exception=({}), uuid=({}), metadata=({})]",
                endpointPath, exception.getMessage(), uuid, resourceMetadata);
            return new ResponseEntity<>("The resource could not be added. It already exits.",
                HttpStatus.FOUND);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to add resource. Something went wrong."
                    + " [endpoint=({}), exception=({}), uuid=({}), metadata=({})]",
                endpointPath, exception.getMessage(), uuid, resourceMetadata);
            return new ResponseEntity<>("The resource could not be added.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates resource metadata by id.
     *
     * @param id               The resource id.
     * @param resourceMetadata The updated metadata.
     * @return OK or error response.
     */
    @Operation(summary = "Update Resource", description = "Update the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID id,
        @RequestBody ResourceMetadata resourceMetadata) {
        final var endpointPath = "/admin/api/resources/resource";
        LOGGER.info("Received request for resource updating."
                + " [endpoint=({}), uuid=({}), metadata=({})]",
            endpointPath, id, resourceMetadata);

        try {
            offeredResourceService.updateResource(id, resourceMetadata);
            LOGGER.info("Successfully updated the resource."
                    + " [endpoint=({}), resourceId=({}), metadata=({})]",
                endpointPath, id, resourceMetadata);
            return new ResponseEntity<>("Resource was updated successfully", HttpStatus.OK);
        } catch (InvalidResourceException exception) {
            LOGGER.info("Failed to update the resource. The resource is not valid."
                    + " [endpoint=({}), exception=({}), resourceId=({}), metadata=({})]",
                endpointPath, exception.getMessage(), id, resourceMetadata);
            return new ResponseEntity<>("The resource could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceNotFoundException exception) {
            LOGGER.info("Failed to update the resource. The resource could not be found."
                    + " [endpoint=({}), exception=({}), resourceId=({}), metadata=({})]",
                endpointPath, exception.getMessage(), id, resourceMetadata);
            return new ResponseEntity<>("Resource could not be updated.", HttpStatus.NOT_FOUND);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to update the resource. Something went wrong."
                    + " [endpoint=({}), exception=({}), resourceId=({}), metadata=({})]",
                endpointPath, exception.getMessage(), id, resourceMetadata);
            return new ResponseEntity<>("Resource could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets resource metadata by id.
     *
     * @param id The resource id.
     * @return Metadata or an error response.
     */
    @Operation(summary = "Get Resource", description = "Get the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID id) {
        final var endpointPath = "/admin/api/resources/{resource-id}";
        LOGGER.info("Received request for resource lookup. [endpoint=({}), uuid=({})]",
            endpointPath, id);

        try {
            try {
                // Try to find the data in the offeredResourceService
                final var responseEntity = new ResponseEntity<Object>(
                    offeredResourceService.getMetadata(id), HttpStatus.OK);
                LOGGER.info("Successfully received the resource."
                        + " [endpoint=({}), resourceId=({})]",
                    endpointPath, id);
                return responseEntity;
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                LOGGER.debug("Failed to receive the resource from the OfferedResourcesService."
                        + " [endpoint=({}), exception=({}), resourceId=({})]",
                    endpointPath, offeredResourceServiceException.getMessage(), id);
                try {
                    // Try to find the data in the requestedResourceService
                    final var responseEntity = new ResponseEntity<Object>(
                        requestedResourceService.getMetadata(id), HttpStatus.OK);
                    LOGGER.info("Successfully received the resource. "
                            + "[endpoint=({}), resourceId=({})]",
                        endpointPath, id);
                    return responseEntity;
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    LOGGER
                        .debug("Failed to receive the resource from the RequestedResourcesService."
                                + " [endpoint=({}),exception=({}), resourceId=({})]",
                            endpointPath, requestedResourceServiceException.getMessage(), id);
                    // The data could not be found in the offeredResourceService and requestedResourceService
                    LOGGER.info("Failed to receive the resource. The resource does not exist. "
                            + "[endpoint=({}), exception=({}), resourceId=({})]",
                        endpointPath, requestedResourceServiceException.getMessage(), id);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to receive the resource. The resource is not valid."
                    + " [endpoint=({}), exception=({}), resourceId=({})]",
                endpointPath, exception.getMessage(), id);
            return new ResponseEntity<>(
                "The resource could not be received. Not a valid resource format.",
                HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to receive the resource. Something went wrong. "
                    + "[endpoint=({}), exception=({}) ,resourceId=({})]",
                endpointPath, exception.getMessage(), id);
            return new ResponseEntity<>("Resource could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes resource by id.
     *
     * @param id The resource id.
     * @return OK or error response.
     */
    @Operation(summary = "Delete Resource", description = "Delete a resource by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID id) {
        final var endpointPath = "/admin/api/resources/{resource-id}";
        LOGGER.info("Received request for resource deletion."
            + " [endpoint=({}), uuid=({})]", endpointPath, id);

        if (offeredResourceService.deleteResource(id)) {
            LOGGER.info("Successfully deleted resource from the OfferedResourcesService."
                + " [endpoint=({}), uuid=({})]", endpointPath, id);
            return new ResponseEntity<>("Resource was deleted successfully.",
                HttpStatus.OK);
        } else {
            LOGGER.debug("Failed to delete the resource from the OfferedResourcesService."
                + " [endpoint=({}), resourceId=({})]", endpointPath, id);
            if (requestedResourceService.deleteResource(id)) {
                LOGGER.info("Successfully deleted resource from the RequestedResourcesService."
                    + " [endpoint=({}), uuid=({})]", endpointPath, id);
                return new ResponseEntity<>("Resource was deleted successfully.",
                    HttpStatus.OK);
            } else {
                LOGGER.debug("Failed to delete the resource from the RequestedResourcesService."
                    + " [endpoint=({}), resourceId=({})]", endpointPath, id);
                LOGGER.info("Failed to delete the resource. The resource does not exist."
                    + " [endpoint=({}), uuid=({})]", endpointPath, id);
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
        final var endpointPath = "/admin/api/resources/{resource-id}";
        LOGGER.info("Received request for updating a resource contract."
                + " [endpoint=({}), uuid=({}), policy=({})]",
            endpointPath, resourceId, policy);

        try {
            policyHandler.getPattern(policy);
            offeredResourceService.updateContract(resourceId, policy);
            LOGGER.info("Successfully updated a resource contract. "
                    + "[endpoint=({}), uuid=({}), policy=({})]",
                endpointPath, resourceId, policy);
            return new ResponseEntity<>("Contract was updated successfully", HttpStatus.OK);
        } catch (IOException exception) {
            // The policy is not in the correct format.
            LOGGER.info("Failed to update the resource contract. The policy is malformed. "
                    + "[endpoint=({}), exception=({}), uuid=({}), policy=({})]",
                endpointPath, exception.getMessage(), resourceId, policy);
            return new ResponseEntity<>("Policy syntax error.", HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Failed to update the resource contract. The resource does not exist. "
                    + "[endpoint=({}), exception=({}), uuid=({}), policy=({})]",
                endpointPath, exception.getMessage(), resourceId, policy);
            return new ResponseEntity<>("The resource could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to update the resource contract. The resource is not valid. "
                    + "[endpoint=({}), exception=({}), uuid=({}), policy=({})]",
                endpointPath, exception.getMessage(), resourceId, policy);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceException exception) {
            LOGGER.warn(
                "Failed to update the resource contract. Something went wrong. "
                    + "[endpoint=({}), exception=({}), uuid=({}), policy=({})]",
                endpointPath, exception.getMessage(), resourceId, policy);
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
        final var endpointPath = "/admin/api/resources/{resource-id}/contract";
        LOGGER.info("Received request for a resource contract lookup. [endpoint=({}), uuid=({})]",
            endpointPath, resourceId);

        try {
            try {
                // Try to find the data in the offeredResourceService
                final var policy = offeredResourceService.getMetadata(resourceId).getPolicy();
                LOGGER.info(
                    "Successfully received the resource contract. [endpoint=({}), uuid=({})]",
                    endpointPath, resourceId);
                return new ResponseEntity<>(policy, HttpStatus.OK);
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                LOGGER.debug("Failed to receive the resource from the OfferedResourcesService."
                        + " [endpoint=({}), exception=({}), resourceId=({})]",
                    endpointPath, offeredResourceServiceException.getMessage(), resourceId);
                try {
                    // Try to find the data in the requestedResourceService
                    final var policy = requestedResourceService.getMetadata(resourceId).getPolicy();
                    LOGGER.info("Successfully received the resource contract. "
                        + "[endpoint=({}), uuid=({})]", endpointPath, resourceId);
                    return new ResponseEntity<>(policy, HttpStatus.OK);
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    // The data could not be found in the offeredResourceService and requestedResourceService
                    LOGGER
                        .debug("Failed to receive the resource from the RequestedResourcesService."
                                + " [endpoint=({}), exception=({}), resourceId=({})]",
                            endpointPath, requestedResourceServiceException.getMessage(),
                            resourceId);
                    LOGGER.info(
                        "Failed to receive the resource contract. The resource does not exist."
                            + " [endpoint=({}), exception=({}), resourceId=({})]",
                        endpointPath, requestedResourceServiceException.getMessage(), resourceId);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Failed to receive the resource contract. The resource does not exist."
                    + " [endpoint=({}), exception=({}), resourceId=({})]",
                endpointPath, exception.getMessage(), resourceId);
            return new ResponseEntity<>("The resource could not be found.", HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to receive the resource contract. The resource is not valid. "
                    + "[endpoint=({}), exception=({}), uuid=({})]",
                endpointPath, exception.getMessage(), resourceId);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn(
                "Failed to receive the resource contract. Something went wrong. "
                    + "[endpoint=({}), exception=({}), uuid=({})]",
                endpointPath, exception.getMessage(), resourceId);
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
        final var endpointPath = "/admin/api/resources/{resource-id}/access";
        LOGGER.info("Received request for a resource access lookup. [endpoint=({}), uuid=({})]",
            endpointPath, resourceId);

        try {
            final var resource = requestedResourceService.getResource(resourceId);
            if (resource == null) {
                LOGGER.info("Failed to received the resource access. The resource does not exist. "
                    + "[endpoint=({}), uuid=({})]", endpointPath, resourceId);
                return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
            }

            LOGGER.info("Successfully received the resource access. [endpoint=({}), uuid=({})]",
                endpointPath, resourceId);
            return new ResponseEntity<>(resource.getAccessed(), HttpStatus.OK);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to receive the resource access. The resource is not valid. "
                    + "[endpoint=({}), exception=({}), uuid=({})]",
                endpointPath, exception.getMessage(), resourceId);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to receive the resource access. Something went wrong."
                    + " [endpoint=({}), exception=({}), uuid=({})]",
                endpointPath, exception.getMessage(), resourceId);
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
        final var endpointPath = "/admin/api/resources/{resource-id}/representation";
        LOGGER.info("Received request for adding a resource representation."
                + " [endpoint=({}), resourceId=({}), uuid=({}), representation=({})]",
            endpointPath, resourceId, uuid, representation);

        try {
            if (uuid != null) {
                offeredResourceService.addRepresentationWithId(resourceId, representation, uuid);
                LOGGER.info("Successfully added a new resource representation. "
                        + "[endpoint=({}), resourceId=({}), uuid=({}), representation=({})]",
                    endpointPath, resourceId, uuid, representation);
            } else {
                uuid = offeredResourceService.addRepresentation(resourceId, representation);
                LOGGER.info("Successfully added a new resource representation. "
                        + "[endpoint=({}), resourceId=({}), uuid=({}), representation=({})]",
                    endpointPath, resourceId, null, representation);
            }

            return new ResponseEntity<>("Representation was saved successfully with uuid " + uuid,
                HttpStatus.CREATED);
        } catch (ResourceAlreadyExists exception) {
            LOGGER.info("Failed to add resource representation. The representation already exists."
                    + "[endpoint=({}), exception=({}), resourceId=({}), uuid=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, uuid, representation);
            return new ResponseEntity<>("The representation could not be added. It already exits.",
                HttpStatus.FOUND);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Failed to add resource representation. The resource does not exist."
                    + "[endpoint=({}), exception=({}), resourceId=({}), uuid=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, uuid, representation);
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to add resource representation. The resource is not valid."
                    + "[endpoint=({}), exception=({}), resourceId=({}), uuid=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, uuid, representation);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to add resource representation. Something went wrong."
                    + "[endpoint=({}), exception=({}), resourceId=({}), uuid=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, uuid, representation);
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
        final var endpointPath = "/admin/api/resources/{resource-id}/{representation-id}";
        LOGGER.info("Received request for updating a resource representation."
                + " [endpoint=({}), resourceId=({}), representationId=({}), representation=({})]",
            endpointPath, resourceId, representationId, representation);

        try {
            offeredResourceService
                .updateRepresentation(resourceId, representationId, representation);
            LOGGER.info("Successfully update a resource representation."
                    + " [endpoint=({}), resourceId=({}), representationId=({}), representation=({})]",
                endpointPath, resourceId, representationId, representation);
            return new ResponseEntity<>("Representation was updated successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            LOGGER.info(
                "Failed to update the resource representation. The resource does not exist."
                    + " [endpoint=({}), exception=({}) resourceId=({}), representationId=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId,
                representation);
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to update the resource representation. The resource is not valid."
                    + " [endpoint=({}), exception=({}) resourceId=({}), representationId=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId, representation);
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to update the resource representation. Something went wrong."
                    + " [endpoint=({}), exception=({}) resourceId=({}), representationId=({}), representation=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId, representation);
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
        final var endpointPath = "/admin/api/resources/{resource-id}/{representation-id}";
        LOGGER.info("Received request for a resource representation lookup."
                + " [endpoint=({}), resourceId=({}), representationId=({})]",
            endpointPath, resourceId, representationId);

        try {
            final var representation =
                offeredResourceService.getRepresentation(resourceId, representationId);
            LOGGER.info("Successfully received the resource representation."
                    + " [endpoint=({}), resourceId=({}), representationId=({})]",
                endpointPath, resourceId, representationId);
            return new ResponseEntity<>(representation, HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info(
                "Failed to received the resource representation. The resource does not exist. "
                    + "[endpoint=({}), exception=({}), resourceId=({}), representationId=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId);
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER
                .info("Failed to received the resource representation. The resource is not valid. "
                        + "[endpoint=({}), exception=({}), resourceId=({}), representationId=({})]",
                    endpointPath, exception.getMessage(), resourceId, representationId);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to received the resource representation. Something went wrong. "
                    + "[endpoint=({}), exception=({}), resourceId=({}), representationId=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId);
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
        final var endpointPath = "/admin/api/resources/{resource-id}/{representation-id}";
        LOGGER.info("Received request for a resource representation deletion."
                + " [endpoint=({}), resourceId=({}), representationId=({})]",
            endpointPath, resourceId, representationId);
        try {
            if (offeredResourceService.deleteRepresentation(resourceId, representationId)) {
                LOGGER.info("Successfully deleted the resource representation. "
                        + "[endpoint=({}), resourceId=({}), representationId=({})]",
                    endpointPath, resourceId, representationId);
            } else {
                LOGGER.info("Failed to delete the resource representation. "
                        + "[endpoint=({}), resourceId=({}), representationId=({})]",
                    endpointPath, resourceId, representationId);
            }

            return new ResponseEntity<>("Representation was deleted successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info(
                "Failed to delete the resource representation. The resource could not be found."
                    + "[endpoint=({}), exception=({}), resourceId=({}), representationId=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId);
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.info("Failed to delete the resource representation. The resource is not valid."
                    + "[endpoint=({}), exception=({}), resourceId=({}), representationId=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to delete the resource representation. Something went wrong."
                    + "[endpoint=({}), exception=({}), resourceId=({}), representationId=({})]",
                endpointPath, exception.getMessage(), resourceId, representationId);
            return new ResponseEntity<>("The representation could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
