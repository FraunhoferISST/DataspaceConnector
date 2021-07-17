package io.dataspaceconnector.camel.routes.controller.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FailedToBuildContractRequestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleFailedToBuildContractRequest")
                .routeId("failedToBuildContractRequest")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling failed contract building called.")
                .to("bean:io.dataspaceconnector.util.ControllerUtils?"
                        + "method=respondFailedToBuildContractRequest(${exception})");
    }

}
