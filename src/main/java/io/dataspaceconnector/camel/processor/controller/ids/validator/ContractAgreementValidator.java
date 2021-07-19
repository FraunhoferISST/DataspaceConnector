package io.dataspaceconnector.camel.processor.controller.ids.validator;

import de.fraunhofer.iais.eis.ContractRequest;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Compares a received contract agreement to the initial contract request.
 */
@Component("ContractAgreementValidator")
@RequiredArgsConstructor
public class ContractAgreementValidator extends IdsValidator {

    /**
     * Service for managing contracts.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Compares the contract agreement to the contract request.
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var contractRequest = exchange
                .getProperty(ParameterUtils.CONTRACT_REQUEST_PARAM, ContractRequest.class);
        final var agreementString = exchange.getIn().getBody(Response.class).getBody();

        final var agreement = contractManager
                .validateContractAgreement(agreementString, contractRequest);

        exchange.setProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, agreement);
    }

}
