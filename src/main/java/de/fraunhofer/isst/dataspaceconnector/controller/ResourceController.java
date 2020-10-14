package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * This class provides endpoints for the internal resource handling. Resources can be created and modified with it's
 * {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} including {@link de.fraunhofer.iais.eis.Contract}
 * and {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation}.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/resources")
@Tag(name = "Connector: Resource Handling", description = "Endpoints  for resource handling")
public class ResourceController {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    private OfferedResourceService offeredResourceService;
    private RequestedResourceService requestedResourceService;

    private PolicyHandler policyHandler;

    @Autowired
    /**
     * <p>Constructor for ResourceController.</p>
     *
     * @param offeredResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param requestedResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService} object.
     * @param policyHandler a {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler} object.
     */
    public ResourceController(OfferedResourceService offeredResourceService,
                              PolicyHandler policyHandler, RequestedResourceService requestedResourceService) {
        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
        this.policyHandler = policyHandler;
    }

    /**
     * Registers a resource with its metadata and, if wanted, with an already existing id.
     *
     * @param resourceMetadata The resource metadata.
     * @param uuid The resource uuid.
     * @return The added uuid.
     */
    @Operation(summary = "Register Resource", description = "Register a resource by its metadata.")
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createResource(@RequestBody ResourceMetadata resourceMetadata, @RequestParam(value = "id", required = false) UUID uuid) {
        try {
            if (uuid != null) {
                offeredResourceService.addResourceWithId(resourceMetadata, uuid);
                return new ResponseEntity<>("Resource registered with uuid: " + uuid, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Resource registered with uuid: " + offeredResourceService.addResource(resourceMetadata), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            LOGGER.error("Resource could not be registered: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<Object> updateResource(@Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") UUID id, @RequestBody ResourceMetadata resourceMetadata) throws IllegalArgumentException {
        try {
            offeredResourceService.updateResource(id, resourceMetadata);
            return new ResponseEntity<>("Resource was updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Resource could not be updated: {}", e.getMessage());
            return new ResponseEntity<>("Resource could not be updated: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets resource metadata by id.
     *
     * @param id The resource id.
     * @return Matadata or an error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Get Resource", description = "Get the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getResource(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID id) throws IllegalArgumentException {
        try {
            return new ResponseEntity<>(offeredResourceService.getMetadata(id), HttpStatus.OK);
        } catch (Exception e) {
            try {
                return new ResponseEntity<>(requestedResourceService.getMetadata(id), HttpStatus.OK);
            } catch (Exception f) {
                return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * Deletes resource by id.
     *
     * @param id The resource id.
     * @return OK or error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Delete Resource", description = "Delete a resource by its uuid.")
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteResource(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID id) throws IllegalArgumentException {
        try {
            offeredResourceService.deleteResource(id);
            return new ResponseEntity<>("Resource was deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            try {
                requestedResourceService.deleteResource(id);
                return new ResponseEntity<>("Resource was deleted successfully", HttpStatus.OK);
            } catch (Exception f) {
                LOGGER.error("Resource could not be deleted: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Updates usage policy.
     *
     * @param resourceId The resource id.
     * @return OK or an error response.
     * @param policy The resource's usage policy as string.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Update Resource Contract", description = "Update the resource's usage policy.")
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateContract(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "A new resource contract.", required = true) @RequestBody String policy) throws IllegalArgumentException {
        try {
            policyHandler.getPattern(policy);
        } catch (IOException e) {
            return new ResponseEntity<>("Policy syntax error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            offeredResourceService.updateContract(resourceId, policy);
            return new ResponseEntity<>("Contract was updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Gets usage policy.
     *
     * @param resourceId The resource id.
     * @return Contract or an error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Get Resource Contract", description = "Get the resource's usage policy.")
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getContract(@Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId) throws IllegalArgumentException {
        try {
            return new ResponseEntity<>(offeredResourceService.getMetadata(resourceId).getPolicy(), HttpStatus.OK);
        } catch (Exception e) {
            try {
                return new ResponseEntity<>(requestedResourceService.getMetadata(resourceId).getPolicy(), HttpStatus.OK);
            } catch (Exception f) {
                return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * <p>getAccess.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Get Data Access", description = "Get the number of the resource's data access.")
    @RequestMapping(value = "/{resource-id}/access", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAccess(@Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId) throws IllegalArgumentException {
        try {
            return new ResponseEntity<>(requestedResourceService.getResource(resourceId).getAccessed(), HttpStatus.OK);
        } catch (Exception f) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId The resource id.
     * @param representation A new representation.
     * @return OK or an error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Get Resource Contract", description = "Get the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}/representation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addRepresentation(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "A new resource representation.", required = true) @RequestBody ResourceRepresentation representation) throws IllegalArgumentException {
        try {
            UUID uuid = offeredResourceService.addRepresentation(resourceId, representation);
            return new ResponseEntity<>("Representation was saved successfully with uuid: " + uuid, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates resource representation.
     *
     * @param resourceId The resource id.
     * @param representationId The representation id.
     * @param representation A new representation.
     * @return OK or an error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Get Resource Contract", description = "Get the resource's metadata by its uuid.")
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateRepresentation(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true) @PathVariable("representation-id") UUID representationId,
            @Parameter(description = "A new resource representation.", required = true) @RequestBody ResourceRepresentation representation) throws IllegalArgumentException {
        try {
            offeredResourceService.updateRepresentation(resourceId, representationId, representation);
            return new ResponseEntity<>("Representation was updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId The resource id.
     * @param representationId The representation id.
     * @return OK or an error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Get Resource Representation", description = "Get the resource's representation by its uuid.")
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getRepresentation(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true) @PathVariable("representation-id") UUID representationId) throws IllegalArgumentException {
        try {
            return new ResponseEntity<>(offeredResourceService.getRepresentation(resourceId, representationId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Removes resource representation.
     *
     * @param resourceId The resource id.
     * @param representationId The representation id.
     * @return OK or an error response.
     * @throws java.lang.IllegalArgumentException if any.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Remove Resource Representation", description = "Get the resource's representation by its uuid.")
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteRepresentation(
            @Parameter(description = "The resource uuid.", required = true) @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true) @PathVariable("representation-id") UUID representationId) throws IllegalArgumentException {
        try {
            offeredResourceService.deleteRepresentation(resourceId, representationId);
            return new ResponseEntity<>("Representation was deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        }
    }
}
