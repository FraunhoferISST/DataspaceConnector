package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ResourceNotFoundForControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleResourceNotFoundForController")
                .routeId("resourceNotFoundForController")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling resource not found called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondResourceNotFound(${exchangeProperty.resourceId})");
    }

}
