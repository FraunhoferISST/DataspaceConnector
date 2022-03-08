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
package io.dataspaceconnector.service.routing.processor;

import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.ParameterUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Adds headers for an HTTP request from the {@link org.apache.camel.Exchange} parameters.
 * Required when routes are used in subscriptions, as the update information resides in the
 * headers.
 */
@Component("headerProcessor")
public class HeaderProcessor implements Processor {

    /**
     * Reads the {@link QueryInput} exchange property and adds its headers as Camel headers.
     *
     * @param exchange The exchange.
     * @throws Exception if an error occurs.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var input = exchange
                .getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class);

        if (input != null && input.getHeaders() != null && !input.getHeaders().isEmpty()) {
            input.getHeaders().forEach(exchange.getIn()::setHeader);
        }
    }
}
