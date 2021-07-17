package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MalformedRulesRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMalformedRules")
                .routeId("malformedRules")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling malformed rules called.")
                .to("bean:messageResponseService?method=handleMalformedRules("
                        + "${exception.cause}, "
                        + "${body.getBody().getContractRequest().toRdf()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
