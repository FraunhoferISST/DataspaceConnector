/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.message.handler.type.base;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.ids.messaging.handler.message.MessageAndClaimsHandler;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import de.fraunhofer.ids.messaging.response.BodyResponse;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import de.fraunhofer.ids.messaging.response.MessageResponse;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;

import java.util.Objects;
import java.util.Optional;

/**
 * Superclass for all message handlers, that contains the logic for processing an incoming request
 * in a designated Camel route.
 *
 * @param <T> type of message that can be processed by this handler.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMessageHandler<T extends Message>
        implements MessageAndClaimsHandler<T> {

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * This message implements the logic that is needed to handle the message. It creates an
     * {@link org.apache.camel.Exchange} and triggers the route specified by the implementing class.
     *
     * @param message The request message.
     * @param payload The message payload.
     * @param claims  The jwt claims.
     * @return The response message.
     * @throws RuntimeException If the response body failed to be build.
     */
    public MessageResponse handleMessage(final T message,
                                         final MessagePayload payload,
                                         final Optional<Jws<Claims>> claims)
            throws RuntimeException {
        final var result = template.send(getHandlerRouteDirect(),
                ExchangeBuilder.anExchange(context)
                        .withBody(new Request<>(message, payload, claims))
                        .build());

        final var response = result.getIn().getBody(Response.class);
        if (response != null) {
            return BodyResponse.create(response.getHeader(), response.getBody());
        } else {
            final var errorResponse = result.getIn().getBody(ErrorResponse.class);
            return Objects.requireNonNullElseGet(errorResponse,
                    () -> ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                            "Could not process request.",
                            connectorService.getConnectorId(),
                            connectorService.getOutboundModelVersion()));
        }
    }

    /**
     * Returns the direct-component-reference to this handler's Camel route.
     *
     * @return the route reference.
     */
    protected abstract String getHandlerRouteDirect();

}
