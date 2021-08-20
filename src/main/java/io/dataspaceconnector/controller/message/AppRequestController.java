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
package io.dataspaceconnector.controller.message;

import de.fraunhofer.iais.eis.AppRepresentation;
import de.fraunhofer.iais.eis.AppResource;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.InvalidInputException;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.RdfBuilderException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.controller.resource.view.app.AppViewAssembler;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.service.ArtifactDataDownloader;
import io.dataspaceconnector.service.MetadataDownloader;
import io.dataspaceconnector.service.resource.type.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This controller provides the endpoint for sending an app request message and starting the
 * metadata and data exchange.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
public class AppRequestController {

    /**
     * Downloads metadata.
     */
    private final @NonNull MetadataDownloader metadataDownloader;

    /**
     * The artifact request service.
     */
    private final @NonNull ArtifactDataDownloader artifactDataDownloader;

    /**
     * Service for managing apps.
     */
    private final @NonNull AppService appSvc;

    /**
     * Assemblers DTOs for apps.
     */
    private final @NonNull AppViewAssembler appViewAssembler;

    /**
     * Add an apps metadata to an app object.
     *
     * @param recipient The recipient url
     * @param appId       The app Id.
     * @return Success, when app can be found and created from recipient response.
     */
    @PostMapping("/app")
    @Operation(summary = "Download IDS app from AppStore")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    @Transactional
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The app id.", required = true)
            @RequestParam(value = "appId") final URI appId) {

        // Send description request message and save the app
        try {
            final var app = metadataDownloader.downloadAppResource(recipient, appId);
            final var instanceId = getInstanceId(app);

            // Send artifact request message
            if (instanceId.isPresent()) {
                artifactDataDownloader.downloadTemplate(recipient, instanceId.get(), appId);
                return respondWithCreatedApp(appId);
            } else {
                return ResponseEntity.internalServerError().body("Could not download app data.");
            }
        } catch (InvalidInputException exception) {
            // If the input rules are malformed.
            return ResponseUtils.respondInvalidInput(exception);
        } catch (ConstraintViolationException | RdfBuilderException exception) {
            // If contract request could not be built.
            return ResponseUtils.respondFailedToBuildContractRequest(exception);
        } catch (PersistenceException exception) {
            // If metadata, data, or contract agreement could not be stored.
            return ResponseUtils.respondFailedToStoreEntity(exception);
        } catch (MessageException exception) {
            return ResponseUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException e) {
            // If the response message is invalid or malformed.
            return ResponseUtils.respondReceivedInvalidResponse(e);
        } catch (ContractException e) {
            // If the contract agreement is invalid.
            return ResponseUtils.respondNegotiationAborted();
        } catch (UnexpectedResponseException e) {
            // If the response is not as expected.
            return ResponseUtils.respondWithContent(e.getContent());
        }
    }

    /**
     * Get instance Id (used for receiving app artifact from AppStore).
     *
     * @param appResource The app resource.
     * @return The instance Id or null.
     */
    private Optional<URI> getInstanceId(final AppResource appResource) {
        if (appResource != null && appResource.getRepresentation() != null) {
            final var representations = appResource.getRepresentation().stream()
                    .filter(x -> x instanceof AppRepresentation)
                    .map(x -> (AppRepresentation) x)
                    .collect(Collectors.toList());
            if (!representations.isEmpty()) {
                final var instance = representations.get(0).getInstance();
                if (instance != null && !instance.isEmpty()) {
                    return Optional.of(instance.get(0).getId());
                }
            }
        }
        return Optional.empty();
    }

    private ResponseEntity<Object> respondWithCreatedApp(final URI remoteId) {
        final var app = appSvc.identifyByRemoteId(remoteId);
        if (app.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final var entity = appViewAssembler.toModel(appSvc.get(app.get()));

        final var headers = new HttpHeaders();
        headers.setLocation(entity.getRequiredLink("self").toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }
}
