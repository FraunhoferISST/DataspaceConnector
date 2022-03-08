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
package io.dataspaceconnector.service.routing.config;

import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.EndpointBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.config.camel.FreemarkerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the RouteConfigurer class.
 */
@SpringBootTest(classes = {RouteConfigurer.class, FreemarkerConfig.class})
class RouteConfigurerTest {

    @Autowired
    RouteConfigurer routeConfigurer;

    @Autowired
    FreemarkerConfig freemarkerConfig;

    @Test
    void testConstructorAndSetter(){
        RouteConfigurer configurer = new RouteConfigurer(freemarkerConfig.freemarkerConfiguration());
        assertNotNull(configurer);
        configurer.setDataSpaceConnectorApiUsername("test");
        configurer.setDataSpaceConnectorApiPassword("test");
    }

    @Test
    void testAddBasicAuth(){
        final var freemarkerInput = new HashMap<String, Object>();
        assertDoesNotThrow(() -> routeConfigurer.addBasicAuthToContext(freemarkerInput));
    }

    @Test
    void testGetRouteTemplate(){
        final var appRouteGenericEnpoint = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new GenericEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                ._appRouteEnd_(Util.asList(new ConnectorEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                .build();
        assertNotNull(routeConfigurer.getRouteTemplate(appRouteGenericEnpoint));
        final var appRouteConnectorEndpoint = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new ConnectorEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                ._appRouteEnd_(Util.asList(new GenericEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                .build();
        assertNotNull(routeConfigurer.getRouteTemplate(appRouteConnectorEndpoint));
        final var appRouteEndpoint = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new EndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                .build();
        assertNull(routeConfigurer.getRouteTemplate(appRouteEndpoint));
    }
}
