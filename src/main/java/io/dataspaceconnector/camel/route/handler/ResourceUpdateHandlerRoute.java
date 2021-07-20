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

import io.dataspaceconnector.camel.exception.DeserializationException;
import io.dataspaceconnector.camel.exception.InvalidAffectedResourceException;
import io.dataspaceconnector.camel.exception.MissingPayloadException;
import io.dataspaceconnector.camel.exception.NoAffectedResourceException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Builds the route for handling a ResourceUpdateMessage.
 */
@Component
public class ResourceUpdateHandlerRoute extends RouteBuilder {

    /**
     * Configures the route.
     *
     * @throws Exception if any error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configure() throws Exception {
        onException(NoAffectedResourceException.class)
                .to("direct:handleNoAffectedResourceException");
        onException(InvalidAffectedResourceException.class)
                .to("direct:handleInvalidAffectedResourceException");
        onException(IllegalStateException.class)
                .to("direct:handleResponseMessageBuilderException");
        onException(IOException.class, IllegalArgumentException.class)
                .to("direct:handleMessagePayloadException");

        from("direct:resourceUpdateHandler")
                .routeId("resourceUpdateHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("AffectedResourceValidator")
                .doTry()
                    .process("ResourceDeserializer")
                    .doCatch(DeserializationException.class)
                        .to("direct:handleWrappedIllegalArgumentException")
                        .stop()
                    .doCatch(MissingPayloadException.class)
                        .to("direct:handleMissingPayloadException")
                        .stop()
                .end()
                .process("CorrectAffectedResourceValidator")
                .process("ResourceUpdateProcessor");
    }

}
