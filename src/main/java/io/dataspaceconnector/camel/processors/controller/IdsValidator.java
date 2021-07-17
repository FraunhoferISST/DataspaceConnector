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
package io.dataspaceconnector.camel.processors.controller;

import java.util.List;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.util.RuleUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Superclass for all processors that perform validation.
 */
public abstract class IdsValidator implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}.
     *
     * @param exchange the exchange.
     * @throws Exception if validation fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange);
    }

    /**
     * Performs validation. To be implemented by sub classes.
     *
     * @param exchange the exchange.
     */
    protected abstract void processInternal(Exchange exchange);

}

/**
 * Compares a received contract agreement to the initial contract request.
 */
@Component("ContractAgreementValidator")
@RequiredArgsConstructor
class ContractAgreementValidator extends IdsValidator {

    /**
     * Service for managing contracts.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Compares the contract agreement to the contract request.
     *
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var contractRequest = exchange
                .getProperty("contractRequest", ContractRequest.class);
        final var agreementString = exchange.getIn().getBody(Response.class).getBody();

        final var agreement = contractManager
                .validateContractAgreement(agreementString, contractRequest);

        exchange.setProperty("contractAgreement", agreement);
    }

}

/**
 * Validates the list of rules given as user input for sending a contract request.
 */
@Component("RuleListInputValidator")
class RuleListInputValidator extends IdsValidator {

    /**
     * Check if every rule in the list of rules contains a target.
     *
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var ruleList = (List<Rule>) exchange.getProperty("ruleList", List.class);

        // Validate input for contract request.
        RuleUtils.validateRuleTarget(ruleList);
    }

}
