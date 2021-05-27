package io.dataspaceconnector.services.messages.handler;

import io.dataspaceconnector.model.messages.DescriptionResponseMessageDesc;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("SelfDescription")
@RequiredArgsConstructor
public class SelfDescription implements Processor {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    @Override
    public void process(final Exchange exchange) throws Exception {
        // TODO Only return contract offers that have no or the right pre-defined consumer
        final var msg = exchange.getIn().getBody(Request.class);
        exchange.getIn().setBody(process(msg));
    }

    protected Response process(final Request request) {
        final var issuer = MessageUtils.extractIssuerConnector(request.getHeader());
        final var messageId = MessageUtils.extractMessageId(request.getHeader());
        final var connector = connectorService.getConnectorWithOfferedResources();

        // Build ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, connector.toRdf());
    }
}
