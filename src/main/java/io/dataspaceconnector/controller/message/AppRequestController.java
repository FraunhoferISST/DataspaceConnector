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

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.InvalidInputException;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.RdfBuilderException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.controller.message.tag.MessageDescription;
import io.dataspaceconnector.controller.message.tag.MessageName;
import io.dataspaceconnector.controller.resource.view.app.AppViewAssembler;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.service.ArtifactDataDownloader;
import io.dataspaceconnector.service.MetadataDownloader;
import io.dataspaceconnector.service.message.AppStoreCommunication;
import io.dataspaceconnector.service.resource.type.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

/**
 * This controller provides the endpoint for sending an app request message and starting the
 * metadata and data exchange.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = MessageName.MESSAGES, description = MessageDescription.MESSAGES)
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
     * Service for handling app store logic.
     */
    private final @NonNull AppStoreCommunication appStoreCommunication;

    /**
     * Add an apps metadata to an app object.
     *
     * @param recipient The recipient url
     * @param appId     The app Id.
     * @return Success, when app can be found and created from recipient response.
     */
    @PostMapping("/app")
    @Operation(summary = "Download an IDS app from an IDS AppStore.")
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
        // Check if input was an appStore id or an url.
        final var appStore = appStoreCommunication.checkInput(recipient);
        var address = recipient;
        if (appStore.isPresent()) {
            // If an app store could be found, use its location as address.
            address = appStore.get().getLocation();
        }

        // Send description request message and save the AppResource's metadata.
        try {
            final var artifactId = metadataDownloader.downloadAppResource(address, appId, appStore);

            // Send artifact request message to download the AppResource's data.
            try {
                artifactDataDownloader.downloadTemplate(address, artifactId, appId);
            } catch (UnexpectedResponseException | MessageException | PersistenceException e) {
                // Remove app if no corresponding data could be downloaded.
                final var app = appSvc.identifyByRemoteId(appId);
                app.ifPresent(appSvc::delete);
                if (log.isDebugEnabled()) {
                    log.debug("Failed to download app data. Removed app. [remoteId=({})]", appId);
                }
                return ResponseEntity.internalServerError().body("Could not download app.");
            }

            return respondWithCreatedApp(appId);
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
     * Return created app as ResponseEntity with Status 201.
     *
     * @param remoteId appstore id of created app.
     * @return ResponseEntity containing created app.
     */
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
