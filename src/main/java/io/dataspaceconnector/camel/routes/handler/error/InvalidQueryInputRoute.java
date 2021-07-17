package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InvalidQueryInputRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleInvalidQueryInputException")
                .routeId("invalidQueryInput")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling invalid query input called.")
                .to("bean:messageResponseService?method=handleInvalidQueryInput("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getTransferContract()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
