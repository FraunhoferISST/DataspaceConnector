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

import de.fraunhofer.iais.eis.RequestMessageImpl;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.model.message.SubscriptionMessageDesc;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.message.builder.base.IdsMessageBuilder;
import io.dataspaceconnector.service.message.builder.type.SubscriptionRequestService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;

/**
 * Builds a RequestMessage and creates a request DTO with header and payload.
 */
@Component("SubscriptionRequestMessageBuilder")
@RequiredArgsConstructor
public class SubscriptionRequestMessageBuilder
        extends IdsMessageBuilder<RequestMessageImpl, SubscriptionDesc> {

    /**
     * Service for message handling.
     */
    private final @NonNull SubscriptionRequestService subscriptionReqSvc;

    /**
     * Builds a RequestMessage according to the exchange properties and creates a Request
     * with the message as header and the {@link SubscriptionDesc} from the exchange properties
     * as payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<RequestMessageImpl, SubscriptionDesc, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var subscription = exchange
                .getProperty(ParameterUtils.SUBSCRIPTION_DESC_PARAM, SubscriptionDesc.class);

        final var target = getTarget(exchange, subscription);
        final var messageDesc = new SubscriptionMessageDesc(recipient, target);
        final var message = (RequestMessageImpl) subscriptionReqSvc.buildMessage(messageDesc);

        return new Request<>(message, subscription, Optional.empty());
    }

    private URI getTarget(final Exchange exchange, final SubscriptionDesc subscription) {
        return subscription == null
                ? exchange.getProperty(ParameterUtils.ELEMENT_ID_PARAM, URI.class)
                : subscription.getTarget();
    }

}
