package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

@Component
public class RuleFactory implements BaseFactory<Rule, RuleDesc> {
    @Override
    public Rule create(final RuleDesc desc) {
        var rule = new Rule();
        update(rule, desc);

        return rule;
    }

    @Override
    public boolean update(final Rule rule, final RuleDesc desc) {
        var hasBeenUpdated = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (!newTitle.equals(rule.getTitle())) {
            rule.setTitle(newTitle);
            hasBeenUpdated = true;
        }

        var newRule = desc.getRule() != null ? desc.getRule() : "";
        if (!newRule.equals(rule.getValue())) {
            rule.setValue(newRule);
            hasBeenUpdated = true;
        }

        return hasBeenUpdated;
    }
}
