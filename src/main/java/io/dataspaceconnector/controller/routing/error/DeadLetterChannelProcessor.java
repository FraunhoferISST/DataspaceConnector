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
package io.dataspaceconnector.controller.routing.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dataspaceconnector.service.message.handler.dto.RouteError;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Prepares a {@link RouteError} for a failed exchange.
 */
@Component("dlcProcessor")
@Log4j2
public class DeadLetterChannelProcessor implements Processor {

    /**
     * Writes the RouteError as JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads the route's ID, the endpoint where the exchange failed, and the exception that caused
     * the failure from an Exchange object and creates an {@link RouteError}. The ErrorDto is set as
     * the body of the exchange's message.
     *
     * @param exchange the failed exchange.
     * @throws Exception if an error occurs writing the error DTO as JSON.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var routeId = exchange.getProperty("CamelFailureRouteId", String.class);
        final var failureEndpoint = exchange.getProperty("CamelFailureEndpoint", String.class);
        final var cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        final var routeError = new RouteError(routeId, failureEndpoint, cause.getMessage(),
                LocalDateTime.now().toString());

        if (log.isWarnEnabled()) {
            log.warn("Caught an exception during route execution. [error=({})]", routeError);
        }

        exchange.getIn().setBody(objectMapper.writeValueAsString(routeError));
    }

}
