package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class NoAffectedResourceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleNoAffectedResourceException")
                .routeId("noAffectedResource")
                .log(LoggingLevel.DEBUG, "Error route for handling missing affected resource called.")
                .to("bean:messageResponseService?method=handleMissingAffectedResource("
                        + "${body.getHeader().getAffectedResource()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
