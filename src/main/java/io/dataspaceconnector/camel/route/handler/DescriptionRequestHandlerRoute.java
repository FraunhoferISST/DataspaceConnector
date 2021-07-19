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

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.exception.InvalidResourceException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.exception.SelfLinkCreationException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling a DescriptionRequestMessage.
 */
@Component
public class DescriptionRequestHandlerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configure() throws Exception {
        onException(ResourceNotFoundException.class, InvalidResourceException.class)
                .to("direct:handleResourceNotFoundException");
        onException(IllegalStateException.class, ConstraintViolationException.class)
                .to("direct:handleResponseMessageBuilderException");
        onException(SelfLinkCreationException.class)
                .to("direct:handleSelfLinkCreationException");

        from("direct:descriptionRequestHandler")
                .routeId("descriptionRequestHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .choice()
                    .when(simple("${body.getHeader().getRequestedElement()} == null"))
                        .process("SelfDescription")
                    .otherwise()
                        .process("ResourceDescription");
    }

}
