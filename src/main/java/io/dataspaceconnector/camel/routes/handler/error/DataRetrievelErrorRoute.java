package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DataRetrievelErrorRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleDataRetrievalError")
                .routeId("dataRetrievalError")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling failed data retrieval called.")
                .to("bean:messageResponseService?method=handleFailedToRetrieveData("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
