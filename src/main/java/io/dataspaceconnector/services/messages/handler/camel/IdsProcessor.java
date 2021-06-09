package io.dataspaceconnector.services.messages.handler.camel;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.exceptions.ContractException;
import io.dataspaceconnector.exceptions.ContractListEmptyException;
import io.dataspaceconnector.exceptions.InvalidInputException;
import io.dataspaceconnector.exceptions.MissingRulesException;
import io.dataspaceconnector.exceptions.MissingTargetInRuleException;
import io.dataspaceconnector.exceptions.UnconfirmedAgreementException;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.model.messages.ArtifactResponseMessageDesc;
import io.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import io.dataspaceconnector.model.messages.ContractRejectionMessageDesc;
import io.dataspaceconnector.model.messages.DescriptionResponseMessageDesc;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.services.EntityUpdateService;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.messages.types.ArtifactResponseService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.messages.types.ContractRejectionService;
import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import io.dataspaceconnector.services.resources.EntityDependencyResolver;
import io.dataspaceconnector.services.usagecontrol.PolicyExecutionService;
import io.dataspaceconnector.services.usagecontrol.RuleValidator;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

public abstract class IdsProcessor<I> implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody(processInternal((I)exchange.getIn().getBody(Request.class)));
    }

    protected abstract Response processInternal(I msg) throws Exception;
}

@Component("ResourceDescription")
@RequiredArgsConstructor
class ResourceDescriptionProcessor extends IdsProcessor<RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl, MessagePayload> msg) throws Exception {
        // Read relevant parameters for message processing.
        final var requested = MessageUtils.extractRequestedElement(msg.getHeader());
        final var entity = entityResolver.getEntityById(requested);
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        // If the element has been found, build the ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);
        final var payload = entityResolver.getEntityAsRdfString(entity);

        // Send ids response message.
        return new Response(header, payload);
    }
}

@Component("SelfDescription")
@RequiredArgsConstructor
class SelfDescriptionProcessor extends IdsProcessor<RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var connector = connectorService.getConnectorWithOfferedResources();

        // Build ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, connector.toRdf());
    }
}

@Log4j2
@Component("DataRequestProcessor")
@RequiredArgsConstructor
class DataRequestProcessor extends IdsProcessor<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling artifact response messages.
     */
    private final @NonNull ArtifactResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    @Override
    protected Response processInternal(final RouteMsg<ArtifactRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var requestedArtifact = msg.getHeader().getRequestedArtifact();
        final var issuer = msg.getHeader().getIssuerConnector();
        final var messageId = msg.getHeader().getId();
        final var transferContract = msg.getHeader().getTransferContract();

        final var queryInput = getQueryInputFromPayload(msg.getBody());
        final var data = entityResolver.getDataByArtifactId(requestedArtifact, queryInput);

        final var desc = new ArtifactResponseMessageDesc(issuer, messageId, transferContract);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, Base64Utils.encodeToString(data.readAllBytes()));
    }

    /**
     * Read query parameters from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the query input.
     * @throws InvalidInputException If the query input is not empty but invalid.
     */
    private QueryInput getQueryInputFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("")) {
                // Query input is optional, so no rejection message will be sent. Query input will
                // be checked for null value in HttpService.class.
                return null;
            } else {
                return new ObjectMapper().readValue(payload, QueryInput.class);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid query input. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid query input.", e);
        }
    }

}

@Component("ProcessedNotification")
@RequiredArgsConstructor
class MessageProcessedProcessor extends IdsProcessor<RouteMsg<NotificationMessageImpl, ?>> {

    /**
     * Service for handling message processed notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(RouteMsg<NotificationMessageImpl, ?> msg) throws Exception {
        // Build the ids response.
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, "Message processed.");
    }
}

@Log4j2
@Component("ResourceUpdateProcessor")
@RequiredArgsConstructor
class ResourceUpdateProcessor extends IdsProcessor<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(final RouteMsg<ResourceUpdateMessageImpl, Resource> msg) throws Exception {
        updateService.updateResource(msg.getBody());

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Message received.");
    }

}

@Log4j2
@RequiredArgsConstructor
@Component("ContractRequest")
class ContractRequestProcessor extends IdsProcessor<RouteMsg<ContractRequestMessageImpl, ContractRequest>> {

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
     * @param request   The message payload containing a contract request.
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

@Log4j2
@Component("AgreementComparisonProcessor")
@RequiredArgsConstructor
class AgreementComparisonProcessor extends IdsProcessor<RouteMsg<ContractAgreementMessageImpl, ContractAgreement>> {

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService executionService;

    /**
     * Service for handling notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(RouteMsg<ContractAgreementMessageImpl, ContractAgreement> msg) throws Exception {
        final var agreement = msg.getBody();
        final var storedAgreement = entityResolver.getAgreementByUri(agreement.getId());
        final var storedIdsAgreement = deserializationService
                .getContractAgreement(storedAgreement.getValue());

        if (!ContractUtils.compareContractAgreements(agreement, storedIdsAgreement)) {
            throw new ContractException("Received agreement does not match stored agreement.");
        }

        if (!updateService.confirmAgreement(storedAgreement)) {
            throw new UnconfirmedAgreementException(storedAgreement, "Could not confirm agreement.");
        }

        //TODO move to own processor?
        executionService.sendAgreement(agreement);

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Received contract agreement message.");
    }

}
