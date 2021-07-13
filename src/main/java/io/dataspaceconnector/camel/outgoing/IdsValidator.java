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
package io.dataspaceconnector.camel.outgoing;

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

public abstract class IdsValidator implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange);
    }

    protected abstract void processInternal(Exchange exchange);

}

@Component("ContractAgreementValidator")
@RequiredArgsConstructor
class ContractAgreementValidator extends IdsValidator {

    private final @NonNull ContractManager contractManager;

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

@Component("RuleListInputValidator")
class RuleListInputValidator extends IdsValidator {

    @Override
    protected void processInternal(final Exchange exchange) {
        final var ruleList = (List<Rule>) exchange.getProperty("ruleList", List.class);

        // Validate input for contract request.
        RuleUtils.validateRuleTarget(ruleList);
    }

}
