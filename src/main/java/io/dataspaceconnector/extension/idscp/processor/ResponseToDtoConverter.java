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

import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.extension.idscp.processor.base.Idscp2MappingProcessor;
import io.dataspaceconnector.service.message.handler.dto.Response;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Converts a response received over IDSCPv2 to a response DTO.
 */
@Component("ResponseToDtoConverter")
public class ResponseToDtoConverter extends Idscp2MappingProcessor {

    /**
     * Converts an incoming response message to q {@link Response}.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var header = in.getHeader(ParameterUtils.IDSCP_HEADER,
                de.fraunhofer.iais.eis.Message.class);
        final var payload = new String(in.getBody(byte[].class), StandardCharsets.UTF_8);

        in.setBody(new Response(header, payload));
    }

}
