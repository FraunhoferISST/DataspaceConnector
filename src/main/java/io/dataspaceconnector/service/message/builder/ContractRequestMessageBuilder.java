/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.message.builder;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.model.message.ContractRequestMessageDesc;
import io.dataspaceconnector.service.message.builder.base.IdsMessageBuilder;
import io.dataspaceconnector.service.message.builder.type.ContractRequestService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
     *
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
