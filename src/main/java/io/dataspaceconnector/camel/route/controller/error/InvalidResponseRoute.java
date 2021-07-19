/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.camel.route.controller.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling invalid responses.
 */
@Component
public class InvalidResponseRoute extends RouteBuilder {

    /**
     * ObjectMapper for writing the response to JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        from("direct:handleReceivedInvalidResponse")
                .routeId("receivedInvalidResponse")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling received invalid response called.")
                .process(exchange -> {
                    final var initialResponse = exchange.getIn().getBody(Response.class);
                    final var exception = exchange
                            .getProperty(Exchange.EXCEPTION_CAUGHT, InvalidResponseException.class);
                    final var map = exception.getResponse();
                    final var response = new Response(initialResponse.getHeader(),
                            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
                    exchange.getIn().setBody(response);
                });
    }

}
