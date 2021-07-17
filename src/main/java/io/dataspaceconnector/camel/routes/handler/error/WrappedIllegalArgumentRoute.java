package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class WrappedIllegalArgumentRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleWrappedIllegalArgumentException")
                .routeId("wrappedIllegalArgument")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling IllegalArgumentException called.")
                .process("PayloadStreamReader")
                .to("bean:messageResponseService?method=handleIllegalArgumentException("
                        + "${exception.cause}, "
                        + "${body.getBody()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
