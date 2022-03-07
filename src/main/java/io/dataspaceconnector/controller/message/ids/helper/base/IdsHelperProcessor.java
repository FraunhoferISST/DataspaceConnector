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
package io.dataspaceconnector.controller.message.ids.helper.base;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Superclass for all processors that perform helper tasks that do not fit in with any of the other
 * processor types.
 */
public abstract class IdsHelperProcessor implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}.
     *
     * @param exchange the exchange.
     * @throws Exception if any error occurs.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange);
    }

    /**
     * Performs the helper task. To be implemented by sub classes.
     *
     * @param exchange the exchange.
     * @throws Exception if any error occurs.
     */
    protected abstract void processInternal(Exchange exchange) throws Exception;

}
