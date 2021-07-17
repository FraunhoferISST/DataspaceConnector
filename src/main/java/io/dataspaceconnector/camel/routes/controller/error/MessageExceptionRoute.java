package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMessageException")
                .routeId("messageException")
                .log(LoggingLevel.DEBUG, "Error route for handling MessageException called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?method=respondIdsMessageFailed(${exception})");
    }

}
