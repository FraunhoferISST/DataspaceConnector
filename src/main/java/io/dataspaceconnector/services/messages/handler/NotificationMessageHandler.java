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
package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.exceptions.MessageEmptyException;
import io.dataspaceconnector.exceptions.VersionNotSupportedException;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.messages.MessageResponseService;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import io.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This @{@link NotificationMessageHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.NotificationMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.NotificationMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(NotificationMessageImpl.class)
@RequiredArgsConstructor
public class NotificationMessageHandler implements MessageHandler<NotificationMessageImpl> {

    /**
     * Service for handling message processed notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Service for building and sending message responses.
     */
    private final @NonNull MessageResponseService responseService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids notification message as header.
     * @param payload The message notification message's content.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final NotificationMessageImpl message,
                                         final MessagePayload payload) {
        // Validate incoming message.
        try {
            messageService.validateIncomingMessage(message);
        } catch (MessageEmptyException exception) {
            return responseService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return responseService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuer = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        try {
            // Build the ids response.
            final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
            final var header = messageService.buildMessage(desc);
            return BodyResponse.create(header, "Message received.");
        } catch (IllegalStateException | ConstraintViolationException e) {
            return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        }
    }
}
