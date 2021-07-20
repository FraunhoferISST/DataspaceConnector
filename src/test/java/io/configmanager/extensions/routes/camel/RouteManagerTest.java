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
package io.configmanager.extensions.routes.camel;

import java.net.URI;
import java.util.UUID;

import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.util.Util;
import io.configmanager.extensions.routes.camel.exceptions.RouteCreationException;
import io.dataspaceconnector.camel.config.CamelConfig;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {RouteManager.class, RouteConfigurer.class, CamelConfig.class, DefaultCamelContext.class})
public class RouteManagerTest {
    @Autowired
    private RouteManager routeManager;

    @Autowired
    private DefaultCamelContext camelContext;

    @Test
    void testCreateAndDeployXMLRoute() throws RouteCreationException {
        final var authenticationBuilder = new BasicAuthenticationBuilder();
        final var authentication = authenticationBuilder._authPassword_("test")._authUsername_("test").build();
        final var uuid = UUID.randomUUID();

        final var appRoute = new AppRouteBuilder(URI.create("http://approute/" + uuid))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new GenericEndpointBuilder()
                        ._genericEndpointAuthentication_(authentication)
                        ._accessURL_(URI.create("http://test")).build()))
                ._appRouteOutput_(Util.asList())
                ._appRouteEnd_(Util.asList(new GenericEndpointBuilder()
                        ._genericEndpointAuthentication_(authentication)
                        ._accessURL_(URI.create("http://test")).build()))
                .build();
        routeManager.createAndDeployXMLRoute(appRoute);
        assertTrue(camelContext.getRouteDefinitions().get(0).toString().startsWith("Route(app-route_" + uuid + ")"));
    }
}
