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

import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.ResultMessageImpl;
import de.fraunhofer.ids.messaging.common.DeserializeException;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.http.SendMessageException;
import de.fraunhofer.ids.messaging.protocol.http.ShaclValidatorException;
import de.fraunhofer.ids.messaging.protocol.multipart.UnknownResponseException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import de.fraunhofer.ids.messaging.requests.exceptions.NoTemplateProvidedException;
import de.fraunhofer.ids.messaging.requests.exceptions.RejectionException;
import de.fraunhofer.ids.messaging.requests.exceptions.UnexpectedPayloadException;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.service.message.GlobalMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for sending ids query messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
public class QueryMessageController {

    /**
     * The service for sending ids messages.
     */
    private final @NonNull GlobalMessageService messageService;

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
     * Send an ids query message with a query message as payload.
     *
     * @param recipient The url of the recipient.
     * @param query     The query statement.
     * @return The response message or an error.
     */
    @PostMapping("/query")
    @Operation(summary = "Query message", description = "Can be used for querying an "
            + "IDS component (e.g. the Broker).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway timeout")})
    @ResponseBody
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    public ResponseEntity<Object> sendQueryMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Schema(description = "Database query (SparQL)", required = true,
                    example = "SELECT ?subject ?predicate ?object\n"
                            + "FROM <urn:x-arq:UnionGraph>\n"
                            + "WHERE {\n"
                            + "  ?subject ?predicate ?object\n"
                            + "};") @RequestBody final String query) {
        if (connectorConfig.isIdscpEnabled()) {
            final var result = template.send("direct:querySender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                            .withProperty(ParameterUtils.QUERY_PARAM, query)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            if (response != null) {
                return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
            } else {
                final var responseEntity =
                    toObjectResponse(result.getIn().getBody(ResponseEntity.class));
                return Objects.requireNonNullElseGet(responseEntity,
                        () -> new ResponseEntity<Object>("An internal server error occurred.",
                                HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } else {
            try {
                // Send the query message.
                final var response = messageService.sendQueryMessage(recipient, query);
                return messageService.validateResponse(response, ResultMessageImpl.class);
            } catch (SocketTimeoutException exception) {
                // If a timeout has occurred.
                return ControllerUtils.respondConnectionTimedOut(exception);
            } catch (MultipartParseException | UnknownResponseException | ShaclValidatorException
                    | DeserializeException | UnexpectedPayloadException | ClaimsException e) {
                // If the response was invalid.
                return ControllerUtils.respondReceivedInvalidResponse(e);
            } catch (RejectionException ignored) {
                // If the response is a rejection message. Error is ignored.
            } catch (SendMessageException | SerializeException | DapsTokenManagerException e) {
                // If the message could not be built or sent.
                return ControllerUtils.respondMessageSendingFailed(e);
            } catch (NoTemplateProvidedException | IOException exception) {
                // If any other error occurred.
                return ControllerUtils.respondIdsMessageFailed(exception);
            }
            return messageService.validateResponse(Optional.empty(), ResultMessageImpl.class);
        }
    }

    /**
     * Send an ids query message with search terms and values as payload.
     *
     * @param recipient The url of the recipient.
     * @param term      The search term.
     * @param limit     The limit of the number of response objects.
     * @param offset    The search offset value.
     * @return The response message or an error.
     */
    @PostMapping("/search")
    @Operation(summary = "Full text search", description = "Can be used for full text search at an "
            + "IDS component (e.g. the Broker).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway timeout")})
    @ResponseBody
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    public ResponseEntity<Object> sendSearchMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The limit value.", required = true)
            @RequestParam(value = "limit", defaultValue = "50") final Integer limit,
            @Parameter(description = "The offset value.", required = true)
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @Parameter(description = "The search term.", required = true)
            @RequestBody final String term) {
        if (connectorConfig.isIdscpEnabled()) {
            final var result = template.send("direct:querySender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                            .withProperty(ParameterUtils.QUERY_LIMIT_PARAM, limit)
                            .withProperty(ParameterUtils.QUERY_OFFSET_PARAM, offset)
                            .withProperty(ParameterUtils.QUERY_TERM_PARAM, term)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            if (response != null) {
                if (response.getHeader() instanceof RejectionMessage) {
                    return new ResponseEntity<>(response.getBody(), HttpStatus.EXPECTATION_FAILED);
                }
                return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
            } else {
                final var responseEntity =
                    toObjectResponse(result.getIn().getBody(ResponseEntity.class));
                return Objects.requireNonNullElseGet(responseEntity,
                        () -> new ResponseEntity<Object>("An internal server error occurred.",
                                HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } else {
            try {
                // Send the query message for full text search.
                final var response = messageService.sendFullTextSearchMessage(recipient, term,
                                                                                limit, offset);
                return messageService.validateResponse(response, ResultMessageImpl.class);
            } catch (SocketTimeoutException exception) {
                // If a timeout has occurred.
                return ControllerUtils.respondConnectionTimedOut(exception);
            } catch (MultipartParseException | UnknownResponseException | ShaclValidatorException
                    | DeserializeException | UnexpectedPayloadException | ClaimsException e) {
                // If the response was invalid.
                return ControllerUtils.respondReceivedInvalidResponse(e);
            } catch (RejectionException ignored) {
                // If the response is a rejection message. Error is ignored.
            } catch (SendMessageException | SerializeException | DapsTokenManagerException e) {
                // If the message could not be built or sent.
                return ControllerUtils.respondMessageSendingFailed(e);
            } catch (NoTemplateProvidedException | IOException exception) {
                // If any other error occurred.
                return ControllerUtils.respondIdsMessageFailed(exception);
            }
            return messageService.validateResponse(Optional.empty(), ResultMessageImpl.class);
        }
    }

    @SuppressWarnings("unchecked")
    private static ResponseEntity<Object> toObjectResponse(final ResponseEntity<?> response) {
        return (ResponseEntity<Object>) response;
    }
}
