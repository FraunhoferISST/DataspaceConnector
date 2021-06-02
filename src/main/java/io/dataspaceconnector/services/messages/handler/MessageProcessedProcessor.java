package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ProcessedNotification")
@RequiredArgsConstructor
public class MessageProcessedProcessor extends IdsProcessor<RouteMsg<NotificationMessageImpl, ?>> {

    /**
     * Service for handling message processed notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(RouteMsg<NotificationMessageImpl, ?> msg) throws Exception {
        // Build the ids response.
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, "Message processed.");
    }
}
