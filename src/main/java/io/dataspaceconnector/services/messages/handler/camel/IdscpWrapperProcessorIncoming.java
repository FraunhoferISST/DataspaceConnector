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
package io.dataspaceconnector.services.messages.handler.camel; // IdscpWrapperProcessor

import java.io.ByteArrayInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import io.dataspaceconnector.services.messages.handler.camel.dto.Request;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Processor that parses an incoming IDSCP message to the DTO used in the message handlers' Camel
 * routes.
 */
@Component("idscpWrapperProcessorIncoming")
public class IdscpWrapperProcessorIncoming implements Processor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var header = exchange.getIn().getHeader("idscp2-header", Message.class);
        final var payloadStream = new ByteArrayInputStream(exchange.getIn().getBody(byte[].class));
        final var payload = new MessagePayloadInputstream(payloadStream, new ObjectMapper());
        exchange.getIn().setBody(new Request(header, payload));
    }

}
