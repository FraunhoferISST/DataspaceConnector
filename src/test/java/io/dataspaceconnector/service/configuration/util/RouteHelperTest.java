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
package io.dataspaceconnector.service.configuration.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.util.Util;
import io.configmanager.extensions.routes.camel.RouteManager;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.ids.builder.IdsAppRouteBuilder;
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
public class RouteHelperTest {

    @MockBean
    private RouteManager routeManager;

    @MockBean
    private IdsAppRouteBuilder appRouteBuilder;

    @Autowired
    private RouteHelper routeHelper;

    @Test
    public void deploy_startUndefined_doNothing() {
        /* ARRANGE */
        final var route = getRoute(null, getConnectorEndpoint());

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, never()).createAndDeployXMLRoute(any());
    }

    @Test
    public void deploy_endUndefined_doNothing() {
        /* ARRANGE */
        final var route = getRoute(getConnectorEndpoint(), null);

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, never()).createAndDeployXMLRoute(any());
    }

    @Test
    public void deploy_validRoute_callRouteManagerDeploy() {
        /* ARRANGE */
        final var endpoint = getConnectorEndpoint();
        final var route = getRoute(endpoint, endpoint);
        final var appRoute = getAppRoute();

        when(appRouteBuilder.create(route)).thenReturn(appRoute);
        doNothing().when(routeManager).createAndDeployXMLRoute(appRoute);

        /* ACT */
        routeHelper.deploy(route);

        /* ASSERT */
        verify(routeManager, times(1)).createAndDeployXMLRoute(appRoute);
    }

    @Test
    public void delete_validRoute_callRouteManagerDelete() {
        /* ARRANGE */
        final var endpoint = getConnectorEndpoint();
        final var route = getRoute(endpoint, endpoint);

        doNothing().when(routeManager).deleteRoute(route);

        /* ACT */
        routeHelper.delete(route);

        /* ASSERT */
        verify(routeManager, times(1)).deleteRoute(route);
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private AppRoute getAppRoute() {
        return new AppRouteBuilder()
                ._appRouteStart_(Util.asList(
                        new GenericEndpointBuilder()
                                ._accessURL_(URI.create("http://http-demo-backend:8090/demo"))
                                .build()))
                ._appRouteEnd_(Util.asList(
                        new GenericEndpointBuilder()
                                ._accessURL_(URI.create("http://http-demo-backend:8090/demo"))
                                .build()))
                ._hasSubRoute_(new ArrayList<>())
                ._routeDeployMethod_("DEPLOY")
                .build();
    }

    private Route getRoute(final Endpoint start, final Endpoint end) {
        final var route = new Route();
        ReflectionTestUtils.setField(route, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        ReflectionTestUtils.setField(route, "start", start);
        ReflectionTestUtils.setField(route, "end", end);

        return route;
    }

    private ConnectorEndpoint getConnectorEndpoint() {
        final var endpoint = new ConnectorEndpoint();
        ReflectionTestUtils.setField(endpoint, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(endpoint, "location", URI.create("https://location.com"));

        return endpoint;
    }

}
