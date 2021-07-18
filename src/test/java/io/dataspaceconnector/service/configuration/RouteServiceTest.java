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
package io.dataspaceconnector.service.configuration;

import java.util.UUID;

import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.configuration.util.RouteHelper;
import io.dataspaceconnector.service.resource.EndpointServiceProxy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Autowired
    private RouteService service;

    @Test
    public void persist_validInput_saveRoutes() {
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

}
