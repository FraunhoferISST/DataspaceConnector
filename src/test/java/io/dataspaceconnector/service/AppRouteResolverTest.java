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

import java.util.Collections;
import java.util.UUID;

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.endpoint.AppEndpointImpl;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.RouteRepository;
import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AppRouteResolver.class})
class AppRouteResolverTest {

    @Mock
    private org.apache.camel.Route camelRoute;

    @MockBean
    private RouteRepository routeRepository;

    @MockBean
    private CamelContext camelContext;

    @Autowired
    private AppRouteResolver resolver;

    @Test
    void isAppUsed_appInUse_returnOptionalOfRoute() {
        /* ARRANGE */
        final var app = getApp();
        final var route = getRoute();

        when(routeRepository.findTopLevelRoutesByEndpoint(app.getEndpoints().get(0).getId()))
                .thenReturn(Collections.singletonList(route));
        when(camelContext.getRoute(route.getId().toString())).thenReturn(camelRoute);

        /* ACT */
        final var result = resolver.isAppUsed(app);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(route.getId().toString(), result.get());
    }

    @Test
    void isAppUsed_camelRouteNotDeployed_returnEmptyOptional() {
        /* ARRANGE */
        final var app = getApp();
        final var route = getRoute();

        when(routeRepository.findTopLevelRoutesByEndpoint(app.getEndpoints().get(0).getId()))
                .thenReturn(Collections.singletonList(route));
        when(camelContext.getRoute(route.getId().toString())).thenReturn(null);

        /* ACT */
        final var result = resolver.isAppUsed(app);

        /* ASSERT */
        assertTrue(result.isEmpty());
    }

    @Test
    void isAppUsed_appNotInRoutes_returnEmptyOptional() {
        /* ARRANGE */
        final var app = getApp();

        when(routeRepository.findTopLevelRoutesByEndpoint(app.getEndpoints().get(0).getId()))
                .thenReturn(Collections.emptyList());

        /* ACT */
        final var result = resolver.isAppUsed(app);

        /* ASSERT */
        assertTrue(result.isEmpty());
    }

    private App getApp() {
        final var endpoint = new AppEndpointImpl();
        ReflectionTestUtils.setField(endpoint, "id", UUID.randomUUID());

        final var app = new App();
        ReflectionTestUtils.setField(app, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(app, "endpoints", Collections.singletonList(endpoint));

        return app;
    }

    private Route getRoute() {
        final var route = new Route();
        ReflectionTestUtils.setField(route, "id", UUID.randomUUID());
        return route;
    }
}
