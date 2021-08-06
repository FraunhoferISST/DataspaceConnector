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
package io.dataspaceconnector.service.message.handler.processor;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.ids.mapping.RdfConverter;
import io.dataspaceconnector.model.message.DescriptionResponseMessageDesc;
import io.dataspaceconnector.service.message.builder.type.DescriptionResponseService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Generates the connector's self-description as the response to a DescriptionRequestMessage,
 * if no requested element was given.
 */
@Component("SelfDescription")
@RequiredArgsConstructor
class SelfDescriptionProcessor extends IdsProcessor<
        RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Generates the self-description as the response payload and creates a
     * DescriptionResponseMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a DescriptionResponseMessage as header and the
     * self-description as payload.
     * @throws Exception if an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl,
            MessagePayload> msg) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var connector = connectorService.getConnectorWithOfferedResources();

        // Build ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, RdfConverter.toRdf(connector));
    }
}
