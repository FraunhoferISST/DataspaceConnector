package io.dataspaceconnector.service.message.handler.transformer;

import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Superclass for Camel processors that transform an incoming message's payload, e.g. by
 * deserialization.
 *
 * @param <I> the expected input type (body of the Camel {@link Exchange}).
 * @param <O> the output type (body of the Camel {@link Exchange} after transformation).
 */
public abstract class IdsTransformer<I, O> implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method and sets the result as the {@link Exchange}'s body.
     *
     * @param exchange the input.
     * @throws Exception if transformation fails.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody(processInternal((I) exchange.getIn().getBody(RouteMsg.class)));
    }

    /**
     * Transforms the input into the desired output type. To be implemented by sub classes.
     *
     * @param msg the incoming message.
     * @return the transformed input.
     * @throws Exception if transformation fails.
     */
    protected abstract O processInternal(I msg) throws Exception;
}
