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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.extension.idscp.processor.base.Idscp2MappingProcessor;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.message.handler.dto.Request;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Prepares a request message for IDSCPv2 communication with a subscription desc as payload.
 */
@Component("RequestWithSubscriptionPayloadPreparer")
public class RequestWithSubscriptionPayloadPreparer extends Idscp2MappingProcessor {

    /**
     * ObjectMapper for writing the query input to JSON.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Prepares a {@link Request} with a RequestMessage as header and a {@link SubscriptionDesc}
     * as body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     * @throws JsonProcessingException if writing the subscription desc to JSON fails.
     */
    @Override
    protected void processInternal(final Message in) throws JsonProcessingException {
        final var request = in.getBody(Request.class);
        final var subscription = (SubscriptionDesc) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        if (subscription != null) {
            in.setBody(mapper.writeValueAsString(subscription).getBytes(StandardCharsets.UTF_8));
        } else {
            in.setBody("".getBytes(StandardCharsets.UTF_8));
        }
    }

}
