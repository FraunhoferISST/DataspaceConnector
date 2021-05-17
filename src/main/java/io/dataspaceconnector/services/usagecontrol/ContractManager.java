/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.services.usagecontrol;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyImpl;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionImpl;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.ProhibitionImpl;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import io.dataspaceconnector.exceptions.ContractException;
import io.dataspaceconnector.exceptions.MessageResponseException;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.resources.EntityDependencyResolver;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.RuleUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * This service offers methods related to contract management.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ContractManager {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for resolving elements and its parents/children.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Check if the transfer contract is valid and the conditions are fulfilled.
     *
     * @param agreementId       The id of the contract.
     * @param requestedArtifact The id of the artifact.
     * @return The contract agreement on successful validation.
     * @throws IllegalArgumentException  if contract agreement deserialization fails.
     * @throws ResourceNotFoundException if agreement could not be found.
     * @throws ContractException         if the contract agreement does not match the requested
     *                                   artifact or is not confirmed.
     */
    public ContractAgreement validateTransferContract(
            final URI agreementId, final URI requestedArtifact) throws IllegalArgumentException,
            ResourceNotFoundException, ContractException {
        final var agreement = entityResolver.getAgreementByUri(agreementId);
        final var artifacts = dependencyResolver.getArtifactsByAgreement(agreement);

        final var valid = ContractUtils.isMatchingTransferContract(artifacts, requestedArtifact);
        // TODO Add validation of issuer connector.
        if (!valid) {
            // If the requested artifact does not match the agreement, send rejection message.
            throw new ContractException("Transfer contract does not match the requested artifact.");
        }

        // Negotiation has to be finished to make the agreement valid.
        if (!agreement.isConfirmed()) {
            throw new ContractException("Contract agreement has not been confirmed. Send contract "
                    + "agreement message to finish the negotiation sequence.");
        }

        return deserializationService.getContractAgreement(agreement.getValue());
    }

    /**
     * Read and validate ids contract agreement from ids response message.
     *
     * @param payload The message's payload (agreement as string).
     * @param request The contract request that was sent.
     * @return The ids contract agreement.
     * @throws MessageResponseException If the response could not be processed.
     * @throws IllegalArgumentException If deserialization fails.
     * @throws ContractException        If the contract's content is invalid.
     */
    public ContractAgreement validateContractAgreement(
            final String payload, final ContractRequest request) throws MessageResponseException,
            IllegalArgumentException, ContractException {
        final var agreement = deserializationService.getContractAgreement(payload);

        ContractUtils.validateRuleAssigner(agreement);
        RuleUtils.validateRuleContent(request, agreement);

        return agreement;
    }

    /**
     * Build contract request from a list of rules - with assignee and consumer.
     *
     * @param ruleList The rule list.
     * @return The ids contract request.
     * @throws ConstraintViolationException If ids contract building fails.
     */
    public ContractRequest buildContractRequest(final List<? extends Rule> ruleList)
            throws ConstraintViolationException {
        final var connectorId = connectorService.getConnectorId();

        final var permissions = new ArrayList<Permission>();
        final var prohibitions = new ArrayList<Prohibition>();
        final var obligations = new ArrayList<Duty>();

        // Add assignee to all rules.
        for (final var rule : ruleList) {
            if (rule instanceof Permission) {
                ((PermissionImpl) rule).setAssignee(Util.asList(connectorId));
                permissions.add((Permission) rule);
            } else if (rule instanceof Prohibition) {
                ((ProhibitionImpl) rule).setAssignee(Util.asList(connectorId));
                prohibitions.add((Prohibition) rule);
            } else if (rule instanceof Duty) {
                ((DutyImpl) rule).setAssignee(Util.asList(connectorId));
                obligations.add((Duty) rule);
            }
        }

        // Return contract request.
        return new ContractRequestBuilder()
                ._consumer_(connectorId)
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                .build();
    }

    /**
     * Build contract agreement from contract request. Sign all rules as assigner.
     *
     * @param request The contract request.
     * @param id      ID to use when creating the contract agreement.
     * @param issuer  The issuer connector id.
     * @return The contract agreement.
     * @throws ConstraintViolationException If building a contract agreement fails.
     */
    public ContractAgreement buildContractAgreement(
            final ContractRequest request, final URI id, final URI issuer)
            throws ConstraintViolationException {
        final var connectorId = connectorService.getConnectorId();

        final var ruleList = ContractUtils.extractRulesFromContract(request);

        final var permissions = new ArrayList<Permission>();
        final var prohibitions = new ArrayList<Prohibition>();
        final var obligations = new ArrayList<Duty>();

        // Add assigner to all rules.
        for (final var rule : ruleList) {
            if (rule instanceof Permission) {
                ((PermissionImpl) rule).setAssigner(Util.asList(connectorId));
                permissions.add((Permission) rule);
            } else if (rule instanceof Prohibition) {
                ((ProhibitionImpl) rule).setAssigner(Util.asList(connectorId));
                prohibitions.add((Prohibition) rule);
            } else if (rule instanceof Duty) {
                ((DutyImpl) rule).setAssigner(Util.asList(connectorId));
                obligations.add((Duty) rule);
            }
        }

        // Return contract request.
        return new ContractAgreementBuilder(id)
                ._consumer_(issuer)
                ._contractDate_(IDSUtils.getGregorianNow())
                ._contractStart_(IDSUtils.getGregorianNow())
                ._contractEnd_(request.getContractEnd()) // TODO Improve calculation of contract
                // end.
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                ._provider_(connectorId)
                .build();
    }
}
