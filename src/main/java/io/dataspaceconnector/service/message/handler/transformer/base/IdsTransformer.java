/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.message.handler.transformer.base;

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
