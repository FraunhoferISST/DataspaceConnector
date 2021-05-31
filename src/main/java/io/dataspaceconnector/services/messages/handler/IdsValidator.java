package io.dataspaceconnector.services.messages.handler;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public abstract class IdsValidator implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange.getIn().getBody(Request.class));
    }

    protected abstract void processInternal(Request request) throws Exception;
}
