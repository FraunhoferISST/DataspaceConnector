package io.dataspaceconnector.camel.routes.controller;

import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ConnectorUnavailableControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(ConfigUpdateException.class)
                .to("direct:handleConfigUpdateException");

        from("direct:connectorUnavailableSender")
                .routeId("connectorUnavailableSender")
                .process("ConfigurationUpdater")
                .process("ConnectorUpdateMessageBuilder")
                .process("RequestWithConnectorPayloadPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter");
    }

}
