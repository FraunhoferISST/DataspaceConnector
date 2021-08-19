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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.routing.RouteHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ConnectorEndpointTest {

    @MockBean
    private RouteRepository routeRepo;

    @MockBean
    RouteHelper routeHelper;

    @SpyBean
    private ConnectorEndpointService service;

    @Test
    public void persist_validInput_returnConnectorEndpoint() {
        /* ARRANGE */
        final var route = new Route();
        final var endpointId = getEndpoint().getId();

        Mockito.doReturn(List.of(route)).when(routeRepo).findTopLevelRoutesByEndpoint(endpointId);
        Mockito.doNothing().when(routeHelper).deploy(route);
        Mockito.doReturn(getEndpoint()).when(service).persist(getEndpoint());

        /* ACT */
        final var result = service.persist(getEndpoint());

        /* ASSERT */
        assertNotNull(result);
        assertEquals(getEndpoint(), result);
    }

    @SneakyThrows
    private ConnectorEndpoint getEndpoint() {
        final var constructor = ConnectorEndpoint.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var endpoint = constructor.newInstance();
        ReflectionTestUtils.setField(endpoint, "id", UUID.randomUUID());

        return endpoint;
    }
}
