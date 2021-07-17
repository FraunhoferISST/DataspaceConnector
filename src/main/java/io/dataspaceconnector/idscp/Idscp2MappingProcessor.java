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
package io.dataspaceconnector.idscp;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.dto.Response;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

public abstract class Idscp2MappingProcessor implements Processor {

    protected final String IDSCP2_HEADER = "idscp2-header";

    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange.getIn());
    }

    protected abstract void processInternal(Message in);

}

@Component("ResponseToDtoConverter")
class ResponseToDtoConverter extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var header = in.getHeader(IDSCP2_HEADER, de.fraunhofer.iais.eis.Message.class);
        final var payload = new String(in.getBody(byte[].class));

        in.setBody(new Response(header, payload));
    }

}

@Component("RequestWithoutPayloadPreparer")
class RequestWithoutPayloadPreparer extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);

        in.setHeader(IDSCP2_HEADER, request.getHeader());
        in.setBody(null);
    }

}

@Component("ContractRequestPreparer")
class ContractRequestPreparer extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var contractRequest = (ContractRequest) request.getBody();

        in.setHeader(IDSCP2_HEADER, request.getHeader());
        in.setBody(contractRequest.toRdf().getBytes(StandardCharsets.UTF_8));
    }
}

@Component("ContractAgreementPreparer")
class ContractAgreementPreparer extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var agreement = (ContractAgreement) request.getBody();

        in.setHeader(IDSCP2_HEADER, request.getHeader());
        in.setBody(agreement.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}

@Component("RequestWithResourcePayloadPreparer")
class RequestWithResourcePayloadPreparer extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var resource = (Resource) request.getBody();

        in.setHeader(IDSCP2_HEADER, request.getHeader());
        in.setBody(resource.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}

@Component("RequestWithConnectorPayloadPreparer")
class RequestWithConnectorPayloadPreparer extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var connector = (Connector) request.getBody();

        in.setHeader(IDSCP2_HEADER, request.getHeader());
        in.setBody(connector.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}

@Component("QueryPreparer")
class QueryPreparer extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var payload = (String) request.getBody();

        in.setHeader(IDSCP2_HEADER, request.getHeader());
        in.setBody(payload.getBytes(StandardCharsets.UTF_8));
    }

}

@Component("IncomingIdscpMessageParser")
class IncomingMessageParser extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var header = in.getHeader(IDSCP2_HEADER, de.fraunhofer.iais.eis.Message.class);
        final var payloadStream = new ByteArrayInputStream(in.getBody(byte[].class));
        final var payload = new MessagePayloadInputstream(payloadStream, new ObjectMapper());
        in.setBody(new Request(header, payload, Optional.empty()));
    }

}

@Component("OutgoingIdscpMessageParser")
class OutgoingMessageParser extends Idscp2MappingProcessor {

    @Override
    protected void processInternal(final Message in) {
        final var response = in.getBody(Response.class);

        if (response != null) {
            in.setHeader("idscp2-header", response.getHeader());
            in.setBody(response.getBody().getBytes());
        } else {
            final var rejection = in.getBody(ErrorResponse.class);
            in.setHeader("idscp2-header", rejection.getRejectionMessage());
            in.setBody(rejection.getErrorMessage().getBytes());
        }
    }

}
