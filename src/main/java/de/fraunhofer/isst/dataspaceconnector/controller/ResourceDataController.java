package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * This class provides endpoints for the internal resource handling.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/resources")
@Tag(name = "Backend: Resource Data Handling", description = "Endpoints  for resource data handling")
public class ResourceDataController { // Header: Content-Type: application/json
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceDataController.class);

    private OfferedResourceService offeredResourceService;
    private RequestedResourceService requestedResourceService;

    @Autowired
    /**
     * <p>Constructor for ResourceDataController.</p>
     *
     * @param offeredResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param requestedResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService} object.
     */
    public ResourceDataController(OfferedResourceService offeredResourceService, RequestedResourceService requestedResourceService) {
        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
    }

    /**
     * Publishes the resource's data as a string.
     *
     * @param id   The resource id.
     * @param data The data string.
     * @return Ok or error response.
     * @throws java.lang.IllegalArgumentException If the requested id is not a uuid.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Publish Resource Data String", description = "Publish resource data as string.")
    @RequestMapping(value = "/{resource-id}/data", method = {RequestMethod.PUT}) // , RequestMethod.POST
    // params = { "type=string" } NOT SUPPORTED with OpenAPI
    @ResponseBody
    public ResponseEntity<Object> publishResource(
            @Parameter(description = "The resource uuid.", required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9") @PathVariable("resource-id") UUID id,
            @Parameter(description = "The resource data.", required = true, example = "Data String") @RequestParam("data") String data)
            throws IllegalArgumentException {
        try {
            offeredResourceService.addData(id, data);
            return new ResponseEntity<>("Resource published", HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Resource could not be published: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets resource data as a string.
     *
     * @param id The resource id.
     * @return Raw data or an error response.
     * @throws java.lang.IllegalArgumentException If the requested id is not a uuid.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Request Data String", description = "Get the resource's data as a string.")
    @RequestMapping(value = "/{resource-id}/data", method = RequestMethod.GET)
    // params = {"type=string"} NOT SUPPORTED with OpenAPI
    @ResponseBody
    public ResponseEntity<Object> getDataById(
            @Parameter(description = "The resource uuid.", required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9") @PathVariable("resource-id") UUID id)
            throws IllegalArgumentException {
        try {
            return new ResponseEntity<>(offeredResourceService.getData(id), HttpStatus.OK);
        } catch (Exception e) {
            try {
                return new ResponseEntity<>(requestedResourceService.getData(id), HttpStatus.OK);
            } catch (Exception f) {
                return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * Gets resource data as a string by representation id.
     *
     * @param resourceId The resource id.
     * @param representationId The representation id.
     * @return Raw data or an error response.
     * @throws java.lang.IllegalArgumentException If the requested id is not a uuid.
     * @throws java.lang.IllegalArgumentException if any.
     */
    @Operation(summary = "Request Data String by Representation", description = "Get the resource's data as a string by representation.")
    @RequestMapping(value = "/{resource-id}/{representation-id}/data", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDataByRepresentation(
            @Parameter(description = "The resource uuid.", required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9") @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true) @PathVariable("representation-id") UUID representationId)
            throws IllegalArgumentException {
        try {
            return new ResponseEntity<>(offeredResourceService.getDataByRepresentation(resourceId, representationId), HttpStatus.OK);
        } catch (Exception e) {
            try {
                return new ResponseEntity<>(requestedResourceService.getData(resourceId), HttpStatus.OK);
            } catch (Exception f) {
                return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
            }
        }
    }
}
