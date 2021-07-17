package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ResourceNotFoundRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleResourceNotFoundException")
                .routeId("resourceNotFound")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling ResourceNotFoundException called.")
                .to("bean:messageResponseService?method=handleResourceNotFoundException("
                        + "${exception}, "
                        + "${body.getHeader().getRequestedElement()}, "
                        + "${body.getHeader().getIssuerConnector()}, "
                        + "${body.getHeader().getId()})");
    }

}
