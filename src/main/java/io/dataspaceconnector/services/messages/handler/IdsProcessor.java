package io.dataspaceconnector.services.messages.handler;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public abstract class IdsProcessor implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody(processInternal(exchange.getIn().getBody(Request.class)));
    }

    protected abstract Response processInternal(Request request) throws Exception;
}
