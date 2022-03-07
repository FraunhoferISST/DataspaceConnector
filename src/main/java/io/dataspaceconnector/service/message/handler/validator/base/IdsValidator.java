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
package io.dataspaceconnector.service.message.handler.validator.base;

import io.dataspaceconnector.service.message.handler.dto.Request;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Superclass for Camel processors that validate either header or payload of an incoming message.
 *
 * @param <I> the expected input type (body of the Camel {@link Exchange}).
 */
public abstract class IdsValidator<I> implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method.
     *
     * @param exchange the input.
     * @throws Exception if validation fails.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        processInternal((I) exchange.getIn().getBody(Request.class));
    }

    /**
     * Validates either header of body of the incoming message. To be implemented by sub classes.
     *
     * @param msg the incoming message.
     * @throws Exception if validation fails.
     */
    protected abstract void processInternal(I msg) throws Exception;
}
