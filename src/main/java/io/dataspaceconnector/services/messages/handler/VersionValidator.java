package io.dataspaceconnector.services.messages.handler;

import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component("VersionValidator")
public class VersionValidator implements Processor {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    @Override
    public void process(final Exchange exchange) throws Exception {
        final var msg = exchange.getIn().getBody(Request.class);
        messageService.validateIncomingMessage(msg.getHeader());
        log.info("Validating and stuff!");
    }
}
