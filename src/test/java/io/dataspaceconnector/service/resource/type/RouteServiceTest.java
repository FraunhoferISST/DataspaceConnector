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
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.routing.RouteHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { RouteService.class, RouteRepository.class, EndpointRepository.class,
        EndpointServiceProxy.class, RouteFactory.class })
class RouteServiceTest {

    @MockBean
    private EndpointRepository endpointRepository;

    @MockBean
    private RouteRepository repository;

    @MockBean
    private EndpointServiceProxy endpointService;

    @MockBean
    private RouteHelper routeHelper;

    @MockBean
    private RouteFactory factory;

    @MockBean
    private ArtifactRepository artifactRepository;

    @SpyBean
    private RouteService service;

    @Test
    void persist_validInput_saveRoutes() {
        /* ARRANGE */
        final var start = new Endpoint();
        ReflectionTestUtils.setField(start, "id", UUID.randomUUID());
        final var end = new Endpoint();
        ReflectionTestUtils.setField(end, "id", UUID.randomUUID());

        final var route = new Route();
        ReflectionTestUtils.setField(route, "start", start);
        ReflectionTestUtils.setField(route, "end", end);

        /* ACT */
        service.persist(route);

        /* ASSERT */
        Mockito.verify(endpointRepository, Mockito.times(2)).save(eq(start));
        Mockito.verify(endpointRepository, Mockito.times(2)).save(eq(end));
    }

    @Test
    void delete_validInput_deleteRoute() {
        /* ARRANGE */
        final var id = UUID.randomUUID();
        Mockito.doReturn(new Route()).when(service).get(Mockito.eq(id));
        Mockito.doNothing().when(routeHelper).delete(Mockito.any());
        Mockito.doReturn(new Route()).when(factory).deleteSubroutes(new Route());

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> service.delete(id));
    }

    @Test
    void setStartEndpoint_validInput_setStartEndpoint() {
        /* ARRANGE */
        final var endpointId = UUID.randomUUID();
        final var routeId = UUID.randomUUID();
        final var endpoint = new GenericEndpoint();
        final var route = new Route();

        Mockito.doReturn(endpoint).when(endpointService).get(Mockito.eq(endpointId));
        Mockito.doReturn(route).when(service).get(routeId);
        Mockito.doReturn(new Route()).when(factory).setStartEndpoint(route, endpoint);

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> service.setStartEndpoint(routeId, endpointId));
    }

    @Test
    void removeStartEndpoint_validInput_removeStartEndpoint() {
        /* ARRANGE */
        final var routeId = UUID.randomUUID();
        final var route = new Route();

        Mockito.doReturn(route).when(service).get(routeId);
        Mockito.doReturn(new Route()).when(factory).deleteStartEndpoint(route);

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> service.removeStartEndpoint(routeId));
    }

    @Test
    void setLastEndpoint_validInput_setLastEndpoint() {
        /* ARRANGE */
        final var endpointId = UUID.randomUUID();
        final var routeId = UUID.randomUUID();
        final var endpoint = new GenericEndpoint();
        final var route = new Route();

        Mockito.doReturn(endpoint).when(endpointService).get(Mockito.eq(endpointId));
        Mockito.doReturn(route).when(service).get(routeId);
        Mockito.doReturn(new Route()).when(factory).setLastEndpoint(route, endpoint);

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> service.setLastEndpoint(routeId, endpointId));
    }

    @Test
    void removeLastEndpoint_validInput_removeLastEndpoint() {
        /* ARRANGE */
        final var routeId = UUID.randomUUID();
        final var route = new Route();

        Mockito.doReturn(route).when(service).get(routeId);
        Mockito.doReturn(new Route()).when(factory).deleteLastEndpoint(route);

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> service.removeLastEndpoint(routeId));
    }
}
