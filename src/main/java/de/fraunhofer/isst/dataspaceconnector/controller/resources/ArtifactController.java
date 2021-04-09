package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artifacts")
@Tag(name = "Artifacts", description = "Endpoints for CRUD operations on artifacts")
public class ArtifactController extends BaseResourceController<Artifact, ArtifactDesc, ArtifactView,
        ArtifactService> {

    /**
     * Returns data from the local database or a remote data source. In case of a remote data
     * source, all headers and query parameters included in this request will be used for the
     * request to the backend.
     *
     * TODO User should be able to manually trigger artifact request (no mandatory boolean, but optional true)
     *
     * @param artifactId ID of the artifact
     * @param params all request parameters
     * @param headers all request headers
     * @return the data object
     */
    @GetMapping("{id}/data")
    @Operation(summary = "Get data by artifact id with query input")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    public ResponseEntity<Object> getData(@Valid @PathVariable(name = "id") final UUID artifactId,
                                          @RequestParam final Map<String, String> params,
                                          @RequestHeader final Map<String, String> headers) {
        final var artifactService = this.getService();

        headers.remove("authorization");
        headers.remove("host");

        final var queryInput = new QueryInput();
        queryInput.setParams(params);
        queryInput.setHeaders(headers);

        return ResponseEntity.ok(artifactService.getData(artifactId, queryInput));
    }

    /**
     * Returns data from the local database or a remote data source. In case of a remote data
     * source, the headers, query parameters and path variables from the request body will be
     * used when fetching the data.
     *
     * TODO User should be able to manually trigger artifact request (no mandatory boolean, but optional true)
     *
     * @param artifactId ID of the artifact
     * @param queryInput query input containing headers, query parameters and path variables
     * @return the data object
     */
    @PostMapping("{id}/data")
    @Operation(summary = "Get data by artifact id with query input")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    public ResponseEntity<Object> getData(@Valid @PathVariable(name = "id") final UUID artifactId,
                                          @RequestBody(required = false) QueryInput queryInput) {
        final var artifactService = this.getService();

        ValidationUtils.validateQueryInput(queryInput);

        return ResponseEntity.ok(artifactService.getData(artifactId, queryInput));
    }
}
