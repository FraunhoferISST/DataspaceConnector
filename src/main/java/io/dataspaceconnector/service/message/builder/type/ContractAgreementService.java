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
package io.dataspaceconnector.service.message.builder.type;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.common.ids.mapping.RdfConverter;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.message.ContractAgreementMessageDesc;
import io.dataspaceconnector.service.message.builder.type.base.AbstractMessageService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

/**
 * Message service for ids contract agreement messages.
 */
@Service
public final class ContractAgreementService
        extends AbstractMessageService<ContractAgreementMessageDesc> {

    /**
     * @throws IllegalArgumentException     if desc is null.
     * @throws ConstraintViolationException if security tokes is null or another error appears
     *                                      when building the message.
     */
    @Override
    public Message buildMessage(final ContractAgreementMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        Utils.requireNonNull(token, ErrorMessage.DAT_NULL);

        final var recipient = desc.getRecipient();
        final var correlationMessage = desc.getCorrelationMessage();

        return new ContractAgreementMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._correlationMessage_(correlationMessage)
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return MessageProcessedNotificationMessageImpl.class;
    }

    /**
     * Build and send a contract agreement message and then validate the response.
     *
     * @param recipient The recipient.
     * @param agreement The contract agreement.
     * @return The response map.
     * @throws MessageException             if message handling failed.
     * @throws MessageResponseException     if the response could not be processed.
     * @throws UnexpectedResponseException  if the response is not as expected.
     * @throws IllegalArgumentException     if contract agreement is null.
     * @throws ConstraintViolationException if contract agreement is malformed.
     */
    public Map<String, String> sendMessage(final URI recipient, final ContractAgreement agreement)
            throws MessageException, MessageResponseException, UnexpectedResponseException,
            ConstraintViolationException {
        Utils.requireNonNull(agreement, ErrorMessage.ENTITY_NULL);

        final var contractRdf = RdfConverter.toRdf(agreement);
        final var desc = new ContractAgreementMessageDesc(recipient, agreement.getId());
        final var response = send(desc, contractRdf);

        try {
            if (!validateResponse(response)) {
                throw new UnexpectedResponseException(getResponseContent(response));
            }
        } catch (MessageResponseException e) {
            throw new UnexpectedResponseException(getResponseContent(response), e);
        }

        return response;
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
