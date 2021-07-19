package io.dataspaceconnector.camel.processor.controller.ids.validator;

import java.util.List;

import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.util.RuleUtils;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Validates the list of rules given as user input for sending a contract request.
 */
@Component("RuleListInputValidator")
public class RuleListInputValidator extends IdsValidator {

    /**
     * Check if every rule in the list of rules contains a target.
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
