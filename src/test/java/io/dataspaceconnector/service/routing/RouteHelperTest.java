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
package io.dataspaceconnector.service.routing;

import java.net.URI;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.ids.builder.IdsAppRouteBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RouteHelper.class})
class RouteHelperTest {

    @MockBean
    private RouteManager routeManager;

    @MockBean
    private IdsAppRouteBuilder routeBuilder;

    @Autowired
    private RouteHelper routeHelper;

    @Test
    void deploy_deployMethodNotCamel_tryToDeleteRoute() {
        /* ARRANGE */
        final var route = new Route();
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.NONE);

        doNothing().when(routeManager).deleteRoute(any());

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, never()).createAndDeployXMLRoute(any());
        verify(routeManager, times(1)).deleteRoute(route);
    }

    @Test
    void deploy_startAndEndNull_tryToDeleteRoute() {
        /* ARRANGE */
        final var route = new Route();
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);

        doNothing().when(routeManager).deleteRoute(any());

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, never()).createAndDeployXMLRoute(any());
        verify(routeManager, times(1)).deleteRoute(route);
    }

    @Test
    void deploy_validRouteWithStartSet_deployRoute() {
        /* ARRANGE */
        final var route = new Route();
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        ReflectionTestUtils.setField(route, "start", new GenericEndpoint());
        ReflectionTestUtils.setField(route, "output", new ArtifactImpl());

        final var idsRoute = getIdsRoute();
        when(routeBuilder.create(route)).thenReturn(idsRoute);
        doNothing().when(routeManager).createAndDeployXMLRoute(any());

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, times(1)).createAndDeployXMLRoute(idsRoute);
        verify(routeManager, never()).deleteRoute(route);
    }

    @Test
    void deploy_validRouteWithEndSet_deployRoute() {
        /* ARRANGE */
        final var route = new Route();
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        ReflectionTestUtils.setField(route, "end", new GenericEndpoint());

        final var idsRoute = getIdsRoute();
        when(routeBuilder.create(route)).thenReturn(idsRoute);
        doNothing().when(routeManager).createAndDeployXMLRoute(any());

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, times(1)).createAndDeployXMLRoute(idsRoute);
        verify(routeManager, never()).deleteRoute(route);
    }

    private AppRoute getIdsRoute() {
        return new AppRouteBuilder()
                ._appRouteStart_(new GenericEndpointBuilder()
                        ._path_("https://some-backend")
                        ._accessURL_(URI.create("https://some-url"))
                        .build())
                ._appRouteEnd_(new ConnectorEndpointBuilder()
                        ._path_("https://some-backend")
                        ._accessURL_(URI.create("https://some-url"))
                        .build())
                ._routeDeployMethod_("CAMEL")
                .build();
    }

}
