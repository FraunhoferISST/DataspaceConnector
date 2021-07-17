package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InvalidResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleInvalidResponseException")
                .routeId("invalidResponse")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling invalid response called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondWithMessageContent(${exception.getResponse()})");
    }

}
