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

import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ContractService.class)
public class ContractServiceTest {

    @Autowired
    private ContractService service;

    @MockBean
    private ContractFactory factory;

    @MockBean
    private ContractRepository repository;

    @Test
    public void getAllByArtifactId_validUuid_returnContracts() {
        /* ARRANGE */
        final var contract = new Contract();
        final var uuid = UUID.randomUUID();
        Mockito.doReturn(List.of(contract)).when(repository).findAllByArtifactId(Mockito.eq(uuid));

        /* ACT */
        final var result = service.getAllByArtifactId(uuid);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(contract, result.get(0));
    }
}
