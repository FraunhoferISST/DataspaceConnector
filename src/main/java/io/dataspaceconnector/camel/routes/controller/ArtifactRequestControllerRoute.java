package io.dataspaceconnector.camel.routes.controller;

import io.dataspaceconnector.camel.exception.InvalidResponseException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ArtifactRequestControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(InvalidResponseException.class)
                .process("PolicyRestrictionProcessor");

        from("direct:artifactRequestSender")
                .routeId("artifactRequestSender")
                .process("ArtifactRequestMessageBuilder")
                .process("RequestWithoutPayloadPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter")
                .process("ArtifactResponseValidator");
    }

}
