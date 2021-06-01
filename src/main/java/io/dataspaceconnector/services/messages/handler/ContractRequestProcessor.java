package io.dataspaceconnector.services.messages.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.springframework.stereotype.Component;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import io.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import io.dataspaceconnector.model.messages.ContractRejectionMessageDesc;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.messages.types.ContractRejectionService;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

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
            // Return rejection message if the contract request is missing rules.
            // return responseService.handleMissingRules(request, messageId, issuer);
        }

        final var targetRuleMap = ContractUtils.getTargetRuleMap(rules);
        if (targetRuleMap.containsKey(null)) {
            // Return rejection message if the rules are missing targets.
            // return responseService.handleMissingTargetInRules(request, messageId, issuer);
        }

        // Retrieve matching contract offers to compare the content.
        for (final var target : targetRuleMap.keySet()) {
            final var valid = checkRule(target, request, messageId, issuer)
            if (!valid) {
                return rejectContract(issuer, messageId);
            }
        }

        return acceptContract(request, issuer, messageId, new ArrayList<>(targetRuleMap.keySet()));
    }

    private boolean checkRule(URI target, final ContractRequest request, final URI messageId, final URI issuer) {
        final List<Contract> contracts;
        try {
            contracts = dependencyResolver.getContractOffersByArtifactId(target);
        } catch (ResourceNotFoundException exception) {
            return responseService.handleResourceNotFoundException(
                    exception, target, issuer, messageId);
        }

        // Abort negotiation if no contract offer could be found.
        if (contracts.isEmpty()) {
            return responseService.handleMissingContractOffers(request, messageId, issuer);
        }

        // Abort negotiation if no contract offer for the issuer connector could be found.
        final var validContracts =
                ContractUtils.removeContractsWithInvalidConsumer(contracts, issuer);
        if (validContracts.isEmpty()) {
            return responseService.handleMissingContractOffers(request, messageId, issuer);
        }

        try {
            return ruleValidator.validateRulesOfRequest(validContracts, targetRuleMap, target);
        } catch (IllegalArgumentException e) {
            return responseService.handleMalformedRules(e, payload, issuer, messageId);
        }
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

        // } catch (ConstraintViolationException | PersistenceException exception) {
        //     return responseService.handleAgreementPersistenceException(exception, agreement,
        //             issuer, messageId);
        // }



        // } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException
        //         | RdfBuilderException e) {
        //     return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        // }
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
        final var body = new JSONObject();
        body.put("status", "rejected");

        // Send ids response message.
        return new Response(header, body.toJSONString());
        // } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException
        //         | RdfBuilderException e) {
        //     return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        // }
    }
}
