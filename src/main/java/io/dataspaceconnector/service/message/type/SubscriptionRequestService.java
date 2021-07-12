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
package io.dataspaceconnector.service.message.type;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.RequestMessageBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.model.Subscription;
import io.dataspaceconnector.model.message.SubscriptionMessageDesc;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

/**
 * Message service for ids subscription and unsubscription messages. Note: currently not available.
 * An generic ids request message will be used. For an unsubscription message, a separate service
 * would have to be added in this package.
 */
@Log4j2
@Service
public final class SubscriptionRequestService extends AbstractMessageService<SubscriptionMessageDesc> {

    /**
     * @throws IllegalArgumentException     if desc is null.
     * @throws ConstraintViolationException if security tokes is null or another error appears
     *                                      when building the message.
     */
    @Override
    public Message buildMessage(final SubscriptionMessageDesc desc) throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();

        final var message = new RequestMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                .build();
        message.setProperty("ids:target", desc.getTarget());

        return message;
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return MessageProcessedNotificationMessageImpl.class;
    }

    /**
     * Send a request message. Allow the access only if that operation was successful.
     *
     * @param recipient    The message's recipient.
     * @param subscription The subscription object containing all relevant information.
     * @return The response map.
     * @throws MessageException         if message handling failed.
     * @throws IllegalArgumentException if contract agreement is null.
     */
    public Map<String, String> sendMessage(final URI recipient, final Subscription subscription)
            throws MessageException, ConstraintViolationException {
        Utils.requireNonNull(subscription, ErrorMessages.ENTITY_NULL);

        final var target = subscription.getTarget();
        return send(new SubscriptionMessageDesc(recipient, target), subscription);
    }

    /**
     * Send a request message. Allow the access only if that operation was successful.
     *
     * @param recipient The message's recipient.
     * @param target    The target element id.
     * @return The response map.
     * @throws MessageException         if message handling failed.
     * @throws IllegalArgumentException if contract agreement is null.
     */
    public Map<String, String> sendMessage(final URI recipient, final URI target)
            throws MessageException, ConstraintViolationException {
        Utils.requireNonNull(target, ErrorMessages.ENTITYID_NULL);

        return send(new SubscriptionMessageDesc(recipient, target), null);
    }

    /**
     * Check if the response message is of type message processed notification.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException if the response could not be read.
     */
    public boolean validateResponse(final Map<String, String> response)
            throws MessageResponseException {
        return isValidResponseType(response);
    }
}
