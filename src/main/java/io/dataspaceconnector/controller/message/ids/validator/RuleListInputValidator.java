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
package io.dataspaceconnector.controller.message.ids.validator;

import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.controller.message.ids.validator.base.IdsValidator;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validates the list of rules given as user input for sending a contract request.
 */
@Component("RuleListInputValidator")
public class RuleListInputValidator extends IdsValidator {

    /**
     * Check if every rule in the list of rules contains a target.
     *
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var ruleList = exchange.getProperty(ParameterUtils.RULE_LIST_PARAM, List.class);

        // Validate input for contract request.
        RuleUtils.validateRuleTarget(toRuleList(ruleList));
    }

    @SuppressWarnings("unchecked")
    private static List<Rule> toRuleList(final List<?> list) {
        return (List<Rule>) list;
    }
}
