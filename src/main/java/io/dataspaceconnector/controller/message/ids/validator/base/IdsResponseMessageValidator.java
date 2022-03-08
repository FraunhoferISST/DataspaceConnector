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
package io.dataspaceconnector.controller.message.ids.validator.base;

import io.dataspaceconnector.service.message.handler.dto.Response;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Superclass for all processors that validate a received response message.
 */
public abstract class IdsResponseMessageValidator implements Processor {

    /**
     * The error message used for throwing an Exception when the response is not valid.
     */
    protected static final String ERROR_MESSAGE = "Received an invalid response.";

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}'s body as parameter.
     *
     * @param exchange the exchange.
     * @throws Exception if validation fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange.getIn().getBody(Response.class));
    }

    /**
     * Validates the response DTO. To be implemented by sub classes.
     *
     * @param response the response DTO.
     * @throws Exception if validation fails.
     */
    protected abstract void processInternal(Response response) throws Exception;

}
