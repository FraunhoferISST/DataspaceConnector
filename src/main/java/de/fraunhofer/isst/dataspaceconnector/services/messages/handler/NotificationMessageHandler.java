package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageResponseBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
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
     * Service for handling notification messages.
     */
    private final @NonNull NotificationMessageService messageService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The received notification message as header.
     * @param payload The message notification message's content.
     * @return The response message.
     * @throws MessageResponseBuilderException If the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(final NotificationMessageImpl message,
                                         final MessagePayload payload) throws MessageResponseBuilderException {
        // Validate incoming message.
        messageService.checkForEmptyMessage(message);
        messageService.checkForVersionSupport(message.getModelVersion());

        // Build the ids response.
        final var header =
                messageService.buildMessageProcessedNotification(message.getIssuerConnector(),
                        message.getId());
        return BodyResponse.create(header, "Message received.");
    }
}
