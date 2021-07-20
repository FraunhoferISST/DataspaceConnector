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
package io.dataspaceconnector.camel.processor.handler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.dto.payload.ContractRuleListContainer;
import io.dataspaceconnector.camel.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.camel.exception.ContractListEmptyException;
import io.dataspaceconnector.camel.exception.ContractRejectedException;
import io.dataspaceconnector.camel.exception.InvalidAffectedResourceException;
import io.dataspaceconnector.camel.exception.MalformedRuleException;
import io.dataspaceconnector.camel.exception.MissingRulesException;
import io.dataspaceconnector.camel.exception.MissingTargetInRuleException;
import io.dataspaceconnector.camel.exception.NoAffectedResourceException;
import io.dataspaceconnector.camel.exception.NoRequestedArtifactException;
import io.dataspaceconnector.camel.exception.NoTransferContractException;
import io.dataspaceconnector.exception.PolicyRestrictionException;
import io.dataspaceconnector.service.message.type.DescriptionResponseService;
import io.dataspaceconnector.service.resource.EntityDependencyResolver;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.service.usagecontrol.DataProvisionVerifier;
import io.dataspaceconnector.service.usagecontrol.ProvisionVerificationInput;
import io.dataspaceconnector.service.usagecontrol.VerificationResult;
import io.dataspaceconnector.util.ContractUtils;
import io.dataspaceconnector.util.ErrorMessage;
import io.dataspaceconnector.util.IdsUtils;
import io.dataspaceconnector.util.MessageUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Superclass for Camel processors that validate either header or payload of an incoming message.
 *
 * @param <I> the expected input type (body of the Camel {@link Exchange}).
 */
public abstract class IdsValidator<I> implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method.
     *
     * @param exchange the input.
     * @throws Exception if validation fails.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        processInternal((I) exchange.getIn().getBody(Request.class));
    }

    /**
     * Validates either header of body of the incoming message. To be implemented by sub classes.
     *
     * @param msg the incoming message.
     * @throws Exception if validation fails.
     */
    protected abstract void processInternal(I msg) throws Exception;
}

/**
 * Validates that the correct resource ID was used in a ResourceUpdateMessage.
 */
@Component("CorrectAffectedResourceValidator")
class CorrectAffectedResourceValidator extends IdsValidator<Request<ResourceUpdateMessageImpl,
        Resource, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the resource ID given in a ResourceUpdateMessage matches the resource ID in
     * the message's payload.
     *
     * @param msg the incoming message.
     * @throws Exception if the IDs do not match.
     */
    @Override
    protected void processInternal(final Request<ResourceUpdateMessageImpl, Resource,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(msg.getHeader());
        if (!msg.getBody().getId().equals(affected)) {
            throw new InvalidAffectedResourceException("Resource in message payload does not "
                    + "match affected resource from message header.");
        }
    }

}

/**
 * Validates that the resource ID given in the header of a ResourceUpdateMessage is not null or
 * empty.
 */
@Component("AffectedResourceValidator")
class AffectedResourceValidator extends IdsValidator<Request<ResourceUpdateMessageImpl,
        MessagePayload, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the resource ID given in a ResourceUpdateMessage is null or empty.
     *
     * @param message the incoming message.
     * @throws Exception if the ID is null or empty.
     */
    @Override
    protected void processInternal(final Request<ResourceUpdateMessageImpl,
            MessagePayload, Optional<Jws<Claims>>> message) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(message.getHeader());
        if (affected == null || affected.toString().isEmpty()) {
            throw new NoAffectedResourceException("Affected resource is null or empty.");
        }
    }

}

/**
 * Validates the contract used in an ArtifactRequestMessage and checks whether data provision
 * is allowed.
 */
