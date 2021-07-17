package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ConfigUpdateExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleConfigUpdateException")
                .routeId("configUpdateException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling ConfigUpdateException called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondConfigurationUpdateError(${exception})");
    }

}
