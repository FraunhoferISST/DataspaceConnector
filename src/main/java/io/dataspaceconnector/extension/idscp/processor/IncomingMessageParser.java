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
package io.dataspaceconnector.extension.idscp.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsValidator;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.extension.idscp.processor.base.Idscp2MappingProcessor;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * Converts an incoming IDSCPv2 message to a request DTO.
 */
@Component("IncomingIdscpMessageParser")
@RequiredArgsConstructor
public class IncomingMessageParser extends Idscp2MappingProcessor {

    /**
     * Service for validating DATs.
     */
    private final @NonNull DapsValidator dapsValidator;

    /**
     * Creates a {@link Request} with the header and payload from the IDSCPv2 message. Also gets the
     * claims from the DAT and adds them to the request.
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var header = in
                .getHeader(ParameterUtils.IDSCP_HEADER, de.fraunhofer.iais.eis.Message.class);
        final var payloadStream = new ByteArrayInputStream(in.getBody(byte[].class));
        final var payload = new MessagePayloadInputstream(payloadStream, new ObjectMapper());

        Optional<Jws<Claims>> claims;
        try {
            claims = Optional.of(dapsValidator.getClaims(header.getSecurityToken()));
        } catch (ClaimsException | ExpiredJwtException exception) {
            claims = Optional.empty();
        }

        in.setBody(new Request<>(header, payload, claims));
    }

}
