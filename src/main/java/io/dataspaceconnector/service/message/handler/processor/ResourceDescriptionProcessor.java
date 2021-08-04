package io.dataspaceconnector.service.message.handler.processor;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.ErrorMessage;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.message.DescriptionResponseMessageDesc;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.type.DescriptionResponseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Generates a resource description as the response to a DescriptionRequestMessage, if a requested
 * element was given.
 */
@Component("ResourceDescription")
@RequiredArgsConstructor
class ResourceDescriptionProcessor extends IdsProcessor<
        RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Generates the description of the requested element as the response payload and creates
     * a DescriptionResponseMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a DescriptionResponseMessage as header and the resource
     * description as payload.
     * @throws Exception if the resource cannot be found or an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl,
            MessagePayload> msg) throws Exception {
        // Read relevant parameters for message processing.
        final var requested = MessageUtils.extractRequestedElement(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var entity = entityResolver.getEntityById(requested);

        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString());
        }

        // If the element has been found, build the ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);
        final var payload = entityResolver.getEntityAsRdfString(entity.get());

        // Send ids response message.
        return new Response(header, payload);
    }
}
