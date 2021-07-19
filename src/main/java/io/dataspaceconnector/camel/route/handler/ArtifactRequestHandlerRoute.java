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
package io.dataspaceconnector.camel.route.handler;

import java.io.IOException;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.camel.exception.NoRequestedArtifactException;
import io.dataspaceconnector.exception.InvalidInputException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling an ArtifactRequestMessage.
 */
@Component
public class ArtifactRequestHandlerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configure() throws Exception {
        onException(NoRequestedArtifactException.class)
                .to("direct:handleNoRequestedArtifactException");
        onException(InvalidInputException.class)
                .to("direct:handleInvalidQueryInputException");
        onException(IOException.class, ConstraintViolationException.class)
                .to("direct:handleResponseMessageBuilderException");
        onException(Exception.class)
                .to("direct:handleDataRetrievalError");

        from("direct:artifactRequestHandler")
                .routeId("artifactRequestHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("RequestedArtifactValidator")
                .choice()
                    .when(simple("${bean:connectorConfiguration.isPolicyNegotiation} == true"))
                        .to("direct:policyCheck")
                .end()
                .process("DataRequestProcessor");
    }

}
