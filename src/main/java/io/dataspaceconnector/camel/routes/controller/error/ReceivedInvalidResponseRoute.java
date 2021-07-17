package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReceivedInvalidResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleReceivedInvalidResponse")
                .routeId("receivedInvalidResponse")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling received invalid response called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondReceivedInvalidResponse(${exception})");
    }

}
