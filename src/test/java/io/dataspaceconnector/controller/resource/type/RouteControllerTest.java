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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.service.resource.type.RouteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class RouteControllerTest {

    @MockBean
    private RouteService routeService;

    @Autowired
    private RouteController controller;

    @Test
    public void createStartEndpoint_validInput_setStartEndpoint() {
        /* ARRANGE */
        final var routeId = UUID.randomUUID();
        final var endpointId = UUID.randomUUID();
        final var endpointUri = URI.create("https://" + endpointId);

        /* ACT */
        final var result = controller.createStartEndpoint(routeId, endpointUri);

        /* ASSERT */
        Mockito.verify(routeService, Mockito.atLeastOnce()).setStartEndpoint(eq(routeId), eq(endpointId));
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void deleteStartEndpoint_validInput_removeStartEndpoint() {
        /* ARRANGE */
        final var routeId = UUID.randomUUID();

        /* ACT */
        final var result = controller.deleteStartEndpoint(routeId);

        /* ASSERT */
        Mockito.verify(routeService, Mockito.atLeastOnce()).removeStartEndpoint(eq(routeId));
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void createLastEndpoint_validInput_setStartEndpoint() {
        /* ARRANGE */
        final var routeId = UUID.randomUUID();
        final var endpointId = UUID.randomUUID();
        final var endpointUri = URI.create("https://" + endpointId);

        /* ACT */
        final var result = controller.createLastEndpoint(routeId, endpointUri);

        /* ASSERT */
        Mockito.verify(routeService, Mockito.atLeastOnce()).setLastEndpoint(eq(routeId), eq(endpointId));
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void deleteLastEndpoint_validInput_removeStartEndpoint() {
        /* ARRANGE */
        final var routeId = UUID.randomUUID();

        /* ACT */
        final var result = controller.deleteLastEndpoint(routeId);

        /* ASSERT */
        Mockito.verify(routeService, Mockito.atLeastOnce()).removeLastEndpoint(eq(routeId));
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
