package io.dataspaceconnector.services.messages.handler.camel; // IdscpWrapperProcessor

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayloadImpl;
import io.dataspaceconnector.services.messages.handler.camel.dto.Request;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component("idscpWrapperProcessorIncoming")
public class IdscpWrapperProcessorIncoming implements Processor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var header = exchange.getIn().getHeader("idscp2-header", Message.class);
        final var payloadStream = new ByteArrayInputStream(exchange.getIn().getBody(byte[].class));
        final var payload = new MessagePayloadImpl(payloadStream, new ObjectMapper());
        exchange.getIn().setBody(new Request(header, payload));
    }

}