@Component("PolicyValidator")
@RequiredArgsConstructor
class PolicyValidator extends IdsValidator<Request<ArtifactRequestMessageImpl, MessagePayload,
        Optional<Jws<Claims>>>> {

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * The verifier for the data access.
     */
    private final @NonNull DataProvisionVerifier accessVerifier;

    /**
     * First checks whether the contract given in an ArtifactRequestMessage is not null or empty.
     * If it is not, checks whether that contract allows provisioning the data.
     *
     * @param msg the incoming message.
     * @throws Exception if the contract is null or empty or if data provision is denied.
     */
    @Override
    protected void processInternal(final Request<ArtifactRequestMessageImpl, MessagePayload,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var transferContract = MessageUtils.extractTransferContract(msg.getHeader());
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());

        if (transferContract == null || transferContract.toString().equals("")) {
            throw new NoTransferContractException("Transfer contract is missing.");
        }

        final var agreement = contractManager.validateTransferContract(
                transferContract, requestedArtifact, issuer);
        final var profile = extractSecurityProfile(msg.getClaims());
        final var input = new ProvisionVerificationInput(requestedArtifact, issuer, agreement,
                profile);
        if (accessVerifier.verify(input) == VerificationResult.DENIED) {
            throw new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
        }
    }

    private Optional<SecurityProfile> extractSecurityProfile(final Optional<Jws<Claims>> claims) {
        if (claims.isEmpty()) {
            return Optional.empty();
        }

        final var value = claims.get().getBody().get("securityProfile");
        final var profile = IdsUtils.getSecurityProfile(value.toString());
        if (profile.isEmpty()) {
            return Optional.empty();
        }

        return profile;
    }

}

/**
 * Validates the requested artifact given in an ArtifactRequestMessage.
 */
@Component("RequestedArtifactValidator")
class RequestedArtifactValidator extends IdsValidator<Request<ArtifactRequestMessageImpl,
        MessagePayload, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the requested artifact given in an ArtifactRequestMessage is null or empty.
     *
     * @param msg the incoming message.
     * @throws Exception if the requested artifact is null or empty.
     */
    @Override
    protected void processInternal(final Request<ArtifactRequestMessageImpl, MessagePayload,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            throw new NoRequestedArtifactException("Requested artifact is missing.");
        }
    }

}

/**
 * Validates any incoming message by checking whether the message is empty and whether it references
 * an Infomodel version supported by this connector.
 */
@RequiredArgsConstructor
@Component("MessageHeaderValidator")
class MessageHeaderValidator extends IdsValidator<Request<? extends Message, ?,
        Optional<Jws<Claims>>>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Checks whether the message is empty and whether it references an Infomodel version supported
     * by this connector.
     *
     * @param msg the incoming message.
     * @throws Exception if the message is empty or references an unsupported Infomodel version.
     */
    @Override
    protected void processInternal(final Request<? extends Message, ?, Optional<Jws<Claims>>> msg)
            throws Exception {
        messageService.validateIncomingMessage(msg.getHeader());
    }
}

/**
 * Validates the rules from a contract request.
 */
@Component("RuleListValidator")
class RuleListValidator extends IdsValidator<Request<ContractRequestMessageImpl,
        ContractRuleListContainer, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the list of rules from a contract request is empty.
     *
     * @param msg the incoming message.
     * @throws Exception if the list of rules is empty.
     */
    @Override
    protected void processInternal(final Request<ContractRequestMessageImpl,
            ContractRuleListContainer, Optional<Jws<Claims>>> msg) throws Exception {
        if (msg.getBody().getRules().isEmpty()) {
            throw new MissingRulesException(msg.getBody().getContractRequest(),
                    "Rule list is empty.");
        }
    }

}

/**
 * Validates the target rule map for a contract request, that links target artifacts to a list of
 * rules.
 */
@Component("TargetRuleMapValidator")
class TargetRuleMapValidator extends IdsValidator<Request<ContractRequestMessageImpl,
        ContractTargetRuleMapContainer, Optional<Jws<Claims>>>> {

    /**
     * Validates the target rule map for a contract request, that links target artifacts to a
     * list of rules.
     *
     * @param msg the incoming message.
     * @throws Exception if the target is missing for any rules.
     */
    @Override
    protected void processInternal(final Request<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer, Optional<Jws<Claims>>> msg) throws Exception {
        if (msg.getBody().getTargetRuleMap().containsKey(null)) {
            throw new MissingTargetInRuleException(msg.getBody().getContractRequest(),
                    "Rule is missing a target.");
        }
    }

}

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

        // Abort negotiation if no contract offer for the issuer connector could be found.
        final var validContracts =
                ContractUtils.removeContractsWithInvalidConsumer(contracts, issuer);
        if (validContracts.isEmpty()) {
            throw new ContractListEmptyException(request, "List of valid contracts is empty.");
        }

        try {
            return ruleValidator.validateRulesOfRequest(validContracts, targetRuleMap, target);
        } catch (IllegalArgumentException e) {
            throw new MalformedRuleException("Malformed rule.", e);
        }
    }

}
