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

import de.fraunhofer.iais.eis.AppEndpointBuilder;
import de.fraunhofer.iais.eis.AppEndpointType;
import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.exception.RouteCreationException;
import io.dataspaceconnector.config.camel.CamelConfig;
import io.dataspaceconnector.config.camel.FreemarkerConfig;
import io.dataspaceconnector.service.routing.config.RouteConfigurer;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {RouteManager.class, RouteConfigurer.class, CamelConfig.class,
        DefaultCamelContext.class, FreemarkerConfig.class})
public class RouteManagerTest {
    @Autowired
    private RouteManager routeManager;

    @Autowired
    private DefaultCamelContext camelContext;

    @Test
    void testCreateAndDeployXMLRoute_GenericEndpoint() throws RouteCreationException {
        final var authentication = new BasicAuthenticationBuilder()
                ._authPassword_("test")
                ._authUsername_("test")
                .build();
        authentication.setProperty("type", "basic");
        final var uuid = UUID.randomUUID();

        final var appRoute = new AppRouteBuilder(URI.create("http://approute/" + uuid))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new GenericEndpointBuilder()
                        ._genericEndpointAuthentication_(authentication)
                        ._accessURL_(URI.create("http://test"))
                        ._path_("http://test")
                        .build()))
                ._appRouteOutput_(Util.asList())
                ._appRouteEnd_(Util.asList(new ConnectorEndpointBuilder()
                        ._accessURL_(URI.create("http://test"))
                        ._path_("http://test")
                        .build()))
                .build();
        routeManager.createAndDeployXMLRoute(appRoute);
        assertTrue(camelContext.getRouteDefinitions().get(0).toString().startsWith("Route(" + uuid + ")"));
    }

    @Test
    void testCreateAndDeployXMLRoute_ConnectorEndpoint() throws RouteCreationException {
        final var uuid = UUID.randomUUID();

        final var appRoute = new AppRouteBuilder(URI.create("http://approute/" + uuid))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new ConnectorEndpointBuilder()
                        ._accessURL_(URI.create("http://test"))
                        ._path_("http://test")
                        .build()))
                ._appRouteOutput_(Util.asList())
                ._appRouteEnd_(Util.asList(new AppEndpointBuilder()
                        ._appEndpointType_(AppEndpointType.INPUT_ENDPOINT)
                        ._accessURL_(URI.create("http://test"))
                        ._path_("http://test")
                        .build()))
                .build();
        routeManager.createAndDeployXMLRoute(appRoute);
        assertTrue(camelContext.getRouteDefinitions().get(1).toString().startsWith("Route(" + uuid + ")"));
    }

    @Test
    void testCreateAndDeployXMLRoute_AppEndpoint() throws RouteCreationException {
        final var uuid = UUID.randomUUID();

        final var appRoute = new AppRouteBuilder(URI.create("http://approute/" + uuid))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new AppEndpointBuilder()
                        ._appEndpointType_(AppEndpointType.OUTPUT_ENDPOINT)
                        ._accessURL_(URI.create("http://test"))
                        ._path_("http://test")
                        .build()))
                ._appRouteOutput_(Util.asList())
                ._appRouteEnd_(Util.asList(new AppEndpointBuilder()
                        ._appEndpointType_(AppEndpointType.INPUT_ENDPOINT)
                        ._accessURL_(URI.create("http://test"))
                        ._path_("http://test")
                        .build()))
                .build();
        routeManager.createAndDeployXMLRoute(appRoute);
        assertTrue(camelContext.getRouteDefinitions().get(2).toString().startsWith("Route(" + uuid + ")"));
    }
}
