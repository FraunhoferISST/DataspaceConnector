/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.resource.type;

import de.fraunhofer.ids.messaging.protocol.UnexpectedResponseException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.dataretrieval.RetrievalInformation;
import io.dataspaceconnector.common.util.ValidationUtils;
import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceNotificationController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.artifact.ArtifactView;
import io.dataspaceconnector.controller.resource.view.route.RouteView;
import io.dataspaceconnector.controller.resource.view.route.RouteViewAssembler;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.ArtifactRetriever;
import io.dataspaceconnector.service.message.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Offers the endpoints for managing artifacts.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(BasePath.ARTIFACTS)
@Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
public class ArtifactController extends BaseResourceNotificationController<Artifact, ArtifactDesc,
        ArtifactView, ArtifactService> {

    /**
     * The service managing artifacts.
     */
    private final @NonNull ArtifactService artifactSvc;

    /**
     * The receiver for getting data from a remote source.
     */
    private final @NonNull ArtifactRetriever dataReceiver;

    /**
     * The verifier for the data access.
     */
    private final @NonNull DataAccessVerifier accessVerifier;

    /**
     * Service for notifying subscribers about an entity update.
     */
    private final @NonNull SubscriberNotificationService subscriberNotificationSvc;

    /**
     * The assembler for creating a view from  a route.
     */
    private final @NonNull RouteViewAssembler routeAssembler;

    /**
     * Returns data from the local database or a remote data source. In case of a remote data
     * source, all headers and query parameters included in this request will be used for the
     * request to the backend.
     *
     * @param artifactId   Artifact id.
     * @param download     If the data should be forcefully downloaded.
     * @param agreementUri The agreement which should be used for access control.
     * @param routeIds     The routes the data should be sent to.
     * @param params       All request parameters.
     * @param headers      All request headers.
     * @param request      The current http request.
     * @return The data object.
     * @throws IOException if the data cannot be received.
     */
    @GetMapping("{id}/data/**")
    @Operation(summary = "Get data by artifact id with query input.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public ResponseEntity<StreamingResponseBody> getData(
            @Valid @PathVariable(name = "id") final UUID artifactId,
            @RequestParam(required = false) final Boolean download,
            @RequestParam(required = false) final URI agreementUri,
            @RequestParam(required = false) final List<URI> routeIds,
            @RequestParam(required = false) final Map<String, String> params,
            @RequestHeader final Map<String, String> headers,
            final HttpServletRequest request) throws IOException {
        headers.remove("authorization");
        headers.remove("host");

        final var queryInput = new QueryInput();
        queryInput.setParams(params);
        queryInput.setHeaders(headers);

        final var searchString = "/data";
        var optional = request.getRequestURI().substring(
                request.getRequestURI().indexOf(searchString) + searchString.length());
        if ("/**".equals(optional)) {
            optional = "";
        }

        if (!optional.isBlank()) {
            queryInput.setOptional(optional);
        }

        /*
            If no agreement information has been passed the connector needs
            to check if the data access is restricted by the usage control.
         */
        final var data = (agreementUri == null)
                ? artifactSvc.getData(accessVerifier, dataReceiver, artifactId, queryInput,
                routeIds)
                : artifactSvc.getData(accessVerifier, dataReceiver, artifactId,
                new RetrievalInformation(agreementUri, download, queryInput), routeIds);

        return returnData(artifactId, data);
    }

    /**
     * Returns data from the local database or a remote data source. In case of a remote data
     * source, the headers, query parameters and path variables from the request body will be
     * used when fetching the data.
     *
     * @param artifactId Artifact id.
     * @param routeIds   The routes the data should be sent to.
     * @param queryInput Query input containing headers, query parameters, and path variables.
     * @return The data object.
     * @throws IOException                 if the data could not be stored.
     * @throws UnexpectedResponseException if the ids response message has been unexpected.
     */
    @PostMapping("{id}/data")
    @Operation(summary = "Get data by artifact id with query input.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public ResponseEntity<StreamingResponseBody> getData(
            @Valid @PathVariable(name = "id") final UUID artifactId,
            @RequestParam(required = false) final List<URI> routeIds,
            @RequestBody(required = false) final QueryInput queryInput)
            throws IOException,
            UnexpectedResponseException,
            io.dataspaceconnector.common.exception.UnexpectedResponseException {
        ValidationUtils.validateQueryInput(queryInput);
        final var data =
                artifactSvc.getData(accessVerifier, dataReceiver, artifactId, queryInput, routeIds);
        return returnData(artifactId, data);
    }

    private ResponseEntity<StreamingResponseBody> returnData(
            final UUID artifactId, final InputStream data) {
        final StreamingResponseBody body = outputStream -> {
            final int blockSize = 1024;
            int numBytesToWrite;
            var buffer = new byte[blockSize];
            while ((numBytesToWrite = data.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, numBytesToWrite);
            }

            data.close();
        };

        final var outputHeader = new HttpHeaders();
        outputHeader.set("Content-Disposition", "attachment;filename=" + artifactId.toString());

        final var type = getMediaTypeOfArtifact(artifactId);
        return ResponseEntity.ok()
                .headers(outputHeader)
                .contentType(type)
                .body(body);
    }

    private MediaType getMediaTypeOfArtifact(final UUID artifactId) {
        // Get type to set the correct content type.
        // NOTE: Assume that an artifact has only one representation.
        try {
            final var artifact = getService().get(artifactId);
            if (artifact.getRepresentations().isEmpty()
                || artifact.getRepresentations().get(0) == null
                || artifact.getRepresentations().get(0).getMediaType() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("No representation found. Return data as stream.");
                }
            } else {
                final var mediaType = artifact.getRepresentations().get(0).getMediaType();
                return MediaType.parseMediaType("application/" + mediaType);
            }
        } catch (ResourceNotFoundException | InvalidMediaTypeException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not resolve media type. Return data as stream. [exception=({})]",
                        e.getMessage());
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    /**
     * Replace the data of an artifact.
     *
     * @param artifactId  The artifact whose data should be replaced.
     * @param inputStream The new data.
     * @return Http Status ok.
     * @throws IOException if the data could not be stored.
     */
    @PutMapping(value = "{id}/data", consumes = "*/*")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public ResponseEntity<Void> putData(
            @Valid @PathVariable(name = "id") final UUID artifactId,
            @RequestBody final byte[] inputStream) throws IOException {
        artifactSvc.setData(artifactId, new ByteArrayInputStream(inputStream));

        // Notify subscribers on update event.
        subscriberNotificationSvc.notifyOnUpdate(getService().get(artifactId));

        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the route associated with an artifact, if any. Returns an empty response body
     * otherwise.
     *
     * @param artifactId The artifact id.
     * @return Response with code 200 and the associated route, if any.
     */
    @GetMapping("{id}/route")
    @Operation(summary = "Get route associated with artifact by id.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public ResponseEntity<RouteView> getRoute(
            @Valid @PathVariable(name = "id") final UUID artifactId) {
        final var route = artifactSvc.getAssociatedRoute(artifactId);
        return returnRoute(route);
    }

    /**
     * Returns a {@link RouteView} if the route is present and an empty response body otherwise.
     *
     * @param route the route.
     * @return Response with code 200 and the RouteView, if route if not null.
     */
    private ResponseEntity<RouteView> returnRoute(final Route route) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return route == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(routeAssembler.toModel(route), headers, HttpStatus.OK);
    }
}
