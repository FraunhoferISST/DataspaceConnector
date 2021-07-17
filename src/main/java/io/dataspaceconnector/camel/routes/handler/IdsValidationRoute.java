package io.dataspaceconnector.camel.routes.handler;

import io.dataspaceconnector.exception.MessageEmptyException;
import io.dataspaceconnector.exception.VersionNotSupportedException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class IdsValidationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(VersionNotSupportedException.class)
                .to("direct:handleVersionNotSupportedException");
        onException(MessageEmptyException.class)
                .to("direct:handleMessageEmptyException");

        from("direct:ids-validation")
                .routeId("idsValidation")
                .process("MessageHeaderValidator");
    }

}
