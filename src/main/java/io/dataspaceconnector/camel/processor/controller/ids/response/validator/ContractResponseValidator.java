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
