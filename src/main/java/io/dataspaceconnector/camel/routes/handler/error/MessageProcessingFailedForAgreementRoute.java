package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessingFailedForAgreementRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMessageProcessingFailedForAgreement")
                .routeId("messageProcessingFailedForAgreement")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling failed message processing called.")
                .to("bean:messageResponseService?method=handleMessageProcessingFailed("
                        + "${exception}, "
                        + "${body.getBody().toRdf(), "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
