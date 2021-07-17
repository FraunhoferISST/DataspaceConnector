package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SelfLinkCreationExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleSelfLinkCreationException")
                .routeId("selfLinkCreationException")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling SelfLinkCreationException called.")
                .to("bean:messageResponseService?method=handleSelfLinkCreationException("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedArtifact()})");
    }

}
