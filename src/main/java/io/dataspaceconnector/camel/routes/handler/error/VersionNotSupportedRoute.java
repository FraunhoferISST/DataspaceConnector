package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class VersionNotSupportedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleVersionNotSupportedException")
                .routeId("versionNotSupported")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling VersionNotSupportedException called.")
                .to("bean:messageResponseService?method=handleInfoModelNotSupportedException"
                        + "(${exception}, "
                        + "${body.getHeader().getModelVersion()})");
    }

}
