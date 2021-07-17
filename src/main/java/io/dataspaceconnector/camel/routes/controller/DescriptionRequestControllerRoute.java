package io.dataspaceconnector.camel.routes.controller;

import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DescriptionRequestControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(MessageException.class)
                .to("direct:handleMessageException");
        onException(MessageResponseException.class)
                .to("direct:handleMessageResponseException");
        onException(InvalidResponseException.class)
                .to("direct:handleInvalidResponseException");

        from("direct:descriptionRequestSender")
                .routeId("descriptionRequestSender")
                .process("DescriptionRequestMessageBuilder")
                .process("RequestWithoutPayloadPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter")
                .process("DescriptionResponseValidator");
    }

}
