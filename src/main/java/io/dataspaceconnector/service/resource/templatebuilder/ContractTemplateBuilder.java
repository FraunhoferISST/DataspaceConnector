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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.template.ContractTemplate;
import io.dataspaceconnector.service.resource.relation.ContractRuleLinker;
import io.dataspaceconnector.service.resource.type.ContractService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

/**
 * Builds contracts from templates.
 */
@RequiredArgsConstructor
public class ContractTemplateBuilder {

    /**
     * Service for contracts.
     */
    private final @NonNull ContractService contractService;

    /**
     * Links contracts to contract rules.
     */
    private final @NonNull ContractRuleLinker contractRuleLinker;

    /**
     * Builds contract rules.
     */
    private final @NonNull ContractRuleTemplateBuilder contractRuleBuilder;

    /**
     * Build a contract and dependencies from a template.
     *
     * @param template The contract template.
     * @return The new contract.
     * @throws IllegalArgumentException if the passed template is null.
     */
    public Contract build(final ContractTemplate template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);

        final var ruleIds = Utils.toStream(template.getRules())
                .map(x -> contractRuleBuilder.build(x).getId())
                .collect(Collectors.toSet());
        final var contract = contractService.create(template.getDesc());
        contractRuleLinker.add(contract.getId(), ruleIds);

        return contract;
    }
}
