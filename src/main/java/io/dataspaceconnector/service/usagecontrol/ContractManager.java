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
package io.dataspaceconnector.service.usagecontrol;

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
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.ids.policy.ContractUtils;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.EntityDependencyResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This service offers methods related to contract management.
 */
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
     * @param issuer            The id of the issuer connector.
     * @return The contract agreement on successful validation.
     * @throws IllegalArgumentException  if contract agreement deserialization fails.
     * @throws ResourceNotFoundException if agreement could not be found.
     * @throws ContractException         if the contract agreement does not match the requested
     *                                   artifact or is not confirmed.
     */
    public ContractAgreement validateTransferContract(
            final URI agreementId, final URI requestedArtifact, final URI issuer)
            throws IllegalArgumentException, ResourceNotFoundException, ContractException {
        final var entity = entityResolver.getEntityById(agreementId);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString());
        }
        final var agreement = (Agreement) entity.get();
        final var artifacts = dependencyResolver.getArtifactsByAgreement(agreement);

        if (!ContractUtils.isMatchingTransferContract(artifacts, requestedArtifact)) {
            // If the requested artifact does not match the agreement, send rejection message.
            throw new ContractException("Transfer contract does not match the requested artifact.");
        }

        // Negotiation has to be finished to make the agreement valid.
        if (!agreement.isConfirmed()) {
            throw new ContractException("Contract agreement has not been confirmed. Send contract "
                    + "agreement message to finish the negotiation sequence.");
        }

        final var idsAgreement = deserializationService.getContractAgreement(agreement.getValue());

        // Validation of end date.
        final var endDate = idsAgreement.getContractEnd()
                .toGregorianCalendar().toZonedDateTime();
        if (endDate.isBefore(ZonedDateTime.now())) {
            throw new ContractException("The agreement has expired.");
        }

        // Validation of issuer connector.
        if (!idsAgreement.getConsumer().equals(issuer)) {
            throw new ContractException("The issuer connector does not correspond to the signed "
                    + "consumer.");
        }

        return idsAgreement;
    }

    /**
     * Read and validate ids contract agreement from ids response message.
     *
     * @param payload The message's payload (agreement as string).
     * @param request The contract request that was sent.
     * @return The ids contract agreement.
     * @throws IllegalArgumentException if deserialization fails.
     * @throws ContractException        if the contract's content is invalid.
     */
    public ContractAgreement validateContractAgreement(
            final String payload, final ContractRequest request) throws IllegalArgumentException,
            ContractException {
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
     * @param request     The contract request.
     * @param agreementId ID to use when creating the contract agreement.
     * @param issuer      The issuer connector id.
     * @return The contract agreement.
     * @throws ConstraintViolationException If building a contract agreement fails.
     */
    public ContractAgreement buildContractAgreement(
            final ContractRequest request, final URI agreementId, final URI issuer)
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
        return new ContractAgreementBuilder(agreementId)
                ._consumer_(issuer)
                ._contractDate_(IdsMessageUtils.getGregorianNow())
                ._contractStart_(IdsMessageUtils.getGregorianNow())
                ._contractEnd_(request.getContractEnd())
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                ._provider_(connectorId)
                .build();
    }
}
