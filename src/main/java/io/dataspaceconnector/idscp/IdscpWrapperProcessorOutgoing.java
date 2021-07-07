package io.dataspaceconnector.idscp;

import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.camel.dto.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Processor that parses the DTO produced by the message handlers' Camel routes to an IDSCP message.
 */
@Component("idscpWrapperProcessorOutgoing")
@Log4j2
public class IdscpWrapperProcessorOutgoing implements Processor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var in = exchange.getIn();
        final var response = in.getBody(Response.class);

        if (response != null) {
            in.setHeader("idscp2-header", response.getHeader());
            in.setBody(response.getBody().getBytes());
        } else {
            final var rejection = in.getBody(ErrorResponse.class);
            in.setHeader("idscp2-header", rejection.getRejectionMessage());
            in.setBody(rejection.getErrorMessage().getBytes());
        }
    }
}
