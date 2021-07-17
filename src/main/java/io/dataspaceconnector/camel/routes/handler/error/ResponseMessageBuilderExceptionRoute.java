package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ResponseMessageBuilderExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleResponseMessageBuilderException")
                .routeId("responseMessageBuilderException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling ResponseMessageBuilderException called.")
                .to("bean:messageResponseService?method=handleResponseMessageBuilderException("
                        + "${exception}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
