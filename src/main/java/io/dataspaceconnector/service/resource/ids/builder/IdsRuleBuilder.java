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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The base class for constructing an ids rule from a dsc rule.
 *
 * @param <T> The ids rule type.
 */
public class IdsRuleBuilder<T extends Rule> extends AbstractIdsBuilder<ContractRule, T> {

    /**
     * The service for deserializing strings to ids rules.
     */
    private final @NonNull DeserializationService deserializer;

    /**
     * The type of the rule to be build. Needed for the deserializer.
     */
    private final @NonNull Class<T> ruleType;

    /**
     * Constructs an IdsRuleBuilder.
     *
     * @param selfLinkHelper the self link helper.
     * @param deserializationService the deserialization service.
     * @param type the rule type.
     */
    @Autowired
    public IdsRuleBuilder(final SelfLinkHelper selfLinkHelper,
                          final DeserializationService deserializationService,
                          final Class<T> type) {
        super(selfLinkHelper);
        this.deserializer = deserializationService;
        this.ruleType = type;
    }

    @Override
    protected final T createInternal(final ContractRule rule, final int currentDepth,
                                     final int maxDepth)
            throws ConstraintViolationException {
        final var idsRule = deserializer.getRule(rule.getValue());
        final var selfLink = getAbsoluteSelfLink(rule);
        var newRule = rule.getValue();

        // Note: IDS deserializer sets autogen ID, when ID is missing in original rule value.
        // If autogen ID not present in original rule value, it's equal to rule not having ID
        if (idsRule.getId() == null || !rule.getValue().contains(idsRule.getId().toString())) {
            // No id has been set for this rule. Thus, no references can be found.
            // Inject the real id.
            newRule = newRule.substring(0, newRule.indexOf("{") + 1)
                    + "\"@id\": \""
                    + selfLink + "\","
                    + newRule
                    .substring(newRule.indexOf("{") + 1);
        } else {
            // The id has been set, there may be references.
            // Search for the id and replace everywhere.
            newRule = newRule.replace(idsRule.getId().toString(), selfLink.toString());

        }

        return deserializer.getRule(newRule, ruleType);
    }
}
