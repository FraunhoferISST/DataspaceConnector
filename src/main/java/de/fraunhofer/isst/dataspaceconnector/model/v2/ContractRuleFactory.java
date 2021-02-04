package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

@Component
public class ContractRuleFactory implements BaseFactory<ContractRule, ContractRuleDesc> {
    @Override
    public ContractRule create(final ContractRuleDesc desc) {
        var rule = new ContractRule();
        update(rule, desc);

        return rule;
    }

    @Override
    public boolean update(final ContractRule contractRule, final ContractRuleDesc desc) {
        var hasBeenUpdated = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (!newTitle.equals(contractRule.getTitle())) {
            contractRule.setTitle(newTitle);
            hasBeenUpdated = true;
        }

        var newRule = desc.getRule() != null ? desc.getRule() : "";
        if (!newRule.equals(contractRule.getValue())) {
            contractRule.setValue(newRule);
            hasBeenUpdated = true;
        }

        return hasBeenUpdated;
    }
}
