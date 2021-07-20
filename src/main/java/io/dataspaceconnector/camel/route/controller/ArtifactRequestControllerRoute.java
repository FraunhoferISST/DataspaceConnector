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
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for requesting an artifact over IDSCPv2.
 */
@Component
public class ArtifactRequestControllerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        onException(InvalidResponseException.class)
                .process("PolicyRestrictionProcessor");
        onException(SocketTimeoutException.class)
                .to("direct:handleSocketTimeout");
        onException(Idscp2Exception.class)
                .to("direct:handleIdscp2Exception");

        from("direct:artifactRequestSender")
                .routeId("artifactRequestSender")
                .process("ArtifactRequestMessageBuilder")
                .process("ArtifactRequestPreparer")
                .toD(ParameterUtils.IDSCP_CLIENT_URI)
                .process("ResponseToDtoConverter")
                .process("ArtifactResponseValidator");
    }

}
