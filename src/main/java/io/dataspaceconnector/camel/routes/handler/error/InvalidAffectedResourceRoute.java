package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InvalidAffectedResourceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleInvalidAffectedResourceException")
                .routeId("invalidAffectedResource")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling invalid affected resource called.")
                .to("bean:messageResponseService?method=handleInvalidAffectedResource("
                        + "${body.getBody().getId()}, "
                        + "${body.getHeader().getAffectedResource()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
