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
package io.dataspaceconnector.camel.errorhandler;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Defines the route used for handling errors, which will send an error description to the
 * Configuration Manager endpoint defined in application.properties.
 */
@Component
public class DeadLetterChannel extends RouteBuilder {

    /**
     * The Configuration Manager endpoint for logging errors.
     */
    @Value("${config-manager.error-api.url}")
    private String errorLogEndpoint;

    /**
     * Configures the error route. The error route uses a processor to create an {@link RouteError}
     * and then sends this to the Configuration Manager.
     */
    @Override
    public void configure() {
        onException(Exception.class)
                .process(exchange -> {
                    final var cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                            Exception.class);
                    log.warn("Failed to send error logs to Configuration Manager. [exception=({})]",
                            cause.getMessage());
                })
                .handled(true);

        from("direct:deadLetterChannel")
                .process("dlcProcessor")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .to(errorLogEndpoint);
    }

}
