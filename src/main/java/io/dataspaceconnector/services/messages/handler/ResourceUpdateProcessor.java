package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.EntityUpdateService;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("ResourceUpdateProcessor")
@RequiredArgsConstructor
public class ResourceUpdateProcessor extends IdsProcessor<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(final RouteMsg<ResourceUpdateMessageImpl, Resource> msg) throws Exception {
        updateService.updateResource(msg.getBody());

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, "Message received.");
    }

}
