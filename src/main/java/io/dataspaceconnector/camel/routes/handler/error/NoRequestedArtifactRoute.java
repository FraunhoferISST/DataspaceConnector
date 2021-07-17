package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class NoRequestedArtifactRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleNoRequestedArtifactException")
                .routeId("noRequestedArtifact")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling missing requested artifact called.")
                .to("bean:messageResponseService?method=handleMissingRequestedArtifact("
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getTransferContract()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
