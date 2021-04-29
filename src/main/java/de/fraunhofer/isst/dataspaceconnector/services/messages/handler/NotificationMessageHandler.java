package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
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
    private final @NonNull MessageProcessedNotificationService notificationService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageResponseService exceptionService;

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

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
            messageService.validateIncomingRequestMessage(message);
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuer = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        try {
            // Build the ids response.
            final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
            final var header = notificationService.buildMessage(desc);
            return BodyResponse.create(header, "Message received.");
        } catch (IllegalStateException | ConstraintViolationException e) {
            return exceptionService.handleResponseMessageBuilderException(e, issuer, messageId);
        }
    }
}
