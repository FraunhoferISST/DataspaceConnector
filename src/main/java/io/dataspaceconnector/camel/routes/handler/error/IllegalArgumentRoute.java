package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class IllegalArgumentRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleIllegalArgumentException")
                .routeId("illegalArgument")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling IllegalArgumentException called.")
                .to("bean:messageResponseService?method=handleIllegalArgumentException("
                        + "${exception}, "
                        + "${body.getBody().toString()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
