package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class NoTransferContractRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleNoTransferContractException")
                .routeId("noTransferContract")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling missing transfer contract called.")
                .to("bean:messageResponseService?method=handleMissingTransferContract("
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getTransferContract()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
