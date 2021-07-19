package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.model.message.ContractRequestMessageDesc;
import io.dataspaceconnector.service.message.type.ContractRequestService;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Builds a ContractRequestMessage and creates a request DTO with header and payload.
 */
@Component("ContractRequestMessageBuilder")
@RequiredArgsConstructor
public class ContractRequestMessageBuilder
        extends IdsMessageBuilder<ContractRequestMessageImpl, ContractRequest> {

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Service for contract request message handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    /**
     * Builds a ContractRequestMessage and a contract request according to the exchange properties
     * and creates a Request with the message as header and the contract request as payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ContractRequestMessageImpl, ContractRequest, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var ruleList = exchange.getProperty(ParameterUtils.RULE_LIST_PARAM, List.class);
        final var request = contractManager.buildContractRequest(toRuleList(ruleList));
        exchange.setProperty("contractRequest", request);

        final var message = (ContractRequestMessageImpl) contractReqSvc
                .buildMessage(new ContractRequestMessageDesc(recipient, request.getId()));

        return new Request<>(message, request, Optional.empty());
    }

    @SuppressWarnings("unchecked")
    private static List<Rule> toRuleList(final List<?> list) {
        return (List<Rule>) list;
    }
}
