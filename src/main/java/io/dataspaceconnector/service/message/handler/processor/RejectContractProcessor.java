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
package io.dataspaceconnector.service.message.handler.processor;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.model.message.ContractRejectionMessageDesc;
import io.dataspaceconnector.service.message.builder.type.ContractRejectionService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.service.message.handler.processor.base.IdsProcessor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Rejects a contract request and generates the response.
 */
@Component("RejectContractProcessor")
@RequiredArgsConstructor
class RejectContractProcessor extends
        IdsProcessor<RouteMsg<ContractRequestMessageImpl, ContractTargetRuleMapContainer>> {

    /**
     * Service for ids contract rejection messages.
     */
    private final @NonNull ContractRejectionService rejectionService;

    /**
     * Generates the response for rejecting a contract.
     *
     * @param msg the incoming message.
     * @return a Response object with a ContractRejectionMessage as header.
     * @throws Exception if the response cannot be built.
     */
    @Override
    protected Response processInternal(final RouteMsg<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer> msg, final Jws<Claims> claims) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        // Build ids response message.
        final var desc = new ContractRejectionMessageDesc(issuer, messageId);
        final var header = (RejectionMessage) rejectionService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, "Contract rejected.");
    }

}
