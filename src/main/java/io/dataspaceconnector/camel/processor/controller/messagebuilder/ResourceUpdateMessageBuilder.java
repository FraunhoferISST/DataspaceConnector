package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.Optional;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
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
 * Builds a ResourceUpdateMessage and creates a request DTO with header and payload.
 */
@Component("ResourceUpdateMessageBuilder")
@RequiredArgsConstructor
public class ResourceUpdateMessageBuilder
        extends IdsMessageBuilder<ResourceUpdateMessageImpl, Resource> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ResourceUpdateMessage according to the exchange properties and creates a Request
     * with the message as header and the resource from the exchange body.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ResourceUpdateMessageImpl, Resource, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var resource = exchange.getIn().getBody(Resource.class);

        final var connectorId = connectorService.getConnectorId();
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var resourceId = exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedResource_(resourceId)
                .build();

        return new Request<>((ResourceUpdateMessageImpl) message, resource, Optional.empty());
    }

}
