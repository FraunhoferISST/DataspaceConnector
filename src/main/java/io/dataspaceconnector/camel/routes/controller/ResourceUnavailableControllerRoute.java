package io.dataspaceconnector.camel.routes.controller;

import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ResourceUnavailableControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(ResourceNotFoundException.class)
                .to("direct:handleResourceNotFoundForController");

        from("direct:resourceUnavailableSender")
                .routeId("resourceUnavailableSender")
                .process("ResourceFinder")
                .process("ResourceUnavailableMessageBuilder")
                .process("RequestWithResourcePayloadPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter");
    }

}
