package io.dataspaceconnector.camel.processor.controller.ids.response.processor;

import de.fraunhofer.iais.eis.ContractAgreement;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.service.EntityPersistenceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Persists a contract agreement received as the response to a ContractRequestMessage.
 */
@Component("ContractAgreementPersistenceProcessor")
@RequiredArgsConstructor
public class ContractAgreementPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull
    EntityPersistenceService persistenceSvc;

    /**
     * Persists the contract agreement.
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var agreement = exchange
                .getProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, ContractAgreement.class);
        final var agreementId = persistenceSvc.saveContractAgreement(agreement);
        exchange.setProperty(ParameterUtils.AGREEMENT_ID_PARAM, agreementId);
    }

}
