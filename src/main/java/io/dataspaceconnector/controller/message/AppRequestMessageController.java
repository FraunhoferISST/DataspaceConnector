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

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import io.dataspaceconnector.util.MessageUtils;
import io.dataspaceconnector.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

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
     * Service for message handling.
     */
    private final @NonNull DescriptionRequestService descriptionReqSvc;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationSvc;

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Service for handle application.properties settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;


    /**
     * Add an apps metadata to an app object.
     *
     * @param recipient The recipient url
     * @param app The app ID.
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
        //Just send a descriptionRequestmessage to get the app information
        //TODO: convert response to App object
        String payload;
        if (connectorConfig.isIdscpEnabled()) {
            final var result = template.send("direct:descriptionRequestSender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                            .withProperty(ParameterUtils.ELEMENT_ID_PARAM, app)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            if (response != null) {
                payload = response.getBody();
            } else {
                final var responseEntity =
                        toObjectResponse(result.getIn().getBody(ResponseEntity.class));
                return Objects.requireNonNullElseGet(responseEntity,
                        () -> new ResponseEntity<>("An internal server error occurred.",
                                HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } else {
            try {
                // Send and validate description request/response message.
                final var response = descriptionReqSvc.sendMessage(recipient, app);

                // Read and process the response message.
                payload = MessageUtils.extractPayloadFromMultipartMessage(response);
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
        return new ResponseEntity<>(convertToAnswer(app, payload), HttpStatus.OK);
    }

    private String convertToAnswer(final URI elementId, final String payload) {
        return Utils.isEmptyOrNull(elementId) ? unwrapResponse(payload) : payload;
    }

    private String unwrapResponse(final String payload) {
        try {
            // Get payload as component.
            return deserializationSvc.getInfrastructureComponent(payload).toRdf();
        } catch (IllegalArgumentException ignored) {
            // If the response is not of type base connector.
            return payload;
        }
    }

    @SuppressWarnings("unchecked")
    private static ResponseEntity<Object> toObjectResponse(final ResponseEntity<?> response) {
        return (ResponseEntity<Object>) response;
    }

    private void downloadApp(final URI recipient, final URI appResource) {
          //TODO apps should be downloaded when they are started, not after the request
//        metadataDownloader.downloadAppResource(recipient, appResource);
    }
}
