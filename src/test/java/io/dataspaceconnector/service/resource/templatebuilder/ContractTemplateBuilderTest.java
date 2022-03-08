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

import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.template.ContractTemplate;
import io.dataspaceconnector.repository.ContractRepository;
import io.dataspaceconnector.service.resource.relation.ContractRuleLinker;
import io.dataspaceconnector.service.resource.type.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class ContractTemplateBuilderTest {

    private ContractRepository repository = Mockito.mock(ContractRepository.class);
    private ContractRuleLinker contractRuleLinker = Mockito.mock(ContractRuleLinker.class);

    private ContractTemplateBuilder builder = new ContractTemplateBuilder(
            new ContractService(repository, new ContractFactory()),
            contractRuleLinker,
            Mockito.mock(ContractRuleTemplateBuilder.class)
    );

    @BeforeEach
    public void setup() {
        Mockito.doAnswer(returnsFirstArg())
               .when(repository)
               .saveAndFlush(Mockito.any());
    }

    @Test
    public void build_ContractTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build(null));
    }

    @Test
    public void build_ContractTemplateValid_returnNewRule() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setTitle("Some title");
        final var template = new ContractTemplate(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
        Mockito.verify(contractRuleLinker, Mockito.atLeastOnce()).add(Mockito.any(), Mockito.any());
    }
}
