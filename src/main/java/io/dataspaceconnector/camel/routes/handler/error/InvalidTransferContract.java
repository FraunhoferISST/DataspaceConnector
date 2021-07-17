package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InvalidTransferContract extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleInvalidTransferContract")
                .routeId("invalidTransferContract")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling invalid transfer contract called.")
                .to("bean:messageResponseService?method=handleInvalidTransferContract("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getTransferContract()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
