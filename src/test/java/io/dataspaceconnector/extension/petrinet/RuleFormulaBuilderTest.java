/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.extension.petrinet;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.extension.petrinet.builder.RuleFormulaBuilder;
import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for building a formula from policy pattern, rule and resource id
 */
@NoArgsConstructor
class RuleFormulaBuilderTest {

    URI uri;

    Rule rule;

    @BeforeEach
    void setup() {
        uri = URI.create("https://test");
        rule = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(
                        new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.COUNT)
                                ._operator_(BinaryOperator.EQ)
                                ._rightOperand_(new RdfResource(String.valueOf(2),
                                        URI.create("xsd:decimal")))
                                .build()))
                .build();
    }

    @Test
    void buildProvideAccess() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.PROVIDE_ACCESS, null, uri);
        assertNotNull(result);
    }

    @Test
    void buildProhibitAccess() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.PROHIBIT_ACCESS, null, uri);
        assertNotNull(result);
    }

    @Test
    void buildUsageUntilDeletion() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.USAGE_UNTIL_DELETION, null, uri);
        assertNotNull(result);
    }

    @Test
    void buildUsageULogging() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.USAGE_LOGGING, null, uri);
        assertNotNull(result);
    }

    @Test
    void buildNTimesUsage() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.N_TIMES_USAGE, rule, uri);
        assertNotNull(result);
    }

    @Test
    void buildUsageNotification() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.USAGE_NOTIFICATION, null, uri);
        assertNotNull(result);
    }

    @Test
    void buildConnectorRestrictedUsage() {
        final var result = RuleFormulaBuilder.buildFormula(PolicyPattern.CONNECTOR_RESTRICTED_USAGE, rule, uri);
        assertNotNull(result);
    }
}
