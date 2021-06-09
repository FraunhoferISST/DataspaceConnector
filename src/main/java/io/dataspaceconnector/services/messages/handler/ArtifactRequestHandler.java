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
package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import io.dataspaceconnector.services.messages.handler.camel.Request;
import io.dataspaceconnector.services.messages.handler.camel.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

/**
 * This @{@link ArtifactRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@Log4j2
@SupportedMessageType(ArtifactRequestMessageImpl.class)
@RequiredArgsConstructor
public class ArtifactRequestHandler implements MessageHandler<ArtifactRequestMessageImpl> {

    private final @NonNull ProducerTemplate template;

    private final @NonNull CamelContext context;

    /**
     * This message implements the logic that is needed to handle the message. As it returns the
     * input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The request message.
     * @param payload The message payload.
     * @return The response message.
     * @throws RuntimeException If the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(final ArtifactRequestMessageImpl message,
                                         final MessagePayload payload) throws RuntimeException {
        final var result = template.send("direct:artifactRequestHandler",
                ExchangeBuilder.anExchange(context)
                    .withBody(new Request(message, payload))
                    .build());

        final var response = result.getIn().getBody(Response.class);
        if (response != null) {
            return BodyResponse.create(response.getHeader(), response.getBody());
        } else {
            return result.getIn().getBody(ErrorResponse.class);
        }
    }
}
