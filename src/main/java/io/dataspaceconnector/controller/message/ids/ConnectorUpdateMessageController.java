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
package io.dataspaceconnector.controller.message.ids;

import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.ids.messaging.common.DeserializeException;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.http.SendMessageException;
import de.fraunhofer.ids.messaging.protocol.http.ShaclValidatorException;
import de.fraunhofer.ids.messaging.protocol.multipart.UnknownResponseException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import de.fraunhofer.ids.messaging.requests.exceptions.NoTemplateProvidedException;
import de.fraunhofer.ids.messaging.requests.exceptions.RejectionException;
import de.fraunhofer.ids.messaging.requests.exceptions.UnexpectedPayloadException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.controller.message.tag.MessageDescription;
import io.dataspaceconnector.controller.message.tag.MessageName;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.service.message.GlobalMessageService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Optional;

/**
 * Controller for sending ids connector update messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = MessageName.MESSAGES, description = MessageDescription.MESSAGES)
public class ConnectorUpdateMessageController {

    /**
     * The service for sending ids messages.
     */
    private final @NonNull GlobalMessageService messageService;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

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
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Sending an ids connector update message with the current connector as payload.
     *
     * @param recipient The url of the recipient.
     * @return The response message or an error.
     */
    @PostMapping("/connector/update")
    @Operation(summary = "Send an IDS ConnectorUpdateMessage.", description = "Can be used for "
            + "registering or updating the connector at an IDS Broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway timeout")})
    @ResponseBody
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient) {
        if (connectorConfig.isIdscpEnabled()) {
            final var result = template.send("direct:connectorUpdateSender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                            .build());

            return ResponseUtils.respondWithExchangeContent(result);
        } else {
            try {
                // Update the config model.
                connectorService.updateConfigModel();

                // Send the connector update message.
                final var response = messageService.sendConnectorUpdateMessage(recipient);
                return messageService.validateResponse(response,
                        MessageProcessedNotificationMessageImpl.class);
            } catch (ConfigUpdateException exception) {
                // If the configuration could not be updated.
                return ResponseUtils.respondConfigurationUpdateError(exception);
            } catch (SocketTimeoutException exception) {
                // If a timeout has occurred.
                return ResponseUtils.respondConnectionTimedOut(exception);
            } catch (MultipartParseException | UnknownResponseException | ShaclValidatorException
                    | DeserializeException | UnexpectedPayloadException | ClaimsException e) {
                // If the response was invalid.
                return ResponseUtils.respondReceivedInvalidResponse(e);
            } catch (RejectionException ignored) {
                // If the response is a rejection message. Error is ignored.
            } catch (SendMessageException | SerializeException | DapsTokenManagerException e) {
                // If the message could not be built or sent.
                return ResponseUtils.respondMessageSendingFailed(e);
            } catch (NoTemplateProvidedException | IOException exception) {
                // If any other error occurred.
                return ResponseUtils.respondIdsMessageFailed(exception);
            }
            return messageService.validateResponse(Optional.empty(),
                    MessageProcessedNotificationMessageImpl.class);
        }
    }
}
