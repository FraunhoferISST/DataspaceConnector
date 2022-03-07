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
package io.dataspaceconnector.controller.message.ids.processor;

import de.fraunhofer.iais.eis.ContractAgreement;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.controller.message.ids.processor.base.IdsResponseProcessor;
import io.dataspaceconnector.service.EntityPersistenceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Persists a contract agreement received as the response to a ContractRequestMessage.
 */
@Component("ContractAgreementPersistenceProcessor")
@RequiredArgsConstructor
public class ContractAgreementPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull
    EntityPersistenceService persistenceSvc;

    /**
     * Persists the contract agreement.
     *
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var agreement = exchange
                .getProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, ContractAgreement.class);
        final var agreementId = persistenceSvc.saveContractAgreement(agreement);
        exchange.setProperty(ParameterUtils.AGREEMENT_ID_PARAM, agreementId);
        exchange.setProperty(ParameterUtils.TRANSFER_CONTRACT_PARAM, agreement.getId());
    }

}
