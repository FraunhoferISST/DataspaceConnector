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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsValidator;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.util.QueryInput;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Superclass for all processors that perform mapping between IDSCPv2 messages and the DTOs used
 * in the routes.
 */
public abstract class Idscp2MappingProcessor implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}'s in-message.
     *
     * @param exchange the exchange.
     * @throws Exception if mapping fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange.getIn());
    }

    /**
     * Performs the mapping operation. To be implemented by sub classes.
     *
     * @param in the in-message of the exchange.
     */
    protected abstract void processInternal(Message in) throws Exception;

}

/**
 * Converts a response received over IDSCPv2 to a response DTO.
 */
@Component("ResponseToDtoConverter")
class ResponseToDtoConverter extends Idscp2MappingProcessor {

    /**
     * Converts an incoming response message to q {@link Response}.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var header = in
                .getHeader(ParameterUtils.IDSCP_HEADER, de.fraunhofer.iais.eis.Message.class);
        final var payload = new String(in.getBody(byte[].class), StandardCharsets.UTF_8);

        in.setBody(new Response(header, payload));
    }

}

/**
 * Prepares a request message for IDSCPv2 communication without a payload.
 */
@Component("RequestWithoutPayloadPreparer")
class RequestWithoutPayloadPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with empty body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(null);
    }

}

/**
 * Prepares a request message for IDSCPv2 communication with a contract request as payload.
 */
@Component("ContractRequestPreparer")
class ContractRequestPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with a contract request as body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var contractRequest = (ContractRequest) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(contractRequest.toRdf().getBytes(StandardCharsets.UTF_8));
    }
}

/**
 * Prepares a request message for IDSCPv2 communication with a contract agreement as payload.
 */
@Component("ContractAgreementPreparer")
class ContractAgreementPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with a contract agreement as body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var agreement = (ContractAgreement) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(agreement.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}

/**
 * Prepares an artifact request message for IDSCPv2 communication with a query input as payload.
 */
@Component("ArtifactRequestPreparer")
class ArtifactRequestPreparer extends Idscp2MappingProcessor {

    /**
     * ObjectMapper for writing the query input to JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Prepares a {@link Request} with an ArtifactRequestMessage as header and a query input as
     * body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     * @throws JsonProcessingException if writing the query input to JSON fails.
     */
    @Override
    protected void processInternal(final Message in) throws JsonProcessingException {
        final var request = in.getBody(Request.class);
        final var queryInput = (QueryInput) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        if (queryInput != null) {
            in.setBody(objectMapper
                    .writeValueAsString(queryInput).getBytes(StandardCharsets.UTF_8));
        } else {
            in.setBody("".getBytes(StandardCharsets.UTF_8));
        }

    }

}

/**
 * Prepares a request message for IDSCPv2 communication with a resource as payload.
 */
@Component("RequestWithResourcePayloadPreparer")
class RequestWithResourcePayloadPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with a resource as body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var resource = (Resource) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(resource.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}

/**
 * Prepares a request message for IDSCPv2 communication with a connector as payload.
 */
@Component("RequestWithConnectorPayloadPreparer")
class RequestWithConnectorPayloadPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with a connector as body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var connector = (Connector) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(connector.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}

/**
 * Prepares a request message for IDSCPv2 communication with a query string as payload.
 */
@Component("QueryPreparer")
class QueryPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with a query string as body for communication over IDSCPv2.
     *
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var payload = (String) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(payload.getBytes(StandardCharsets.UTF_8));
    }

}

/**
 * Converts an incoming IDSCPv2 message to a request DTO.
 */
@Component("IncomingIdscpMessageParser")
@RequiredArgsConstructor
class IncomingMessageParser extends Idscp2MappingProcessor {

    /**
     * Service for validating DATs.
     */
    private final @NonNull DapsValidator dapsValidator;

    /**
     * Creates a {@link Request} with the header and payload from the IDSCPv2 message. Also
     * gets the claims from the DAT and adds them to the request.
     *
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

        in.setBody(new Request(header, payload, claims));
    }

}

/**
 * Converts a response DTO to an IDSCPv2 message.
 */
@Component("OutgoingIdscpMessageParser")
class OutgoingMessageParser extends Idscp2MappingProcessor {

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
