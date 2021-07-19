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
package io.dataspaceconnector.camel.processor.controller;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ProcessorUtils;
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

/**
 * Superclass for all processors that validate a received response message.
 */
public abstract class IdsResponseMessageValidator implements Processor {

    /**
     * The error message used for throwing an Exception when the response is not valid.
     */
    protected static final String ERROR_MESSAGE = "Received an invalid response.";

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}'s body as parameter.
     *
     * @param exchange the exchange.
     * @throws Exception if validation fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange.getIn().getBody(Response.class));
    }

    /**
     * Validates the response DTO. To be implemented by sub classes.
     *
     * @param response the response DTO.
     * @throws Exception if validation fails.
     */
    protected abstract void processInternal(Response response) throws Exception;

}

/**
 * Validates the response to a DescriptionRequestMessage.
 */
@Component("DescriptionResponseValidator")
@RequiredArgsConstructor
class DescriptionResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for DescriptionRequestMessage handling.
     */
    private final @NonNull DescriptionRequestService descReqSvc;

    /**
     * Validates the response to a DescriptionRequestMessage.
     *
     * @param response the response DTO.
     * @throws MessageResponseException if the received response is not valid.
     */
    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = ProcessorUtils.getResponseMap(response);

        if (!descReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = descReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, ERROR_MESSAGE);
        }

    }
}

/**
 * Validates the response to a ContractRequestMessage.
 */
@Component("ContractResponseValidator")
@RequiredArgsConstructor
class ContractResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for ContractRequestMessage handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    /**
     * Validates the response to a ContractRequestMessage.
     *
     * @param response the response DTO.
     * @throws MessageResponseException if the received response is not valid.
     */
    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = ProcessorUtils.getResponseMap(response);

        if (!contractReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = contractReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, ERROR_MESSAGE);
        }
    }

}

/**
 * Validates the response to a ContractAgreementMessage.
 */
@Component("ContractAgreementResponseValidator")
@RequiredArgsConstructor
class ContractAgreementResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for ContractAgreementMessage handling.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Validates the response to a ContractAgreementMessage.
     *
     * @param response the response DTO.
     * @throws MessageResponseException if the received response is not valid.
     */
    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = ProcessorUtils.getResponseMap(response);

        if (!agreementSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = agreementSvc.getResponseContent(map);
            throw new InvalidResponseException(content, ERROR_MESSAGE);
        }
    }

}

/**
 * Validates the response to an ArtifactRequestMessage.
 */
@Component("ArtifactResponseValidator")
@RequiredArgsConstructor
class ArtifactResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for ArtifactRequestMessage handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Validates the response to an ArtifactRequestMessage.
     *
     * @param response the response DTO.
     * @throws MessageResponseException if the received response is not valid.
     */
    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = ProcessorUtils.getResponseMap(response);

        if (!artifactReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = artifactReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, ERROR_MESSAGE);
        }
    }
}
