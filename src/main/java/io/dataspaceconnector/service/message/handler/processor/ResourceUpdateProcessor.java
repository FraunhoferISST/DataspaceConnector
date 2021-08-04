package io.dataspaceconnector.service.message.handler.processor;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.service.EntityUpdateService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.type.MessageProcessedNotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Updates a requested resource when a ResourceUpdateMessage is received and generates the response.
 */
@Component("ResourceUpdateProcessor")
@RequiredArgsConstructor
class ResourceUpdateProcessor extends IdsProcessor<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * The global event publisher used for handling events.
     */
    private final @NonNull ApplicationEventPublisher publisher;

    /**
     * Updates the local copy of the resource given in the ResourceUpdateMessage and creates a
     * MessageProcessedNotificationMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if the resource cannot be updated or an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<ResourceUpdateMessageImpl, Resource> msg)
            throws Exception {
        updateService.updateResource(msg.getBody());

        // Publish the agreement so that the designated event handler sends it to the CH.
        publisher.publishEvent(msg.getBody());

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Message received.");
    }

}
