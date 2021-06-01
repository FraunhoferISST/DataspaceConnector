package io.dataspaceconnector.services.messages.handler;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public abstract class IdsValidator<I> implements Processor {

    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        processInternal((I)exchange.getIn().getBody(Request.class));
    }

    protected abstract void processInternal(I msg) throws Exception;
}
