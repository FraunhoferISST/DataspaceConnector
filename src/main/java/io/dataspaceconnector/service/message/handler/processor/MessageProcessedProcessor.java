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
package io.dataspaceconnector.service.message.handler.processor;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.service.message.builder.type.MessageProcessedNotificationService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.processor.base.IdsProcessor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Generates the response to a NotificationMessage.
 */
@Component("ProcessedNotification")
@RequiredArgsConstructor
class MessageProcessedProcessor extends IdsProcessor<RouteMsg<NotificationMessageImpl, ?>> {

    /**
     * Service for handling message processed notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Creates a MessageProcessedNotificationMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<NotificationMessageImpl, ?> msg,
                                       final Jws<Claims> claims)
            throws Exception {
        // Build the ids response.
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, "Message processed.");
    }
}
