package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a ContractRule.
 */
@Component
public class ContractRuleFactory implements AbstractFactory<ContractRule, ContractRuleDesc> {

    /**
     * The default remote id assigned to all contract rules.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * The default title assigned to all contract rules.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * The default rule assigned to all contract rules.
     */
    public static final String DEFAULT_RULE = "";

    /**
     * Default constructor.
     */
    public ContractRuleFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new ContractRule.
     * @param desc The description of the new ContractRule.
     * @return The new ContractRule.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public ContractRule create(final ContractRuleDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var rule = new ContractRule();
        rule.setContracts(new ArrayList<>());

        update(rule, desc);

        return rule;
    }

    /**
     * Update a ContractRule.
     * @param contractRule The ContractRule to be updated.
     * @param desc         The new ContractRule description.
     * @return True if the ContractRule has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final ContractRule contractRule, final ContractRuleDesc desc) {
        Utils.requireNonNull(contractRule, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRemoteId = this.updateRemoteId(contractRule, desc.getRemoteId());
        final var hasUpdatedTitle = this.updateTitle(contractRule, desc.getTitle());
        final var hasUpdatedRule = this.updateRule(contractRule, desc.getValue());
        final var hasUpdatedAdditional = this.updateAdditional(contractRule, desc.getAdditional());

        return hasUpdatedRemoteId || hasUpdatedTitle || hasUpdatedRule || hasUpdatedAdditional;
    }

    private boolean updateRemoteId(final ContractRule contractRule, final URI remoteId) {
        final var newUri = MetadataUtils.updateUri(
                contractRule.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(contractRule::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateTitle(final ContractRule contractRule, final String title) {
        final var newTitle =
                MetadataUtils.updateString(contractRule.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(contractRule::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateRule(final ContractRule contractRule, final String rule) {
        final var newRule = MetadataUtils.updateString(contractRule.getValue(), rule, DEFAULT_RULE);
        newRule.ifPresent(contractRule::setValue);

        return newRule.isPresent();
    }

    private boolean updateAdditional(
            final ContractRule contractRule, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                contractRule.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(contractRule::setAdditional);

        return newAdditional.isPresent();
    }
}
