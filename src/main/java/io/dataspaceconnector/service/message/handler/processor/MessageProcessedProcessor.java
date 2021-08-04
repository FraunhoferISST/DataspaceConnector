package io.dataspaceconnector.service.message.handler.processor;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.type.MessageProcessedNotificationService;
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
    protected Response processInternal(final RouteMsg<NotificationMessageImpl, ?> msg)
            throws Exception {
        // Build the ids response.
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, "Message processed.");
    }
}
