package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InvalidInputRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleInvalidInputException")
                .routeId("invalidInput")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling invalid input called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondInvalidInput(${exception})");
    }
}
