package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageResponseExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMessageResponseException")
                .routeId("messageResponseException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling MessageResponseException called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondReceivedInvalidResponse(${exception})");
    }

}
