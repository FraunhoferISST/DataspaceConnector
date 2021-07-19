package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.Optional;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorUpdateMessageImpl;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Builds a ConnectorUpdateMessage and creates a request DTO with header and payload.
 */
@Component("ConnectorUpdateMessageBuilder")
@RequiredArgsConstructor
public class ConnectorUpdateMessageBuilder
        extends IdsMessageBuilder<ConnectorUpdateMessageImpl, Connector> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ConnectorUpdateMessage according to the exchange properties as well as the connector
     * object and creates a Request with the message as header and the connector as payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ConnectorUpdateMessageImpl, Connector, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ConnectorUpdateMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedConnector_(connectorId)
                .build();

        return new Request<>((ConnectorUpdateMessageImpl) message, connector, Optional.empty());
    }

}
