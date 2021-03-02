package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This @{@link NotificationMessageHandler} handles
 * all incoming messages that have a {@link de.fraunhofer.iais.eis.NotificationMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link de.fraunhofer.iais.eis.NotificationMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(NotificationMessageImpl.class)
@RequiredArgsConstructor
public class NotificationMessageHandler implements MessageHandler<NotificationMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageHandler.class);

    private final @NonNull NotificationMessageService messageService;
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message        The received notification message.
     * @param messagePayload The message notification messages content.
     * @return The response message.
     * @throws RuntimeException If the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(final NotificationMessageImpl message,
        final MessagePayload messagePayload) throws RuntimeException {

        messageService.checkForEmptyMessage(message);
        messageService.checkForVersionSupport(message.getModelVersion());

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        try {
            // Build the ids response.
            final var header = messageService.buildMessageProcessedNotification(message.getIssuerConnector(), message.getId());
            return BodyResponse.create(header, "Message received.");
        } catch (ConstraintViolationException | MessageException exception) {
            // The response could not be constructed.
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }
}
