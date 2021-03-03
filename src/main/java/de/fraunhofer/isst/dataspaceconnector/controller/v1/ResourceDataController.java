package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationArtifactLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceRepresentationLinker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ResourceDataController {

    private final @NonNull ResourceRepresentationLinker<OfferedResource> resourceRepresentationLinker;

    private final @NonNull RepresentationArtifactLinker representationArtifactLinker;

    private final @NonNull ArtifactService artifactService;

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
            @PathVariable("resource-id") final UUID resourceId,
            @Parameter(description = "The resource data.", required = true, example = "Data String")
            @RequestParam("data") final String data) {

//        final var representations = resourceRepresentationLinker.get(resourceId);
//
//        if (representations.isEmpty()) {
//            throw new ResourceNotFoundException("");
//        }
//
//        final var artifacts =
//                representationArtifactLinker.get((UUID) representations.toArray()[0]);
//
//        if (artifacts.isEmpty()) {
//            throw new ResourceNotFoundException("");
//        }
//
//        final var artifactId = (UUID) artifacts.toArray()[0];
//        final var artifact = artifactService.get(artifactId);
//        final var desc = new ArtifactDesc();
//        desc.setTitle(artifact.getTitle());
//        desc.setValue(data);
//
//        artifactService.update(artifactId, desc);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        throw new RuntimeException("Not implemented");
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
                                              @PathVariable("resource-id") final UUID resourceId) {
//
//        final var representations = resourceRepresentationLinker.get(resourceId);
//
//        if (representations.isEmpty()) {
//            throw new ResourceNotFoundException("");
//        }
//
//        final var artifacts =
//                representationArtifactLinker.get((UUID) representations.toArray()[0]);
//
//        if (artifacts.isEmpty()) {
//            throw new ResourceNotFoundException("");
//        }
//
//        // TODO Add Query
//        return new ResponseEntity<>(artifactService.getData((UUID) artifacts.toArray()[0], null),
//                HttpStatus.OK);


        throw new RuntimeException("Not implemented");
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
            @PathVariable("resource-id") final UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true)
            @PathVariable("representation-id") final UUID representationId) {

//        final var representations = resourceRepresentationLinker.get(resourceId);
//
//        if (representations.isEmpty()) {
//            throw new ResourceNotFoundException("");
//        }
//
//        final var artifacts = representationArtifactLinker.get(representationId);
//
//        if (artifacts.isEmpty()) {
//            throw new ResourceNotFoundException("");
//        }
//
//        // TODO Add Query
//        return new ResponseEntity<>(artifactService.getData((UUID) artifacts.toArray()[0], null), HttpStatus.OK);


        throw new RuntimeException("Not implemented");
    }
}
