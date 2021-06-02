package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.model.messages.DescriptionResponseMessageDesc;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ResourceDescription")
@RequiredArgsConstructor
public class ResourceDescription extends IdsProcessor<RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl, MessagePayload> msg) throws Exception {
        // Read relevant parameters for message processing.
        final var requested = MessageUtils.extractRequestedElement(msg.getHeader());
        final var entity = entityResolver.getEntityById(requested);
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        // If the element has been found, build the ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);
        final var payload = entityResolver.getEntityAsRdfString(entity);

        // Send ids response message.
        return new Response(header, payload);
    }
}
