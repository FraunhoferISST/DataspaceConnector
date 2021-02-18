package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;

/**
 * Creates and updates a ContractRule.
 */
@Component
public class ContractRuleFactory implements BaseFactory<ContractRule, ContractRuleDesc> {
    /**
     * Default constructor.
     */
    public ContractRuleFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new ContractRule.
     *
     * @param desc The description of the new ContractRule.
     * @return The new ContractRule.
     */
    @Override
    public ContractRule create(final ContractRuleDesc desc) {
        final var rule = new ContractRule();
        update(rule, desc);

        return rule;
    }

    /**
     * Update a ContractRule.
     *
     * @param contractRule The ContractRule to be updated.
     * @param desc         The new ContractRule description.
     * @return True if the ContractRule has been modified.
     */
    @Override
    public boolean update(final ContractRule contractRule, final ContractRuleDesc desc) {
        final var hasUpdatedTitle = this.updateTitle(contractRule, desc.getTitle());
        final var hasUpdatedRule = this.updateRule(contractRule, desc.getRule());

        return hasUpdatedTitle || hasUpdatedRule;
    }

    private boolean updateTitle(final ContractRule contractRule, final String title) {
        final var newTitle = MetadataUtils.updateString(contractRule.getTitle(), title, "");
        newTitle.ifPresent(contractRule::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateRule(final ContractRule contractRule, final String rule) {
        final var newRule = MetadataUtils.updateString(contractRule.getValue(), rule, "");
        newRule.ifPresent(contractRule::setValue);

        return newRule.isPresent();
    }
}
