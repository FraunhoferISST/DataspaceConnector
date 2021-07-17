package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContractExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleContractException")
                .routeId("contractException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling ContractException called.")
                .to("bean:messageResponseService?method=handleContractException("
                        + "${exception}, "
                        + "${body.getBody().toRdf()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
