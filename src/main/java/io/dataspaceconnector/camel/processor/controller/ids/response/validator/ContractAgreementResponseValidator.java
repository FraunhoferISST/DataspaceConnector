package io.dataspaceconnector.camel.processor.controller.ids.response.validator;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ProcessorUtils;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates the response to a ContractAgreementMessage.
 */
@Component("ContractAgreementResponseValidator")
@RequiredArgsConstructor
public class ContractAgreementResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for ContractAgreementMessage handling.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Validates the response to a ContractAgreementMessage.
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
