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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Defines the route used for handling errors, which will send an error description to the
 * Configuration Manager endpoint defined in application.properties.
 */
@Component
@RequiredArgsConstructor
public class DeadLetterChannel extends RouteBuilder {

    /**
     * The controller offering the route error API.
     */
    private final @NonNull ErrorController errorController;

    /**
     * Configures the error route. The error route uses a processor to create an
     * {@link io.dataspaceconnector.service.message.handler.dto.RouteError} and then sends this to
     * the Configuration Manager.
     */
    @Override
    public void configure() {
        onException(Exception.class)
                .process(this::processException)
                .handled(true);

        from("direct:deadLetterChannel")
                .process("dlcProcessor")
                .bean(errorController, "addRouteErrors(${body})");
    }

    @SuppressFBWarnings("CRLF_INJECTION_LOGS")
    private void processException(final Exchange exchange) {
        if (log.isWarnEnabled()) {
            final var cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                    Exception.class);
            log.warn("Failed to store error logs at RoutesController. [exception=({})]",
                    cause.getMessage());
        }
    }

}
