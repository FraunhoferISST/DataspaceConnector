package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AgreementPersistenceExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleAgreementPersistenceException")
                .routeId("agreementPersistenceException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling AgreementPersistenceException called.")
                .to("bean:messageResponseService?method=handleAgreementPersistenceException("
                        + "${exception.getCause()}, "
                        + "null, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
