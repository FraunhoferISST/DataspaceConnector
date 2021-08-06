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

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import de.fraunhofer.iais.eis.AppRepresentation;
import de.fraunhofer.iais.eis.AppResource;
import de.fraunhofer.ids.messaging.util.SerializerProvider;
import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.ArtifactDataDownloader;
import io.dataspaceconnector.service.EntityPersistenceService;
import io.dataspaceconnector.service.MetadataDownloader;
import io.dataspaceconnector.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides the endpoint for sending an app request message and starting the
 * metadata and data exchange.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
public class AppRequestMessageController {

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serProvider;

    /**
     * Downloads metadata.
     */
    private final @NonNull MetadataDownloader metadataDownloader;

    /**
     * The artifact request service.
     */
    private final @NonNull ArtifactDataDownloader artifactDataDownloader;

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Add an apps metadata to an app object.
     *
     * @param recipient The recipient url
     * @param app       The app ID.
     * @return Success, when App can be found and created from recipient response.
     */
    @PostMapping("/app")
    @Operation(summary = "Send IDS App request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The app url.", required = true)
            @RequestParam("app") final URI app) {

        try {
            // Send description request message and save the app
            var response = downloadApp(recipient, app);
            var appresource = parseAppResource(response);
            var instanceId = getInstanceID(appresource);
            // Send artifact request message
            if (instanceId != null) {
                var artifactJson = artifactDataDownloader
                        .downloadAppArtifact(recipient, instanceId);
                if (artifactJson != null) {
                    persistenceSvc.saveAppResource(response, artifactJson, recipient);
                } else {
                    //TODO useful error response
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                //TODO useful error response
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (MessageException exception) {
            // If the message could not be built.
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException | IOException e) {
            // If the response message is invalid or malformed.
            return ControllerUtils.respondReceivedInvalidResponse(e);
        } catch (UnexpectedResponseException e) {
            // If the response is not as expected.
            return ControllerUtils.respondWithContent(e.getContent());
        }

    }

    /**
     * Get AppResource from AppStore.
     *
     * @param recipient The recipient connector.
     * @param appResource The app resource.
     * @throws UnexpectedResponseException if the response type is not as expected.
     * @return the appstore response.
     */
    private Map<String, String> downloadApp(final URI recipient, final URI appResource)
            throws UnexpectedResponseException {
        return metadataDownloader.downloadAppResource(recipient, appResource);
    }

    /**
     * Parse response from AppStore to AppResource.
     *
     * @param response response from AppStore.
     * @return payload parsed as AppResource.
     * @throws IOException when payload cannot be parsed to AppResource.
     */
    private AppResource parseAppResource(final Map<String, String> response) throws IOException {
        var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        return serProvider.getSerializer().deserialize(payload, AppResource.class);
    }

    /**
     * Get instance ID (used for receiving AppArtifact with metadata from AppStore)
     * from AppResource.
     *
     * @param appResource the appresource.
     * @return the instance ID.
     */
    private URI getInstanceID(final AppResource appResource) {
        if (appResource != null && appResource.getRepresentation() != null) {
            var appRepresentations = appResource.getRepresentation().stream()
                    .filter(x -> x instanceof AppRepresentation)
                    .map(x -> (AppRepresentation) x)
                    .collect(Collectors.toList());
            if (!appRepresentations.isEmpty()) {
                var instance = appRepresentations.get(0).getInstance();
                if (instance != null && !instance.isEmpty()) {
                    return instance.get(0).getId();
                }
            }
        }
        return null;
    }


//    private String convertToAnswer(final URI elementId, final String payload) {
//        return Utils.isEmptyOrNull(elementId) ? unwrapResponse(payload) : payload;
//    }
//
//    private String unwrapResponse(final String payload) {
//        try {
//            // Get payload as component.
//            return deserializationSvc.getInfrastructureComponent(payload).toRdf();
//        } catch (IllegalArgumentException ignored) {
//            // If the response is not of type base connector.
//            return payload;
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static ResponseEntity<Object> toObjectResponse(final ResponseEntity<?> response) {
//        return (ResponseEntity<Object>) response;
//    }
}
