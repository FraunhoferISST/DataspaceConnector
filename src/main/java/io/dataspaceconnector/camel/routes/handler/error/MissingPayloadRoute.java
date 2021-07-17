package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MissingPayloadRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMissingPayloadException")
                .routeId("missingPayloadException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling missing payload called.")
                .to("bean:messageResponseService?method=handleMissingPayload("
                        + "${body.getHeader().getAffectedResource()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
