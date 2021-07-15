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
package io.dataspaceconnector.service;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import io.dataspaceconnector.service.message.type.ContractRequestService;
import io.dataspaceconnector.service.message.type.exceptions.InvalidResponse;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.util.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

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
     * @param recipient The provider connector.
     * @param ruleList The rule willing to agree upon.
     * @return The agreement.
     * @throws InvalidResponse The ids response message is invalid.
     * @throws ConstraintViolationException Invalid ids.
     * @throws IllegalArgumentException Invalid input.
     * @throws ContractException The contract is not valid.
     */
    public UUID negotiate(final URI recipient, final List<Rule> ruleList)
            throws InvalidResponse, ConstraintViolationException, IllegalArgumentException,
            ContractException {
        final var request = contractManager.buildContractRequest(ruleList);

        // Send and validate contract request/response message.
        final var agreementResponse = contractReqSvc.sendMessageAndValidate(recipient, request);

        // Read and process the response message.
        final ContractAgreement agreement =
                validateAgreementAgainstOffer(request, agreementResponse);

        // Send and validate contract agreement/response message.
        agreementSvc.sendMessageAndValidate(recipient, agreement);

        return saveAgreement(agreement);
    }

    private ContractAgreement validateAgreementAgainstOffer(final ContractRequest request,
                                                   final Map<String, String> response)
            throws MessageResponseException, IllegalArgumentException, ContractException {
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
