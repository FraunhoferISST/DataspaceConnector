package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageExceptionService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.NotificationService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private final @NonNull NotificationService messageService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageExceptionService exceptionService;

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
            messageService.checkForEmptyMessage(message);
            messageService.checkForVersionSupport(message.getModelVersion());
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (InfoModelVersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        try {
            // Build the ids response.
            final var issuerConnector = MessageUtils.extractIssuerConnectorFromMessage(message);
            final var messageId = MessageUtils.extractMessageIdFromMessage(message);
            final var params = List.of(messageId);
            final var header = messageService.buildMessage(issuerConnector, params);
            return BodyResponse.create(header, "Message received.");
        } catch (IllegalStateException exception) {
            return exceptionService.handleIllegalStateException(exception);
        } catch (ConstraintViolationException exception) {
            return exceptionService.handleConstraintViolationException(exception);
        }
    }
}
