/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.Optional;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorUnavailableMessageImpl;
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
 * Builds a ConnectorUnavailableMessage and creates a request DTO with header and payload.
 */
@Component("ConnectorUnavailableMessageBuilder")
@RequiredArgsConstructor
public class ConnectorUnavailableMessageBuilder
        extends IdsMessageBuilder<ConnectorUnavailableMessageImpl, Connector> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ConnectorUnavailableMessage according to the exchange properties as well as the
     * connector object and creates a Request with the message as header and the connector as
     * payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ConnectorUnavailableMessageImpl, Connector, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ConnectorUnavailableMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedConnector_(connectorId)
                .build();

        return new Request<>((ConnectorUnavailableMessageImpl) message, connector,
                             Optional.empty());
    }

}
