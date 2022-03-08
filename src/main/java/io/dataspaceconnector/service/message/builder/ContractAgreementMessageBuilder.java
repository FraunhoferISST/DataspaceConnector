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

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.model.message.ContractAgreementMessageDesc;
import io.dataspaceconnector.service.message.builder.base.IdsMessageBuilder;
import io.dataspaceconnector.service.message.builder.type.ContractAgreementService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;

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
     *
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
