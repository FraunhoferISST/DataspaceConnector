package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PersistenceExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handlePersistenceException")
                .routeId("persistenceException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling PersistenceException called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondFailedToStoreEntity(${exception})");
    }

}
