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
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {DefaultCamelContext.class})
public class DeadLetterChannelProcessorTest {

    @Autowired
    private CamelContext camelContext;

    private DeadLetterChannelProcessor dlcProcessor = new DeadLetterChannelProcessor();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void process_createRouteErrorObject() {
        /* ARRANGE */
        final var routeId = "route-id";
        final var failureEndpoint = "https://some-endpoint.com";
        final var exceptionMessage = "Connection refused.";
        final var exception = new ConnectException(exceptionMessage);

        var exchange = new ExchangeBuilder(camelContext).withBody("Body").build();
        exchange.setProperty("CamelFailureRouteId", routeId);
        exchange.setProperty("CamelFailureEndpoint", failureEndpoint);
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exception);

        /* ACT */
        dlcProcessor.process(exchange);

        /* ASSERT */
        final var errorString = exchange.getIn().getBody(String.class);
        final var routeError = objectMapper.readValue(errorString, RouteError.class);
        assertNotNull(routeError);
        assertEquals(routeId, routeError.getRouteId());
        assertEquals(failureEndpoint, routeError.getEndpoint());
        assertEquals(exceptionMessage, routeError.getMessage());
        assertNotNull(routeError.getTimestamp());
    }

}
