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
package io.dataspaceconnector.service.resource.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.RuleService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ContractRuleLinker.class})
class ContractRuleLinkerTest {
    @MockBean
    ContractService contractService;

    @MockBean
    RuleService ruleService;

    @Autowired
    @InjectMocks
    ContractRuleLinker linker;

    Contract contract = getContract();
    ContractRule rule = getRule();

    /***********************************************************************************************
     * getInternal                                                                                 *
     **********************************************************************************************/

    @Test
    public void getInternal_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> linker.getInternal(null));
    }

    @Test
    public void getInternal_Valid_returnOfferedResources() {
        /* ARRANGE */
        contract.getRules().add(rule);

        /* ACT */
        final var resources = linker.getInternal(contract);

        /* ASSERT */
        final var expected = List.of(rule);
        assertEquals(expected, resources);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private Contract getContract() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();
        ReflectionTestUtils.setField(contract,"rules", new ArrayList<ContractRule>());
        ReflectionTestUtils.setField(contract, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return contract;
    }

    @SneakyThrows
    private ContractRule getRule() {
        final var constructor = ContractRule.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var rule = constructor.newInstance();
        ReflectionTestUtils.setField(contract,"title", "Hello");
        ReflectionTestUtils.setField(contract, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return rule;
    }
}
