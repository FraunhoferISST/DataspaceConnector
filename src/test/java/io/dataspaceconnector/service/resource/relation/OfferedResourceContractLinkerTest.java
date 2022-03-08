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
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.ResourceService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {OfferedResourceContractLinker.class})
class OfferedResourceContractLinkerTest {
    @MockBean
    ResourceService<OfferedResource, OfferedResourceDesc> resourceService;

    @MockBean
    ContractService contractService;

    @Autowired
    @InjectMocks
    OfferedResourceContractLinker linker;

    OfferedResource resource = getResource();
    Contract contract = getContract();

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
    public void getInternal_Valid_returnResources() {
        /* ARRANGE */
        resource.getContracts().add(contract);

        /* ACT */
        final var contracts = linker.getInternal(resource);

        /* ASSERT */
        final var expected = List.of(contract);
        assertEquals(expected, contracts);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private OfferedResource getResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();
        ReflectionTestUtils.setField(resource, "title", "Hello");
        ReflectionTestUtils.setField(resource, "contracts", new ArrayList<Contract>());
        ReflectionTestUtils.setField(resource, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }

    @SneakyThrows
    private Contract getContract() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();
        ReflectionTestUtils.setField(resource, "title", "Contract");
        ReflectionTestUtils.setField(resource, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return contract;
    }
}
