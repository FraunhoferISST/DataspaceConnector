/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.message.handler;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Processor that checks whether an exchange message's body is a valid response DTO or
 * {@link ErrorResponse}. If neither is the case, this processor sets a default error response as
 * the body. This processor's intended purpose is to guarantee that a valid response is returned
 * from the IDSCP2 server route.
 */
@Component("DefaultErrorResponseProcessor")
@RequiredArgsConstructor
public class DefaultErrorResponseProcessor implements Processor {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Checks whether the exchange message's body is a valid response DTO or {@link ErrorResponse}
     * and sets a default error response as the body, if neither is the case.
     *
     * @param exchange the exchange.
     * @throws Exception if any error occurs.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var response = exchange.getIn().getBody(Response.class);
        if (response == null) {
            final var errorResponse = exchange.getIn().getBody(ErrorResponse.class);
            if (errorResponse == null) {
                final var defaultResponse = ErrorResponse.withDefaultHeader(
                        RejectionReason.INTERNAL_RECIPIENT_ERROR,
                        "Could not process request.",
                        connectorService.getConnectorId(),
                        connectorService.getOutboundModelVersion());
                exchange.getIn().setBody(defaultResponse);
            }
        }
    }
}
