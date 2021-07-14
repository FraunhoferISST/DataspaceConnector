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

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import org.apache.velocity.VelocityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RouteConfigurer class.
 */
@SpringBootTest(classes = {RouteConfigurer.class})
class RouteConfigurerTest {

    @Autowired
    RouteConfigurer routeConfigurer;

    @Test
    void testConstructorAndSetter(){
        RouteConfigurer configurer = new RouteConfigurer();
        assertNotNull(configurer);
        configurer.setDataSpaceConnectorApiUsername("test");
        configurer.setDataSpaceConnectorApiPassword("test");
    }

    @Test
    void testAddBasicAuth(){
        VelocityContext velocityContext = new VelocityContext();
        assertDoesNotThrow(() -> routeConfigurer.addBasicAuthToContext(velocityContext));
    }

    @Test
    void testGetRouteTemplate(){
        final var appRouteGenericEnpoint = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new GenericEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                .build();
        assertNotNull(routeConfigurer.getRouteTemplate(appRouteGenericEnpoint));
        final var appRouteConnectorEndpoint = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new ConnectorEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                .build();
        assertNotNull(routeConfigurer.getRouteTemplate(appRouteConnectorEndpoint));
        final var appRouteEndpoint = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new EndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                .build();
        assertNull(routeConfigurer.getRouteTemplate(appRouteEndpoint));
    }
}
