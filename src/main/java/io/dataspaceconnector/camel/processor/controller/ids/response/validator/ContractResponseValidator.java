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
package io.dataspaceconnector.camel.processor.controller.ids.response.validator;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ProcessorUtils;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.service.message.type.ContractRequestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates the response to a ContractRequestMessage.
 */
@Component("ContractResponseValidator")
@RequiredArgsConstructor
public class ContractResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for ContractRequestMessage handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    /**
     * Validates the response to a ContractRequestMessage.
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
