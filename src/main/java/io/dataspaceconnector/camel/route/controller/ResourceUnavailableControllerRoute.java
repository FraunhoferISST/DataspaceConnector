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
package io.dataspaceconnector.camel.route.controller;

import java.net.SocketTimeoutException;

import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2Exception;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for sending a resource unavailable message over IDSCP_v2.
 */
@Component
public class ResourceUnavailableControllerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        onException(ResourceNotFoundException.class)
                .to("direct:handleResourceNotFoundForController");
        onException(SocketTimeoutException.class)
                .to("direct:handleSocketTimeout");
        onException(Idscp2Exception.class)
                .to("direct:handleIdscp2Exception");

        from("direct:resourceUnavailableSender")
                .routeId("resourceUnavailableSender")
                .process("ResourceFinder")
                .process("ResourceUnavailableMessageBuilder")
                .process("RequestWithResourcePayloadPreparer")
                .toD(ParameterUtils.IDSCP_CLIENT_URI)
                .process("ResponseToDtoConverter");
    }

}
