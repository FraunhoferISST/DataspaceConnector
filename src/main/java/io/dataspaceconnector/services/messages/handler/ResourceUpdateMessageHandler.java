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

import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import io.dataspaceconnector.exceptions.MessageEmptyException;
import io.dataspaceconnector.exceptions.VersionNotSupportedException;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.EntityUpdateService;
import io.dataspaceconnector.services.ids.DeserializationService;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * This @{@link ResourceUpdateMessageHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ResourceUpdateMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link ResourceUpdateMessageImpl} JsonTypeName annotation.
 */

@Component
@Log4j2
@RequiredArgsConstructor
@SupportedMessageType(ResourceUpdateMessageImpl.class)
public class ResourceUpdateMessageHandler implements MessageHandler<ResourceUpdateMessageImpl> {

    /**
     * Service for building and sending message responses.
     */
    private final @NonNull MessageResponseService responseService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The notification message payload.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final ResourceUpdateMessageImpl message,
                                         final MessagePayload payload) throws RuntimeException {
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
        final var affected = MessageUtils.extractAffectedResource(message);
        final var issuer = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        if (affected == null || affected.toString().isEmpty()) {
            // Without an affected resource, the message processing will be aborted.
            return responseService.handleMissingAffectedResource(affected, issuer, messageId);
        }

        String payloadAsString;
        try {
            // Try to read payload as string.
            payloadAsString = MessageUtils.getStreamAsString(payload);
            if (payloadAsString.isEmpty()) {
                return responseService.handleMissingPayload(affected, issuer, messageId);
            }
        } catch (IOException | IllegalArgumentException e) {
            return responseService.handleMessagePayloadException(e, messageId, issuer);
        }

        return updateResource(payloadAsString, affected, issuer, messageId);
    }

    /**
     * Update resource in internal database.
     *
     * @param payload   The payload as string.
     * @param affected  The affected resource.
     * @param issuer    The issuer connector.
     * @param messageId The message id.
     * @return A message response.
     */
    private MessageResponse updateResource(final String payload, final URI affected,
                                           final URI issuer, final URI messageId) {
        // Get ids resource from payload.
        try {
            final var resource = deserializationService.getResource(payload);
            final var resourceId = resource.getId();

            // Check if the resource id and affected resource id match.
            if (!resourceId.equals(affected)) {
                return responseService.handleInvalidAffectedResource(resourceId, affected, issuer,
                        messageId);
            }

            // Update requested resource with received information.
            updateService.updateResource(resource);
        } catch (IllegalArgumentException e) {
            return responseService.handleIllegalArgumentException(e, payload, issuer, messageId);
        }

        // Respond although updating the resource may have failed.
        return respondToMessage(issuer, messageId);
    }

    /**
     * Build and send response message.
     *
     * @param issuer    The issuer connector.
     * @param messageId The message id.
     * @return A message response.
     */
    private MessageResponse respondToMessage(final URI issuer, final URI messageId) {
        try {
            // Build ids response message.
            final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
            final var header = messageService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, "Message received.");
        } catch (IllegalStateException e) {
            return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        }
    }
}
