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
package io.dataspaceconnector.camel.outgoing;

import java.util.HashMap;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.outgoing.exceptions.InvalidResponseException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.service.message.type.ArtifactRequestService;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import io.dataspaceconnector.service.message.type.ContractRequestService;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

public abstract class IdsResponseMessageValidator implements Processor {

    protected final String HEADER_NAME = "header";
    protected final String PAYLOAD_NAME = "payload";

    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange.getIn().getBody(Response.class));
    }

    protected abstract void processInternal(Response response) throws Exception;

}

@Component("DescriptionResponseValidator")
@RequiredArgsConstructor
class DescriptionResponseValidator extends IdsResponseMessageValidator {

    private final @NonNull DescriptionRequestService descReqSvc;

    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = new HashMap<String, String>();
        map.put(HEADER_NAME, response.getHeader().toRdf());
        map.put(PAYLOAD_NAME, response.getBody());

        if (!descReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = descReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, "Received an invalid response.");
        }

    }
}

@Component("ContractResponseValidator")
@RequiredArgsConstructor
class ContractResponseValidator extends IdsResponseMessageValidator {

    private final @NonNull ContractRequestService contractReqSvc;

    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = new HashMap<String, String>();
        map.put(HEADER_NAME, response.getHeader().toRdf());
        map.put(PAYLOAD_NAME, response.getBody());

        if (!contractReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = contractReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, "Received an invalid response.");
        }
    }

}

@Component("ContractAgreementResponseValidator")
@RequiredArgsConstructor
class ContractAgreementResponseValidator extends IdsResponseMessageValidator {

    private final @NonNull ContractAgreementService agreementSvc;

    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = new HashMap<String, String>();
        map.put(HEADER_NAME, response.getHeader().toRdf());
        map.put(PAYLOAD_NAME, response.getBody());

        if (!agreementSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = agreementSvc.getResponseContent(map);
            throw new InvalidResponseException(content, "Received an invalid response.");
        }
    }

}

@Component("ArtifactResponseValidator")
@RequiredArgsConstructor
class ArtifactResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for artifact request message handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = new HashMap<String, String>();
        map.put(HEADER_NAME, response.getHeader().toRdf());
        map.put(PAYLOAD_NAME, response.getBody());

        if (!artifactReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = artifactReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, "Received an invalid response.");
        }
    }
}
