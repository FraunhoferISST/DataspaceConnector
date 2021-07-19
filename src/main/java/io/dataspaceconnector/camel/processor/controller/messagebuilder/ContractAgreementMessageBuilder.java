package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.Optional;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.model.message.ContractAgreementMessageDesc;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Builds a ContractAgreementMessage and creates a request DTO with header and payload.
 */
@Component("ContractAgreementMessageBuilder")
@RequiredArgsConstructor
public class ContractAgreementMessageBuilder extends
        IdsMessageBuilder<ContractAgreementMessageImpl, ContractAgreement> {

    /**
     * The service for managing agreements.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Builds a ContractAgreementMessage and creates a Request with the message as header and the
     * contract agreement from the exchange properties as payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ContractAgreementMessageImpl, ContractAgreement, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var agreement = exchange
                .getProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, ContractAgreement.class);
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = (ContractAgreementMessageImpl) agreementSvc
                .buildMessage(new ContractAgreementMessageDesc(recipient, agreement.getId()));

        return new Request<>(message, agreement, Optional.empty());
    }

}
