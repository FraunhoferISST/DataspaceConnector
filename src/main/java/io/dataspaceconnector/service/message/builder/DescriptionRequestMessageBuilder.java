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
package io.dataspaceconnector.service.message.builder;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.model.message.DescriptionRequestMessageDesc;
import io.dataspaceconnector.service.message.builder.base.IdsMessageBuilder;
import io.dataspaceconnector.service.message.builder.type.DescriptionRequestService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Builds a DescriptionRequestMessage and creates a request DTO with header and payload.
 */
@Component("DescriptionRequestMessageBuilder")
@RequiredArgsConstructor
public class DescriptionRequestMessageBuilder extends
        IdsMessageBuilder<DescriptionRequestMessageImpl, String> {

    /**
     * Service for description request message handling.
     */
    private final @NonNull DescriptionRequestService descReqSvc;

    /**
     * Builds a DescriptionRequestMessage according to the exchange properties and creates a Request
     * with the message as header and an empty payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<DescriptionRequestMessageImpl, String, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        var elementId = exchange.getProperty(ParameterUtils.ELEMENT_ID_PARAM, URI.class);
        if (elementId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            if (index != null) {
                final var resources = exchange
                        .getProperty(ParameterUtils.RESOURCES_PARAM, List.class);
                elementId = (URI) resources.get(index);
            }
        }
        final var message = (DescriptionRequestMessageImpl) descReqSvc
                .buildMessage(new DescriptionRequestMessageDesc(recipient, elementId));

        return new Request<>(message, "", Optional.empty());
    }
}
