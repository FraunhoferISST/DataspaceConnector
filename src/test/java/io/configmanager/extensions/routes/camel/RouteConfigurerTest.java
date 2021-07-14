package io.configmanager.extensions.routes.camel;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import org.apache.velocity.VelocityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RouteConfigurer class.
 */
@SpringBootTest(classes = {RouteConfigurer.class})
@AutoConfigureMockMvc
public class RouteConfigurerTest {

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
