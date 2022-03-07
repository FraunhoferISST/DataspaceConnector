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
package io.dataspaceconnector.extension.idscp.processor;

import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.extension.idscp.processor.base.Idscp2MappingProcessor;
import io.dataspaceconnector.service.message.handler.dto.Response;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Converts a response DTO to an IDSCPv2 message.
 */
@Component("OutgoingIdscpMessageParser")
public class OutgoingMessageParser extends Idscp2MappingProcessor {

    /**
     * Creates an IDSCPv2 message with header and payload from a {@link Response}.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var response = in.getBody(Response.class);

        if (response != null) {
            in.setHeader(ParameterUtils.IDSCP_HEADER, response.getHeader());
            in.setBody(response.getBody().getBytes(StandardCharsets.UTF_8));
        } else {
            final var rejection = in.getBody(ErrorResponse.class);
            in.setHeader(ParameterUtils.IDSCP_HEADER, rejection.getRejectionMessage());
            in.setBody(rejection.getErrorMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

}
