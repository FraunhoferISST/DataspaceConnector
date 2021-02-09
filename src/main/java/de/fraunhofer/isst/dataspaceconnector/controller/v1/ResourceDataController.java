package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v1.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v1.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.ArtifactBFFService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFRepresentationArtifactLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFResourceRepresentationLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.Basepaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * This class provides endpoints for the internal resource handling.
 */
@RestController
@RequestMapping("/api/v1/resources")
@Tag(name = "Resource Handling")
public class ResourceDataController {

    @Autowired
    private BFFResourceRepresentationLinker resourceRepresentationLinker;

    @Autowired
    private BFFRepresentationArtifactLinker representationArtifactLinker;

    @Autowired
    private ArtifactBFFService artifactService;

    /**
     * Constructor for ResourceDataController.
     *
     * @param offeredResourceService   The service for the offered resources
     * @param requestedResourceService The service for the requested resources
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public ResourceDataController(OfferedResourceServiceImpl offeredResourceService,
                                  RequestedResourceServiceImpl requestedResourceService) throws IllegalArgumentException {
        if (offeredResourceService == null) {
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");
        }

        if (requestedResourceService == null) {
            throw new IllegalArgumentException("The RequestedResourceService cannot be null.");
        }
    }

    /**
     * Publishes the resource's data as a string.
     *
     * @param resourceId The resource id.
     * @param data       The data string.
     * @return Ok or error response.
     */
    @Operation(summary = "Add local data string",
            description = "Add local data string to resource.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created"),
            @ApiResponse(responseCode = "400", description = "Invalid resource"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/data", method = {RequestMethod.PUT})
    @ResponseBody
    public ResponseEntity<Void> publishResource(
            @Parameter(description = "The resource uuid.", required = true,
                    example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The resource data.", required = true, example = "Data String")
            @RequestParam("data") String data) {

        final var representations =
                resourceRepresentationLinker.get(new EndpointId(Basepaths.Resources.toString(),
                        resourceId));

        if (representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var artifacts =
                representationArtifactLinker.get((EndpointId) representations.toArray()[0]);

        if (artifacts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var endpointId = (EndpointId) artifacts.toArray()[0];
        final var artifact = artifactService.get(endpointId);
        final var desc = new ArtifactDesc();
        desc.setTitle(artifact.getTitle());
        desc.setValue(data);

        artifactService.update(endpointId, desc);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Gets resource data as a string.
     *
     * @param resourceId The resource id.
     * @return Raw data or an error response.
     */
    @Operation(summary = "Request data string",
            description = "Get the resource's data as a string.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/data", method = RequestMethod.GET)
    // params = {"type=string"} NOT SUPPORTED with OpenAPI
    @ResponseBody
    public ResponseEntity<Object> getDataById(@Parameter(description = "The resource uuid.",
            required = true, example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
                                              @PathVariable("resource-id") UUID resourceId) {

        final var representations =
                resourceRepresentationLinker.get(new EndpointId(Basepaths.Resources.toString(),
                        resourceId));

        if (representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var artifacts =
                representationArtifactLinker.get((EndpointId) representations.toArray()[0]);

        if (artifacts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        return new ResponseEntity<>(artifactService.getData((EndpointId) artifacts.toArray()[0]),
                HttpStatus.OK);
    }

    /**
     * Gets resource data as a string by representation id.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @return Raw data or an error response.
     */
    @Operation(summary = "Request data string by representation",
            description = "Get the resource's data as a string by representation.", deprecated =
            true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/{representation-id}/data", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDataByRepresentation(
            @Parameter(description = "The resource uuid.", required = true,
                    example = "a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @PathVariable("resource-id") UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true)
            @PathVariable("representation-id") UUID representationId) {

        final var representations =
                resourceRepresentationLinker.get(new EndpointId(Basepaths.Resources.toString(),
                        resourceId));

        if (representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var artifacts =
                representationArtifactLinker.get(new EndpointId(Basepaths.Representations.toString(), representationId));

        if (artifacts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        return new ResponseEntity<>(artifactService.getData((EndpointId) artifacts.toArray()[0]), HttpStatus.OK);
    }
}
