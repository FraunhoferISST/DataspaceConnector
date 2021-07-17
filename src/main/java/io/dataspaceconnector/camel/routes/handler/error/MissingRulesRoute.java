package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MissingRulesRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMissingRulesException")
                .routeId("missingRules")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling missing rules called.")
                .to("bean:messageResponseService?method=handleMissingRules("
                        + "${exception.getContractRequest()}, "
                        + "${body.getHeader().getId()}, "
                        + "${body.getHeader().getIssuerConnector()})");
    }

}
