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

import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.MetadataDownloader;
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

import java.net.URI;

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
     * Downloads metadata.
     */
    private final @NonNull MetadataDownloader metadataDownloader;

    /**
     * Add an apps metadata to an app object.
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
    public final ResponseEntity<Object> sendMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The app url.", required = true)
            @RequestParam("app") final URI app) {

        try {
            downloadApp(recipient, app);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (MessageException exception) {
            // If the message could not be built.
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException e) {
            // If the response message is invalid or malformed.
            return ControllerUtils.respondReceivedInvalidResponse(e);
        } catch (UnexpectedResponseException e) {
            // If the response is not as expected.
            return ControllerUtils.respondWithContent(e.getContent());
        }
    }

    private void downloadApp(final URI recipient, final URI appResource)
            throws UnexpectedResponseException {
        metadataDownloader.downloadAppResource(recipient, appResource);
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
