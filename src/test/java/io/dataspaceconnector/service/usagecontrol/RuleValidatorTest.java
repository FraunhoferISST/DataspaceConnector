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
package io.dataspaceconnector.service.usagecontrol;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import io.dataspaceconnector.controller.policy.util.PatternUtils;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.model.pattern.SecurityRestrictionDesc;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.EntityDependencyResolver;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;


@SpringBootTest(classes = { RuleValidator.class })
class RuleValidatorTest {

    @MockBean
    private PolicyExecutionService executionService;

    @MockBean
    private PolicyInformationService informationService;

    @MockBean
    private EntityDependencyResolver dependencyResolver;

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private RuleValidator validator;

    @Test
    public void validatePolicy_USAGE_DURING_INTERVAL_doNothing() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient");
        final var rule = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                  ._operator_(BinaryOperator.AFTER)
                                                  ._rightOperand_(new RdfResource("2009-05-07T17:05:45.678Z",
                                                                                  URI.create("xsd:dateTimeStamp")))
                                                  .build(), new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                  ._operator_(BinaryOperator.BEFORE)
                                                  ._rightOperand_(new RdfResource("2029-05-07T17:05:45.678Z", URI.create("xsd:dateTimeStamp")))
                                                  .build()))
                .build();
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> validator.validatePolicy( PolicyPattern.USAGE_DURING_INTERVAL, rule, target, recipient, Optional.empty(), agreementId));
    }

    @Test
    public void validatePolicy_USAGE_DURING_INTERVAL_notATimeFail() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient");
        final var rule = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                  ._operator_(BinaryOperator.AFTER)
                                                  ._rightOperand_(new RdfResource("some long long time ago",
                                                                                  URI.create("xsd:dateTimeStamp")))
                                                  .build(), new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                  ._operator_(BinaryOperator.BEFORE)
                                                  ._rightOperand_(new RdfResource("2029-05-07T17:05:45.678Z", URI.create("xsd:dateTimeStamp")))
                                                  .build()))
                .build();
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        /* ACT && ASSERT */
        final var result = assertThrows(PolicyRestrictionException.class, () -> validator.validatePolicy( PolicyPattern.USAGE_DURING_INTERVAL, rule, target, recipient, Optional.empty(), agreementId));
        assertEquals(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL.toString(), result.getMessage());
    }

    @Test
    public void validatePolicy_USAGE_DURING_INTERVAL_EndAlreadyPassedFails() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient");
        final var rule = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                  ._operator_(BinaryOperator.AFTER)
                                                  ._rightOperand_(new RdfResource("2009-05-07T17:05:45.678Z",
                                                                                  URI.create("xsd:dateTimeStamp")))
                                                  .build(), new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                  ._operator_(BinaryOperator.BEFORE)
                                                  ._rightOperand_(new RdfResource("2019-05-07T17:05:45.678Z", URI.create("xsd:dateTimeStamp")))
                                                  .build()))
                .build();
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        /* ACT && ASSERT */
        final var result = assertThrows(PolicyRestrictionException.class, () -> validator.validatePolicy(PolicyPattern.USAGE_DURING_INTERVAL, rule, target, recipient, Optional.empty(), agreementId));
        assertEquals(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL.toString(), result.getMessage());
    }

    @Test
    public void validatePolicy_N_TIMES_USAGE_doNothing() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient");
        final var rule = new PermissionBuilder()
                ._action_(List.of(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.COUNT)
                                                  ._operator_(BinaryOperator.EQ)
                                                  ._rightOperand_(new RdfResource("5"))
                                                  .build()))
                .build();
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        Mockito.when(informationService.getAccessNumber(eq(target))).thenReturn(0L);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> validator.validatePolicy( PolicyPattern.N_TIMES_USAGE, rule, target, recipient, Optional.empty(),agreementId));
    }

    @Test
    public void validatePolicy_N_TIMES_USAGE_failsOnExceededUsage() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient");
        final var rule = new PermissionBuilder()
                ._action_(List.of(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.COUNT)
                                                  ._operator_(BinaryOperator.EQ)
                                                  ._rightOperand_(new RdfResource("5"))
                                                  .build()))
                .build();
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        Mockito.when(informationService.getAccessNumber(eq(target))).thenReturn(6L);

        /* ACT && ASSERT */
        final var result = assertThrows(PolicyRestrictionException.class, () -> validator.validatePolicy(PolicyPattern.N_TIMES_USAGE, rule, target, recipient, Optional.empty(), agreementId));
        assertEquals(ErrorMessage.DATA_ACCESS_NUMBER_REACHED.toString(), result.getMessage());
    }

    @Test
    public void validatePolicy_CONNECTOR_RESTRICTED_USAGE_doNothing() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient");
        final var rule = new PermissionBuilder()
                ._action_(List.of(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                      ._leftOperand_(LeftOperand.ENDPOINT)
                                      ._operator_(BinaryOperator.DEFINES_AS)
                                      ._rightOperand_(new RdfResource(recipient.toString(), URI.create("xsd:anyURI")))
                                      .build()))
                                  .build();
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> validator.validatePolicy( PolicyPattern.CONNECTOR_RESTRICTED_USAGE, rule, target, recipient, Optional.empty(), agreementId));
    }

    @Test
    public void validatePolicy_CONNECTOR_RESTRICTED_USAGE_failsOnDifferentConsumer() {
        /* ARRANGE */
        final var consumer = "https://consumer";
        final var rule = new PermissionBuilder()
                ._action_(List.of(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                                                  ._leftOperand_(LeftOperand.ENDPOINT)
                                                  ._operator_(BinaryOperator.DEFINES_AS)
                                                  ._rightOperand_(new RdfResource(consumer, URI.create("xsd:anyURI")))
                                                  .build()))
                .build();
        final var recipient = URI.create("https://recipient");
        final var target = URI.create("https://target");
        final var agreementId = URI.create("https://target");

        /* ACT && ASSERT */
        final var result = assertThrows(PolicyRestrictionException.class, () -> validator.validatePolicy(PolicyPattern.CONNECTOR_RESTRICTED_USAGE, rule, target, recipient, Optional.empty(), agreementId));
        assertEquals(ErrorMessage.DATA_ACCESS_INVALID_CONSUMER.toString(), result.getMessage());
    }

    @SneakyThrows
    @Test
    public void validateSecurityProfile_matchingInput_throwNothing() {
        /* ARRANGE */
        final var profile = SecurityProfile.BASE_SECURITY_PROFILE;
        final var desc = new SecurityRestrictionDesc();
        desc.setProfile("idsc:BASE_SECURITY_PROFILE");
        final var rule = PatternUtils.buildSecurityProfileRestrictedUsageRule(desc);

        final var target = URI.create("https://target");
        final var issuer = URI.create("https://issuer");
        final var agreementId = URI.create("https://agreementId");

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> validator.validatePolicy(
                PolicyPattern.SECURITY_PROFILE_RESTRICTED_USAGE, rule, target, issuer,
                Optional.of(profile), agreementId));
    }
}
