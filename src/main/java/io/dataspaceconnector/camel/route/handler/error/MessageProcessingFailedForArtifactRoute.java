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
package io.dataspaceconnector.camel.route.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling MessageProcessingFailedExceptions during the artifact request.
 */
@Component
public class MessageProcessingFailedForArtifactRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @Override
    public void configure() throws Exception {
        from("direct:handleMessageProcessingFailedForArtifact")
                .routeId("messageProcessingFailedForArtifact")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling failed message processing called.")
                .to("bean:messageResponseService?method=handleMessageProcessingFailedForArtifact("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getTransferContract()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
