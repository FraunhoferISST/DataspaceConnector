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
package io.dataspaceconnector.service.message.handler.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.RequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.exception.InvalidInputException;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.message.builder.type.MessageProcessedNotificationService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.processor.base.IdsProcessor;
import io.dataspaceconnector.service.resource.type.SubscriptionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Generates the response to a SubscriptionMessage.
 */
@Component("ProcessedSubscription")
@Log4j2
@RequiredArgsConstructor
class SubscriptionProcessor extends IdsProcessor<RouteMsg<RequestMessageImpl, ?>> {

    /**
     * Service for handling message processed subscription messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Handler for adding and removing subscriptions.
     */
    private final @NonNull SubscriptionService subscriptionSvc;

    /**
     * Creates a MessageProcessedNotificationMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<RequestMessageImpl, ?> msg,
                                       final Jws<Claims> claims) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var target = MessageUtils.extractTargetId(msg.getHeader());
        final var optional = getSubscriptionFromPayload((MessagePayload) msg.getBody());

        String response;
        if (optional.isPresent()) {
            final var subscription = optional.get();

            // Create new subscription.
            final var desc = new SubscriptionDesc();
            desc.setSubscriber(issuer);
            desc.setTarget(subscription.getTarget());
            desc.setPushData(subscription.isPushData());
            desc.setLocation(subscription.getLocation());

            // Set boolean to true as this subscription has been created via ids message.
            desc.setIdsProtocol(true);

            // Create subscription (this will also be linked to the target)
            subscriptionSvc.create(desc);

            response = "Successfully subscribed to %s.";
        } else {
            subscriptionSvc.removeSubscription(target, issuer);
            response = "Successfully unsubscribed from %s.";
        }

        // Build the ids response.
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, String.format(response, target));
    }

    /**
     * Read subscription from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the subscription input.
     * @throws InvalidInputException if the subscription is not empty but invalid.
     */
    private Optional<Subscription> getSubscriptionFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("") || payload.equals("null")) {
                return Optional.empty();
            } else {
                final var subscription = new ObjectMapper().readValue(payload, Subscription.class);
                return Optional.of(subscription);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid subscription payload. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid subscription payload.", e);
        }
    }
}
