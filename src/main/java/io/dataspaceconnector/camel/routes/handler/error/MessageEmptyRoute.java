package io.dataspaceconnector.camel.routes.handler.error;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageEmptyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:handleMessageEmptyException")
                .routeId("messageEmpty")
                .log(LoggingLevel.DEBUG,
                        "Error route for handling MessageEmptyException called.")
                .to("bean:messageResponseService?method=handleMessageEmptyException(${exception})");
    }

}
