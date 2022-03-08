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
package io.dataspaceconnector.model.rule;

import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import java.net.URI;
import java.util.ArrayList;

/**
 * Creates and updates a ContractRule.
 */
public class ContractRuleFactory extends AbstractNamedFactory<ContractRule, ContractRuleDesc> {

    /**
     * The default remote id assigned to all contract rules.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * The default rule assigned to all contract rules.
     */
    public static final String DEFAULT_RULE = "";

    /**
     * Create a new ContractRule.
     *
     * @param desc The description of the new ContractRule.
     * @return The new ContractRule.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    protected ContractRule initializeEntity(final ContractRuleDesc desc) {
        final var rule = new ContractRule();
        rule.setContracts(new ArrayList<>());

        return rule;
    }

    /**
     * Update a ContractRule.
     *
     * @param contractRule The ContractRule to be updated.
     * @param desc         The new ContractRule description.
     * @return True if the ContractRule has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    protected boolean updateInternal(final ContractRule contractRule, final ContractRuleDesc desc) {
        final var hasUpdatedRemoteId = this.updateRemoteId(contractRule, desc.getRemoteId());
        final var hasUpdatedRule = this.updateRule(contractRule, desc.getValue());

        return hasUpdatedRemoteId || hasUpdatedRule;
    }

    private boolean updateRemoteId(final ContractRule contractRule, final URI remoteId) {
        final var newUri = FactoryUtils.updateUri(contractRule.getRemoteId(),
                remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(contractRule::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateRule(final ContractRule contractRule, final String rule) {
        final var newRule = FactoryUtils.updateString(contractRule.getValue(), rule, DEFAULT_RULE);
        newRule.ifPresent(contractRule::setValue);

        return newRule.isPresent();
    }
}
