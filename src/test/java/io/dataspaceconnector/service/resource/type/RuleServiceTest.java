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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import io.dataspaceconnector.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RuleService.class})
public class RuleServiceTest {

    @MockBean
    private RuleRepository repository;

    @MockBean
    private ContractRuleFactory factory;

    @Autowired
    private RuleService service;

    @Test
    public void getAllByContract_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.getAllByContract(null));
    }

    @Test
    public void getAllByContract_validInput_returnRules() {
        /* ARRANGE */
        final var contractId = UUID.randomUUID();
        final var rules = List.of(new ContractRule());

        when(repository.findAllByContract(contractId)).thenReturn(rules);

        /* ACT */
        final var result = service.getAllByContract(contractId);

        /* ASSERT */
        assertEquals(rules, result);
    }
}
