package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContractListEmptyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleContractListEmptyException")
                .routeId("contractListEmpty")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling missing contract offers called.")
                .to("bean:messageResponseService?method=handleMissingContractOffers("
                        + "${exception.getContractRequest()}, "
                        + "${body.getHeader().getId()}, "
                        + "${body.getHeader().getIssuerConnector()})");
    }

}
