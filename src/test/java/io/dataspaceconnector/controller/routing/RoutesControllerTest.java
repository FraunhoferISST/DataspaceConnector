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
package io.dataspaceconnector.controller.routing;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RoutesController.class})
public class RoutesControllerTest {

    @MockBean
    private DefaultCamelContext camelContext;

    @MockBean
    private RouteController routeController;

    @MockBean
    private Unmarshaller unmarshaller;

    @Autowired
    private RoutesController routesController;

    @BeforeEach
    public void setup() {
        doReturn(camelContext).when(camelContext).adapt(ModelCamelContext.class);
        doReturn(routeController).when(camelContext).getRouteController();
    }

    @Test
    public void addRoutes_fileNull_returnStatusCode400() {
        /* ACT */
        final var response = routesController.addRoutes(null);

        /* ASSERT */
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addRoutes_validRouteFile_returnStatusCode200() throws Exception {
        /* ARRANGE */
        when(unmarshaller.unmarshal(any(InputStream.class))).thenReturn(new RoutesDefinition());
        doNothing().when(camelContext).addRouteDefinitions(any());

        final var file = new MockMultipartFile("file", "routes.xml",
                "application/xml",
                getRouteFileContent().getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var response = routesController.addRoutes(file);

        /* ASSERT */
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addRoutes_invalidRouteFile_returnStatusCode400() throws Exception {
        /* ARRANGE */
        when(unmarshaller.unmarshal(any(InputStream.class))).thenThrow(JAXBException.class);

        final var file = new MockMultipartFile("file", "routes.xml",
                "application/xml",
                getRouteFileContent().getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var response = routesController.addRoutes(file);

        /* ASSERT */
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addRoutes_errorAddingRoutesToContext_returnStatusCode500() throws Exception {
        /* ARRANGE */
        when(unmarshaller.unmarshal(any(InputStream.class))).thenReturn(new RoutesDefinition());
        doThrow(Exception.class).when(camelContext).addRouteDefinitions(any());

        final var file = new MockMultipartFile("file", "routes.xml",
                "application/xml",
                getRouteFileContent().getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var response = routesController.addRoutes(file);

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void removeRoute_validRouteId_returnStatusCode200() throws Exception {
        /* ARRANGE */
        doNothing().when(camelContext).stopRoute(any());
        when(camelContext.removeRoute(anyString())).thenReturn(true);

        /* ACT */
        final var response = routesController.removeRoute("validId");

        /* ASSERT */
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void removeRoute_invalidRouteId_returnStatusCode500() throws Exception {
        /* ARRANGE */
        doThrow(Exception.class).when(camelContext).stopRoute(any());

        /* ACT */
        final var response = routesController.removeRoute("invalidId");

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void removeRoute_routeCannotBeStopped_returnStatusCode500() throws Exception {
        /* ARRANGE */
        doNothing().when(camelContext).stopRoute(any());
        when(camelContext.removeRoute(anyString())).thenReturn(false);

        /* ACT */
        final var response = routesController.removeRoute("validId");

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    private String getRouteFileContent() {
        return "<routes xmlns=\"http://camel.apache.org/schema/spring\">\n"
                + "    <route id=\"route-id\">\n"
                + "        <from uri=\"timer:tick?period=3000\"/>\n"
                + "        <to uri=\"log:info\"/>\n"
                + "    </route>\n"
                + "</routes>";
    }

}
