package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MissingTargetInRuleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMissingTargetInRuleException")
                .routeId("missingTargetInRule")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling missing target in rules called.")
                .to("bean:messageResponseService?method=handleMissingTargetInRules("
                        + "${exception.getContractRequest()}, "
                        + "${body.getHeader().getId()}, "
                        + "${body.getHeader().getIssuerConnector()})");
    }

}
