package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/artifacts")
@Tag(name = "Artifacts", description = "Endpoints for CRUD operations on artifacts")
public class ArtifactController extends BaseResourceController<Artifact, ArtifactDesc, ArtifactView,
        ArtifactService> {

    /**
     * Return the data from the database or a remote data source. In case of a remote HTTP data
     * source, the given headers, query parameters and path variables will be used for the request.
     * TODO User should be able to manually trigger artifact request (no mandatory boolean, but optional true)
     *
     * @param artifactId The artifact id.
     * @param headers JSON representation of a map containing additional headers for the backend call.
     * @param params JSON representation of a map containing query parameters for the backend call.
     * @param pathVariables JSON representation of a map containing path variables for the backend call.
     * @return The data object.
     */
    @GetMapping("{id}/data")
    @Operation(summary = "Get data by artifact id with query input")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    public ResponseEntity<Object> getData(@Valid @PathVariable(name = "id") final UUID artifactId,
                                            @RequestParam("headers") final Optional<String> headers,
                                            @RequestParam("params") final Optional<String> params,
                                            @RequestParam("pathVariables") final Optional<String> pathVariables) {
        final var artifactService = this.getService();

        final var queryInput = new QueryInput();
        final var objectMapper = new ObjectMapper();
        final var typeReference = new TypeReference<HashMap<String, String>>() { };
        try {
            if (headers.isPresent()) {
                final var headersMap = objectMapper
                        .readValue(headers.get(), typeReference);
                queryInput.setHeaders(headersMap);
            }
            if (params.isPresent()) {
                final var paramsMap = objectMapper
                        .readValue(params.get(), typeReference);
                queryInput.setParams(paramsMap);
            }
            if (pathVariables.isPresent()) {
                final var pathVariablesMap = objectMapper
                        .readValue(pathVariables.get(), typeReference);
                queryInput.setPathVariables(pathVariablesMap);
            }
        } catch (JsonProcessingException exception) {
            return new ResponseEntity<>("Invalid input.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(artifactService.getData(artifactId, queryInput));
    }
}
