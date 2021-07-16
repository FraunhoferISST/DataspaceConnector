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
package io.dataspaceconnector.service.usagecontrol;

import de.fraunhofer.iais.eis.SecurityProfile;
import io.dataspaceconnector.model.pattern.SecurityRestrictionDesc;
import io.dataspaceconnector.util.PatternUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = RuleValidator.class)
public class RuleValidatorTest {

    @MockBean
    RuleValidator ruleValidator;

    @MockBean
    PolicyExecutionService executionService;

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

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> ruleValidator.validatePolicy(
                PolicyPattern.SECURITY_PROFILE_RESTRICTED_USAGE, rule, target, issuer, Optional.of(profile)));
    }
}
