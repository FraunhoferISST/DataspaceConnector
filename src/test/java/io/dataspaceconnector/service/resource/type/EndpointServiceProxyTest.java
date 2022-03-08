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

import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpointDesc;
import io.dataspaceconnector.repository.EndpointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = EndpointServiceProxy.class)
class EndpointServiceProxyTest {

    @Autowired
    EndpointServiceProxy serviceProxy;

    @MockBean
    GenericEndpointService generic;

    @MockBean
    private EndpointRepository repository;

    @MockBean
    private AppEndpointService appEndpointService;

    @Test
    public void create_genericEndpoint_returnGenericEndpoint() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();

        Mockito.doReturn(new GenericEndpoint()).when(generic).create(desc);

        /* ACT */
        final var result = serviceProxy.create(desc);

        /* ASSERT */
        assertNotNull(result);
        assertTrue(result instanceof GenericEndpoint);
    }

    @Test
    public void update_genericEndpoint_returnUpdatedGenericEndpoint() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();
        final var desc = new GenericEndpointDesc();

        Mockito.doReturn(new GenericEndpoint()).when(generic).update(uuid, desc);

        /* ACT */
        final var result = serviceProxy.update(uuid, desc);

        /* ASSERT */
        assertNotNull(result);
        assertTrue(result instanceof GenericEndpoint);
    }

    @Test
    public void get_genericEndpoint_returnGenericEndpoint() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();

        Mockito.doReturn(new GenericEndpoint()).when(generic).get(uuid);

        /* ACT */
        final var result = serviceProxy.get(uuid);

        /* ASSERT */
        assertNotNull(result);
        assertTrue(result instanceof GenericEndpoint);
    }

    @Test
    public void getAll_validInput_returnEndpoints() {
        /* ARRANGE */
        Mockito.doReturn(new PageImpl<Endpoint>(List.of())).when(repository).findAll(Pageable.unpaged());

        /* ACT */
        final var result = serviceProxy.getAll(Pageable.unpaged());

        /* ASSERT */
        assertNotNull(result);
    }

    @Test
    public void doesExist_validGenericEndpointId_returnTrue() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();
        Mockito.doReturn(true).when(generic).doesExist(uuid);

        /* ACT */
        final var result = serviceProxy.doesExist(uuid);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void doesExist_invalidGenericEndpointId_returnFalse() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();
        Mockito.doReturn(false).when(generic).doesExist(uuid);

        /* ACT */
        final var result = serviceProxy.doesExist(uuid);

        /* ASSERT */
        assertFalse(result);
    }
}
