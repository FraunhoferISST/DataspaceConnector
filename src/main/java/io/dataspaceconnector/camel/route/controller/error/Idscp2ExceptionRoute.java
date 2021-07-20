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

import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling Idscp2Exceptions. When an Idscp2Exception occurs, it's always
 * wrapped by an ExecutionException.
 */
@Component
public class Idscp2ExceptionRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        from("direct:handleIdscp2Exception")
                .routeId("idscp2Exception")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling IDSCP2 exception called.")
                .to(ParameterUtils.CONTROLLER_UTILS_BEAN
                        + "respondIdscp2Error(${exchangeProperty.recipient}, "
                        + "${exception.getCause})");
    }
}
