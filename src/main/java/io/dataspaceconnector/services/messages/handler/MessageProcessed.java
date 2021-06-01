package io.dataspaceconnector.services.messages.handler;

import org.springframework.stereotype.Component;

import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;

@Component("ProcessedNotification")
@RequiredArgsConstructor
public class MessageProcessed extends IdsProcessor {

    /**
     * Service for handling message processed notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(final Request request) throws Exception {
        // Build the ids response.
        final var issuer = MessageUtils.extractIssuerConnector(request.getHeader());
        final var messageId = MessageUtils.extractMessageId(request.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);
        final var body = new JSONObject();
        body.put("status:", "acknowledged");

        return new Response(header, body.toJSONString());
    }
}
