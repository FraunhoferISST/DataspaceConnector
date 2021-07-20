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

import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryMessageImpl;
import de.fraunhofer.iais.eis.QueryScope;
import de.fraunhofer.iais.eis.QueryTarget;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.broker.util.FullTextQueryTemplate;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
 * Builds a QueryMessage and creates a request DTO with header and payload.
 */
@Component("QueryMessageBuilder")
@RequiredArgsConstructor
public class QueryMessageBuilder extends IdsMessageBuilder<QueryMessageImpl, String> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a QueryMessage according to the exchange properties and creates a Request with the
     * message as header and a query from the exchange properties as payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @SuppressFBWarnings(value = "FORMAT_STRING_MANIPULATION")
    @Override
    protected Request<QueryMessageImpl, String, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.QueryMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._queryLanguage_(QueryLanguage.SPARQL)
                ._queryScope_(QueryScope.ALL)
                ._recipientScope_(QueryTarget.BROKER)
                .build();

        String payload;
        if (exchange.getProperty(ParameterUtils.QUERY_PARAM) != null) {
            payload = (String) exchange.getProperty(ParameterUtils.QUERY_PARAM);
        } else {
            final var searchTerm = exchange
                    .getProperty(ParameterUtils.QUERY_TERM_PARAM, String.class);
            final var limit = exchange
                    .getProperty(ParameterUtils.QUERY_LIMIT_PARAM, Integer.class);
            final var offset = exchange
                    .getProperty(ParameterUtils.QUERY_OFFSET_PARAM, Integer.class);

            payload = String.format(FullTextQueryTemplate.FULL_TEXT_QUERY, searchTerm,
                                    limit, offset);
        }

        return new Request<>((QueryMessageImpl) message, payload, Optional.empty());
    }

}
