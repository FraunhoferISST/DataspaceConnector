package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessagePayloadExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMessagePayloadException")
                .routeId("messagePayloadException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling MessagePayloadException called.")
                .to("bean:messageResponseService?method=handleMessagePayloadException("
                        + "${exception}, "
                        + "${body.getHeader().getId()}, "
                        + "${body.getHeader().getIssuerConnector()})");
    }

}
