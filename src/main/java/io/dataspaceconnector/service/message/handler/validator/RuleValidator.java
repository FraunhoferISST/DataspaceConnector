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
package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.ids.policy.ContractUtils;
import io.dataspaceconnector.service.EntityDependencyResolver;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.service.message.handler.exception.ContractListEmptyException;
import io.dataspaceconnector.service.message.handler.exception.ContractRejectedException;
import io.dataspaceconnector.service.message.handler.exception.MalformedRuleException;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Validates the rules from a contract request by comparing them to the corresponding contract
 * offer.
 */
@Component("RuleValidator")
@RequiredArgsConstructor
class RuleValidator extends IdsValidator<Request<ContractRequestMessageImpl,
        ContractTargetRuleMapContainer, Optional<Jws<Claims>>>> {

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for validating the rules from a contract.
     */
    private final @NonNull io.dataspaceconnector.service.usagecontrol.RuleValidator ruleValidator;

    /**
     * Compares the rules from the contract request to the rules from the contract offers for
     * the artifact for each given target.
     *
     * @param msg the incoming message.
     * @throws Exception if there are no contract offers for the artifact or the rules do not match.
     */
    @Override
    protected void processInternal(final Request<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer, Optional<Jws<Claims>>> msg) throws Exception {
        final var targetRuleMap = msg.getBody().getTargetRuleMap();
        final var request = msg.getBody().getContractRequest();
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());

        // Retrieve matching contract offers to compare the content.
        for (final var target : targetRuleMap.keySet()) {
            final var valid = checkRule(target, request, issuer, targetRuleMap);
            if (!valid) {
                throw new ContractRejectedException(issuer, messageId, "Contract rejected.");
            }
        }
    }

    /**
     * Checks whether there is an applicable contract offer for the requesting consumer and the
     * target artifact and if there is, compares the rules from the request to the ones from the
     * contract offer.
     *
     * @param target        URI of the target artifact.
     * @param request       the contract request.
     * @param issuer        the issuer connector of the request.
     * @param targetRuleMap the list of rules from the contract request.
     * @return true, if there is a valid offer; false otherwise.
     * @throws ContractListEmptyException if there are no contract offers for the artifact.
     */
    private boolean checkRule(final URI target, final ContractRequest request, final URI issuer,
                              final Map<URI, List<Rule>> targetRuleMap) {
        final var contracts = dependencyResolver.getContractOffersByArtifactId(target);

        // Abort negotiation if no contract offer could be found.
        if (contracts.isEmpty()) {
            throw new ContractListEmptyException(request, "List of contracts is empty.");
        }

        // Abort negotiation if no contract offer with a valid time interval could be found
        // for the issuer connector.
        final var contractsWithValidTimeInterval = ContractUtils
                .removeContractsWithInvalidDates(contracts);
        final var validContracts = ContractUtils
                .removeContractsWithInvalidConsumer(contractsWithValidTimeInterval, issuer);
        if (validContracts.isEmpty()) {
            throw new ContractListEmptyException(request, "List of valid contracts is empty.");
        }

        try {
            final var contract = ruleValidator
                    .findMatchingContractForRequest(validContracts, targetRuleMap, target);
            if (contract.isEmpty()) {
                return false;
            } else {
                // Set the end date of the matching contract offer for the request
                request.setContractEnd(ToIdsObjectMapper.getGregorianOf(contract.get().getEnd()));
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new MalformedRuleException("Malformed rule.", e);
        }
    }

}
