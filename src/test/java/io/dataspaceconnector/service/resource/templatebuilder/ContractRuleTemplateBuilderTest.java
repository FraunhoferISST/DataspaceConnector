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

import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import io.dataspaceconnector.model.template.RuleTemplate;
import io.dataspaceconnector.repository.RuleRepository;
import io.dataspaceconnector.service.resource.type.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

class ContractRuleTemplateBuilderTest {

    private RuleRepository repository = Mockito.mock(RuleRepository.class);
    private ContractRuleTemplateBuilder builder = new ContractRuleTemplateBuilder(
            new RuleService(repository, new ContractRuleFactory())
    );

    @BeforeEach
    public void setup() {
        Mockito.doAnswer(returnsFirstArg())
               .when(repository)
               .saveAndFlush(Mockito.any());
    }

    @Test
    public void build_RuleTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build(null));
    }

    @Test
    public void build_RuleTemplateValid_returnNewRule() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setTitle("Some title");
        final var template = new RuleTemplate(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
    }
}
