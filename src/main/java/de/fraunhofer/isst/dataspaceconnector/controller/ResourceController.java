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
 *
 * @version $Id: $Id
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
     * Constructor
     *
     * @param offeredResourceService   a {@link OfferedResourceService} object.
     * @param requestedResourceService a {@link RequestedResourceService} object.
     * @param policyHandler            a {@link PolicyHandler} object.
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
        try {
            if (uuid != null) {
                offeredResourceService.addResourceWithId(resourceMetadata, uuid);
                return new ResponseEntity<>("Resource registered with uuid: " + uuid,
                    HttpStatus.CREATED);
            } else {
                final var new_uuid = offeredResourceService.addResource(resourceMetadata);
                return new ResponseEntity<>("Resource registered with uuid: " + new_uuid.toString(),
                    HttpStatus.CREATED);
            }
        } catch (InvalidResourceException exception) {
            LOGGER.warn("The resource could not be added. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be added.",
                HttpStatus.NOT_ACCEPTABLE);
        } catch (ResourceAlreadyExists exception) {
            LOGGER.info("The resource could not be added. It already exists.", exception);
            return new ResponseEntity<>("The resource could not be added. It already exits.",
                HttpStatus.FOUND);
        } catch (ResourceException exception) {
            LOGGER.error("The resource could not be added. Something went wrong.", exception);
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
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Update Resource", description = "Update the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID id,
        @RequestBody ResourceMetadata resourceMetadata) throws IllegalArgumentException {
        try {
            offeredResourceService.updateResource(id, resourceMetadata);
            return new ResponseEntity<>("Resource was updated successfully", HttpStatus.OK);
        } catch (InvalidResourceException exception) {
            LOGGER.warn("The resource could not be updated. The resource is not valid.", exception);
            return new ResponseEntity<>("The resource could not be updated.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceNotFoundException exception) {
            LOGGER.info("The resource could not be updated. It could not be found.", exception);
            return new ResponseEntity<>("Resource could not be updated.", HttpStatus.NOT_FOUND);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        try {
            try {
                // Try to find the data in the offeredResourceService
                return new ResponseEntity<>(offeredResourceService.getMetadata(id),
                    HttpStatus.OK);
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                try {
                    // Try to find the data in the requestedResourceService
                    LOGGER.info("Could not find the resource %s in the offered resources.",
                        offeredResourceServiceException);
                    return new ResponseEntity<>(requestedResourceService.getMetadata(id),
                        HttpStatus.OK);
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    // The data could not be found in the offeredResourceService and requestedResourceService
                    LOGGER.warn(String.format("Could not find the resource %s in the requested " +
                            "resources.", id),
                        offeredResourceServiceException);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        if (offeredResourceService.deleteResource(id)) {
            LOGGER.info(String.format("Deleted resource %s from offeredResourceService", id));
            return new ResponseEntity<>("Resource was deleted successfully from the " +
                "offeredResourceService.",
                HttpStatus.OK);
        } else {
            if (requestedResourceService.deleteResource(id)) {
                LOGGER.info(String.format("Deleted resource %s from requestedResourceService", id));
                return new ResponseEntity<>("Resource was deleted successfully from the " +
                    "requestedResourceService.",
                    HttpStatus.OK);
            } else {
                LOGGER.warn(String.format("Resource %s could not be found.", id));
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
        @RequestBody String policy) throws IllegalArgumentException {
        try {
            policyHandler.getPattern(policy);
            offeredResourceService.updateContract(resourceId, policy);
            return new ResponseEntity<>("Contract was updated successfully", HttpStatus.OK);
        } catch (IOException exception) {
            // The policy is not in the correct format.
            LOGGER.info("The policy is malformed.");
            return new ResponseEntity<>("Policy syntax error.", HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.warn("Resource could not be found. " + resourceId);
            return new ResponseEntity<>("The resource could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        @PathVariable("resource-id") UUID resourceId) throws IllegalArgumentException {
        try {
            try {
                // Try to find the data in the offeredResourceService
                return new ResponseEntity<>(
                    offeredResourceService.getMetadata(resourceId).getPolicy(), HttpStatus.OK);
            } catch (ResourceNotFoundException offeredResourceServiceException) {
                try {
                    // Try to find the data in the requestedResourceService
                    LOGGER.info("Could not find the resource %s in the offered resources.",
                        offeredResourceServiceException);
                    return new ResponseEntity<>(
                        requestedResourceService.getMetadata(resourceId).getPolicy(),
                        HttpStatus.OK);
                } catch (ResourceNotFoundException requestedResourceServiceException) {
                    // The data could not be found in the offeredResourceService and requestedResourceService
                    LOGGER.warn("Could not find the resource %s in the requested resources.",
                        offeredResourceServiceException);
                    return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("Resource could not be found. " + resourceId);
            return new ResponseEntity<>("The resource could not be found.", HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        try {
            final var resource = requestedResourceService.getResource(resourceId);
            if (resource == null) {
                return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(resource.getAccessed(), HttpStatus.OK);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        try {
            if (uuid != null) {
                offeredResourceService.addRepresentationWithId(resourceId, representation, uuid);
            } else {
                uuid = offeredResourceService.addRepresentation(resourceId, representation);
            }

            return new ResponseEntity<>("Representation was saved successfully with uuid " + uuid,
                HttpStatus.CREATED);
        } catch (ResourceAlreadyExists exception) {
            LOGGER.info("The representation could not be added. It already exists.", exception);
            return new ResponseEntity<>("The representation could not be added. It already exits.",
                HttpStatus.FOUND);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info("The representation could not be found. " + resourceId);
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        try {
            offeredResourceService
                .updateRepresentation(resourceId, representationId, representation);
            return new ResponseEntity<>("Representation was updated successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            LOGGER.info(String.format("The Resource representation %s could not be found.",
                resourceId));
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        try {
            final var representation = offeredResourceService.getRepresentation(resourceId,
                representationId);
            return new ResponseEntity<>(representation, HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info(
                String.format("The Resource representation %s could not be found.", resourceId));
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
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
        try {
            offeredResourceService.deleteRepresentation(resourceId, representationId);
            return new ResponseEntity<>("Representation was deleted successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            // The resource could not be found.
            LOGGER.info(
                String.format("The Resource representation %s could not be found.", resourceId));
            return new ResponseEntity<>("The representation could not be found.",
                HttpStatus.NOT_FOUND);
        } catch (InvalidResourceException exception) {
            // The resource has been found but is in an invalid format.
            LOGGER.warn("The resource could not be received. The resource is not valid.",
                exception);
            return new ResponseEntity<>("The resource could not be received. Not a " +
                "valid resource format.", HttpStatus.EXPECTATION_FAILED);
        } catch (ResourceException exception) {
            LOGGER.warn("Caught unhandled resource exception.", exception);
            return new ResponseEntity<>("The representation could not be received.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
