package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class IdsMessageFailedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleIdsMessageFailed")
                .routeId("idsMessageFailed")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling failed IDS message called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondIdsMessageFailed(${exception})");
    }

}
