package io.dataspaceconnector.services.messages.handler.camel; // IdscpWrapperProcessor

import java.io.ByteArrayInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import io.dataspaceconnector.services.messages.handler.camel.dto.Request;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("idscpWrapperProcessorIncoming")
public class IdscpWrapperProcessorIncoming implements Processor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var header = exchange.getIn().getHeader("idscp2-header", Message.class);
        final var payloadStream = new ByteArrayInputStream(exchange.getIn().getBody(byte[].class));
        final var payload = new MessagePayloadInputstream(payloadStream, new ObjectMapper());
        exchange.getIn().setBody(new Request(header, payload));
    }

}
