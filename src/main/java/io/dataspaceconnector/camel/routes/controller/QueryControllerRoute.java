package io.dataspaceconnector.camel.routes.controller;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QueryControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:querySender")
                .routeId("querySender")
                .process("QueryMessageBuilder")
                .process("QueryPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter");
    }

}
