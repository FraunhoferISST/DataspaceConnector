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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.service.resource.type.EndpointServiceProxy;
import io.dataspaceconnector.service.resource.type.GenericEndpointService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class EndpointControllerTest {

    @MockBean
    private GenericEndpointService genericEndpointService;

    @MockBean
    private EndpointServiceProxy service;

    @Autowired
    private EndpointController controller;

    @Test
    public void create_validInput_returnNewEndpoint() {
        /* ARRANGE */
        final var desc = new EndpointDesc();
        final var endpoint = new GenericEndpoint();

        Mockito.when(service.create(eq(desc))).thenReturn(endpoint);

        /* ACT */
        final var result = controller.create(desc);

        /* ASSERT */
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Mockito.verify(service, Mockito.atLeastOnce()).create(eq(desc));
    }

    @Test
    public void getAll_validInput_returnNoEndpointsWhenNoExist() {
        /* ARRANGE */
        final var page = 0;
        final var size = 30;

        final var endpoints = Utils.toPage(new ArrayList<Endpoint>(), Utils.toPageRequest(page, size));
        Mockito.when(service.getAll(eq(Utils.toPageRequest(page, size)))).thenReturn(endpoints);

        /* ACT */
        final var result = controller.getAll(page, size);

        /* ASSERT */
        assertEquals(1, result.getContent().size());
        Mockito.verify(service, Mockito.atLeastOnce()).getAll(eq(Utils.toPageRequest(page, size)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getAll_validInput_returnOneEndpointsWhenOneExist() {
        /* ARRANGE */
        final var page = 0;
        final var size = 30;

        final var endpoints = (Page<Endpoint>) (Page<?>) Utils.toPage(List.of(new GenericEndpoint()), Utils.toPageRequest(page, size));
        Mockito.when(service.getAll(eq(Utils.toPageRequest(page, size)))).thenReturn(endpoints);

        /* ACT */
        final var result = controller.getAll(page, size);

        /* ASSERT */
        assertEquals(1, result.getContent().size());
        Mockito.verify(service, Mockito.atLeastOnce()).getAll(eq(Utils.toPageRequest(page, size)));
    }

    @Test
    public void get_validInput_returnEndpoint() {
        /* ARRANGE */
        final var resourceId = UUID.randomUUID();
        final var endpoint = new GenericEndpoint();

        Mockito.when(service.get(eq(resourceId))).thenReturn(endpoint);

        /* ACT */
        controller.get(resourceId);

        /* ASSERT */
        Mockito.verify(service, Mockito.atLeastOnce()).get(eq(resourceId));
    }

    @Test
    public void delete_validInput_returnNewEndpoint() {
        /* ARRANGE */
        final var resourceId = UUID.randomUUID();

        /* ACT */
        final var result = controller.delete(resourceId);

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Mockito.verify(service, Mockito.atLeastOnce()).delete(eq(resourceId));
    }
}
