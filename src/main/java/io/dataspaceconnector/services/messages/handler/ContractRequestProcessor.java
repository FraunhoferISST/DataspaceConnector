package io.dataspaceconnector.services.messages.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.PersistenceException;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.exceptions.ContractListEmptyException;
import io.dataspaceconnector.exceptions.MissingRulesException;
import io.dataspaceconnector.exceptions.MissingTargetInRuleException;
import io.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import io.dataspaceconnector.model.messages.ContractRejectionMessageDesc;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.messages.types.ContractRejectionService;
import io.dataspaceconnector.services.resources.EntityDependencyResolver;
import io.dataspaceconnector.services.usagecontrol.RuleValidator;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component("ContractRequest")
public class ContractRequestProcessor extends IdsProcessor<RouteMsg<ContractRequestMessageImpl, ContractRequest>> {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Service for ids contract agreement messages.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Service for ids contract rejection messages.
     */
    private final @NonNull ContractRejectionService rejectionService;

    private final @NonNull EntityDependencyResolver dependencyResolver;

    private final @NonNull RuleValidator ruleValidator;

    @Override
    protected Response processInternal(RouteMsg<ContractRequestMessageImpl, ContractRequest> msg) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        return processContractRequest(msg.getBody(), messageId, issuer);
    }

        /**
     * Checks if the contract request content by the consumer complies with the contract offer by
     * the provider.
     *
     * @param payload   The message payload containing a contract request.
     * @param messageId The message id of the incoming message.
     * @param issuer    The issuer connector extracted from the incoming message.
     * @return A message response to the requesting connector.
     */
    public Response processContractRequest(final ContractRequest request, final URI messageId,
                                                  final URI issuer) throws RuntimeException {
        // Get all rules of the contract request.
        final var rules = ContractUtils.extractRulesFromContract(request);
        if (rules.isEmpty()) {
            throw new MissingRulesException(request, "Rule list is empty.");
        }

        final var targetRuleMap = ContractUtils.getTargetRuleMap(rules);
        if (targetRuleMap.containsKey(null)) {
            throw new MissingTargetInRuleException(request, "Rule is missing a target.");
        }

        // Retrieve matching contract offers to compare the content.
        for (final var target : targetRuleMap.keySet()) {
            final var valid = checkRule(target, request, messageId, targetRuleMap);
            if (!valid) {
                return rejectContract(issuer, messageId);
            }
        }

        return acceptContract(request, issuer, messageId, new ArrayList<>(targetRuleMap.keySet()));
    }

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

        return ruleValidator.validateRulesOfRequest(validContracts, targetRuleMap, target);
    }

    /**
     * Accept contract by building a contract agreement and sending it as payload within a
     * contract agreement message.
     *
     * @param request   The contract request object from the data consumer.
     * @param issuer    The issuer connector id.
     * @param messageId The correlation message id.
     * @param targets   List of requested targets.
     * @return The message response to the requesting connector.
     */
    private Response acceptContract(final ContractRequest request, final URI issuer,
                                           final URI messageId, final List<URI> targets)
                                           throws PersistenceException {
        // Turn the accepted contract request into a contract agreement and persist it.
        final var agreement =
                persistenceSvc.buildAndSaveContractAgreement(request, targets, issuer);

        // Build ids response message.
        final var desc = new ContractAgreementMessageDesc(issuer, messageId);
        final var header = agreementSvc.buildMessage(desc);
        if (log.isDebugEnabled()) {
            log.debug("Contract request accepted. [agreementId=({})]", agreement.getId());
        }

        // Send ids response message.
        return new Response(header, agreement.toRdf());
    }

    /**
     * Builds a contract rejection message with a rejection reason.
     *
     * @param issuer    The issuer connector.
     * @param messageId The correlation message id.
     * @return A contract rejection message.
     */
    private Response rejectContract(final URI issuer, final URI messageId) {
        // Build ids response message.
        final var desc = new ContractRejectionMessageDesc(issuer, messageId);
        final var header = (RejectionMessage) rejectionService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, "Contract rejected.");
    }
}
