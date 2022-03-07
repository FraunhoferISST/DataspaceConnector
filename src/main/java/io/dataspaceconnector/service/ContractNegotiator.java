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
package io.dataspaceconnector.service;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.RdfBuilderException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.message.builder.type.ContractAgreementService;
import io.dataspaceconnector.service.message.builder.type.ContractRequestService;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Negotiates contracts.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ContractNegotiator {

    /**
     * Service for contract request message handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Service for contract agreement message handling.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Negotiates till an agreement can be reached.
     *
     * @param recipient The provider connector.
     * @param ruleList  The rule willing to agree upon.
     * @return The agreement.
     * @throws UnexpectedResponseException  the ids response message is not as expected.
     * @throws MessageResponseException     if the response message is invalid.
     * @throws ConstraintViolationException if contract request could not be built.
     * @throws IllegalArgumentException     if the contract agreement is malformed.
     * @throws ContractException            if the contract agreement is invalid.
     * @throws PersistenceException         if contract agreement could not be stored.
     * @throws RdfBuilderException          if contract request could not be built.
     */
    public UUID negotiate(final URI recipient, final List<Rule> ruleList)
            throws UnexpectedResponseException, ConstraintViolationException,
            IllegalArgumentException, ContractException, PersistenceException,
            MessageResponseException, MessageException, RdfBuilderException {
        final var request = contractManager.buildContractRequest(ruleList);

        // Send and validate contract request/response message.
        final var agreementResponse = contractReqSvc.sendMessage(recipient, request);

        // Read and process the response message.
        final var agreement = validateAgreementAgainstOffer(request, agreementResponse);

        try {
            // Send and validate contract agreement/response message.
            agreementSvc.sendMessage(recipient, agreement);
        } catch (ConstraintViolationException e) {
            // Handle malformed contract agreement as invalid response.
            throw new MessageResponseException(e);
        }

        return saveAgreement(agreement);
    }

    /**
     * Validate contract agreement against offer.
     *
     * @param request  The contract request.
     * @param response The message response.
     * @throws IllegalArgumentException if the payload could not be read.
     * @throws ContractException        if the contract agreement is invalid.
     *
     * @return true, if contract agreement can be validated.
     */
    private ContractAgreement validateAgreementAgainstOffer(final ContractRequest request,
                                                            final Map<String, String> response)
            throws IllegalArgumentException, ContractException {
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        return contractManager.validateContractAgreement(payload, request);
    }

    private UUID saveAgreement(final ContractAgreement agreement) throws PersistenceException {
        final var agreementId = persistenceSvc.saveContractAgreement(agreement);
        if (log.isDebugEnabled()) {
            log.debug("Policy negotiation success. Saved agreement. [agreementId=({})].",
                    agreementId);
        }

        return agreementId;
    }
}
