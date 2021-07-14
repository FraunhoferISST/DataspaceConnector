package io.configmanager.extensions.routes.petrinet;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import io.configmanager.extensions.routes.petrinet.builder.RuleFormulaBuilder;
import io.dataspaceconnector.service.usagecontrol.PolicyPattern;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for building a formula from policy pattern, rule and resource id
 */
@Log4j2
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
