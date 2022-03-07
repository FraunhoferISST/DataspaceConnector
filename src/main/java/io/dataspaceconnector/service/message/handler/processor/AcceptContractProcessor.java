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
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.ids.mapping.RdfConverter;
import io.dataspaceconnector.model.message.ContractAgreementMessageDesc;
import io.dataspaceconnector.service.EntityPersistenceService;
import io.dataspaceconnector.service.message.builder.type.ContractAgreementService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.service.message.handler.exception.AgreementPersistenceException;
import io.dataspaceconnector.service.message.handler.processor.base.IdsProcessor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.PersistenceException;
import java.util.ArrayList;

/**
 * Accepts a contract request and generates the response.
 */
@Log4j2
@Component("AcceptContractProcessor")
@RequiredArgsConstructor
class AcceptContractProcessor extends
        IdsProcessor<RouteMsg<ContractRequestMessageImpl, ContractTargetRuleMapContainer>> {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Service for ids contract agreement messages.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Creates a contract agreement from a given contract request and stores the agreement in the
     * database, before generating a response.
     *
     * @param msg the incoming message.
     * @return a Response object with a ContractAgreementMessage as header and the agreement as
     * payload.
     * @throws Exception if the agreement cannot be stores or the response cannot be built.
     */
    @Override
    protected Response processInternal(final RouteMsg<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer> msg, final Jws<Claims> claims) throws Exception {
        final var targets = new ArrayList<>(msg.getBody().getTargetRuleMap().keySet());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        // Turn the accepted contract request into a contract agreement and persist it.
        final ContractAgreement agreement;
        try {
            agreement = persistenceSvc.buildAndSaveContractAgreement(
                    msg.getBody().getContractRequest(), targets, issuer);
        } catch (ConstraintViolationException | PersistenceException exception) {
            throw new AgreementPersistenceException("Failed to build or persist agreement.",
                    exception);
        }

        // Build ids response message.
        final var desc = new ContractAgreementMessageDesc(issuer, messageId);
        final var header = agreementSvc.buildMessage(desc);
        if (log.isDebugEnabled()) {
            log.debug("Contract request accepted. [agreementId=({})]", agreement.getId());
        }

        // Send ids response message.
        return new Response(header, RdfConverter.toRdf(agreement));
    }

}
