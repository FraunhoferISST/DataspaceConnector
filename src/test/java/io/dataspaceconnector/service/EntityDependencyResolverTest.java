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
package io.dataspaceconnector.service;

import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {EntityDependencyResolver.class})
public class EntityDependencyResolverTest {

    @MockBean
    private ContractService contractService;

    @MockBean
    private RuleService ruleService;

    @MockBean
    private ArtifactService artifactService;

    @Autowired
    private EntityDependencyResolver resolver;

    @Test
    public void getRulesByContractOffer_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> resolver.getRulesByContractOffer(null));
    }

    @Test
    public void getRulesByContractOffer_validInput_returnRules() {
        /* ARRANGE */
        final var contract = new Contract();
        ReflectionTestUtils.setField(contract, "id", UUID.randomUUID());

        final var rules = List.of(new ContractRule());
        when(ruleService.getAllByContract(contract.getId())).thenReturn(rules);

        /* ACT */
        final var result = resolver.getRulesByContractOffer(contract);

        /* ASSERT */
        assertEquals(rules, result);
    }

    @Test
    public void getArtifactsByAgreement_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> resolver.getArtifactsByAgreement(null));
    }

    @Test
    public void getArtifactsByAgreement_validInput_returnArtifacts() {
        /* ARRANGE */
        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "id", UUID.randomUUID());

        final List<Artifact> artifacts = List.of(new ArtifactImpl());
        when(artifactService.getAllByAgreement(agreement.getId())).thenReturn(artifacts);

        /* ACT */
        final var result = resolver.getArtifactsByAgreement(agreement);

        /* ASSERT */
        assertEquals(artifacts, result);
    }
}
