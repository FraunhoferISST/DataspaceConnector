package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UnconfirmedAgreementRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleUnconfirmedAgreementException")
                .routeId("unconfirmedAgreement")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling unconfirmed agreement called.")
                .to("bean:messageResponseService?method=handleUnconfirmedAgreement("
                        + "${exception.getAgreement()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
