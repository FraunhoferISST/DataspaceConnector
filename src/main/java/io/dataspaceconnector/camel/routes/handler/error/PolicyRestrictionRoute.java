package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PolicyRestrictionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handlePolicyRestrictionException")
                .routeId("policyRestriction")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling PolicyRestrictionException called.")
                .to("bean:messageResponseService?method=handlePolicyRestrictionException("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedArtifact()}, "
                        + "${body.getHeader().getTransferContract()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
