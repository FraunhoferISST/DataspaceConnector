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

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.ids.policy.ContractUtils;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.EntityUpdateService;
import io.dataspaceconnector.service.message.builder.type.MessageProcessedNotificationService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.dto.payload.AgreementClaimsContainer;
import io.dataspaceconnector.service.message.handler.exception.UnconfirmedAgreementException;
import io.dataspaceconnector.service.message.handler.processor.base.IdsProcessor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Compares the contract agreement given in a ContractAgreementMessage to the locally stored
 * agreement and generates the response.
 */
@Component("AgreementComparisonProcessor")
@RequiredArgsConstructor
class AgreementComparisonProcessor extends IdsProcessor<
        RouteMsg<ContractAgreementMessageImpl, ContractAgreement>> {

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * The global event publisher used for handling events.
     */
    private final @NonNull ApplicationEventPublisher publisher;

    /**
     * Service for handling notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Compares the contract agreement given by the consumer to the one stored in the database
     * and saves it as confirmed if they match.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if the contracts do not match or the confirmed agreement cannot be stored.
     */
    @Override
    protected Response processInternal(
            final RouteMsg<ContractAgreementMessageImpl, ContractAgreement> msg,
            final Jws<Claims> claims) throws Exception {
        final var agreement = msg.getBody();
        final var entity = entityResolver.getEntityById(agreement.getId());
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString());
        }
        final var storedAgreement = (Agreement) entity.get();
        final var storedIdsAgreement =
                deserializationService.getContractAgreement(storedAgreement.getValue());

        if (!ContractUtils.compareContractAgreements(agreement, storedIdsAgreement)) {
            throw new ContractException("Received agreement does not match stored agreement.");
        }

        if (!updateService.confirmAgreement(storedAgreement)) {
            throw new UnconfirmedAgreementException(storedAgreement,
                    "Could not confirm agreement.");
        }

        var agreementContainer = new AgreementClaimsContainer(agreement, claims);

        // Publish the agreement so that the designated event handler sends it to the CH.
        publisher.publishEvent(agreementContainer);

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Received contract agreement message.");
    }

}
