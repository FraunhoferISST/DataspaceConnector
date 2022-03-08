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
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.template.RuleTemplate;
import io.dataspaceconnector.service.resource.type.RuleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds contract rules from templates.
 */
@RequiredArgsConstructor
public class ContractRuleTemplateBuilder {

    /**
     * Service for rules.
     */
    private final @NonNull RuleService ruleService;

    /**
     * Build a rule and dependencies from a template.
     *
     * @param template The rule template.
     * @return The new rule.
     * @throws IllegalArgumentException if the passed template is null.
     */
    public ContractRule build(final RuleTemplate template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);
        return ruleService.create(template.getDesc());
    }
}
