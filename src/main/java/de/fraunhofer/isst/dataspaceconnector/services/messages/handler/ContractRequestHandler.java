package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageRequestException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRejectionMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRejectionService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyManagementService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This @{@link ContractRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ContractRequestMessageImpl.class)
@RequiredArgsConstructor
public class ContractRequestHandler implements MessageHandler<ContractRequestMessageImpl> {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ContractRequestHandler.class);

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageResponseService exceptionService;

    /**
     * Service for ids contract rejection messages.
     */
    private final @NonNull ContractRejectionService rejectionService;

    /**
     * Service for ids contract agreement messages.
     */
    private final @NonNull ContractAgreementService agreementService;

    /**
     * Service for resolving elements and its parents/children.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for policy management.
     */
    private final @NonNull PolicyManagementService managementService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The request message payload.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final ContractRequestMessageImpl message,
                                         final MessagePayload payload) {
        // Validate incoming message.
        try {
            messageService.validateIncomingRequestMessage(message);
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuerConnector = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        // Read message payload as string.
        String payloadAsString;
        try {
            payloadAsString = MessageUtils.getPayloadAsString(payload);
        } catch (MessageRequestException exception) {
            return exceptionService.handleMessagePayloadException(exception, messageId,
                    issuerConnector);
        }

        // Check the contract's content.
        return checkContractRequest(payloadAsString, messageId, issuerConnector);
    }

    /**
     * Checks if the contract request content by the consumer complies with the contract offer by
     * the provider.
     *
     * @param payload         The message payload containing a contract request.
     * @param messageId       The message id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response to the requesting connector.
     */
    public MessageResponse checkContractRequest(final String payload,
                                                final URI messageId,
                                                final URI issuerConnector) throws RuntimeException {
        try {
            // Deserialize string to contract object.
            final var request = deserializationService.getContractRequest(payload);

            // Get all rules of the contract request.
            final var rules = PolicyUtils.extractRulesFromContract(request);
            if (rules.isEmpty()) {
                // Return rejection message if the contract request is missing rules.
                return exceptionService.handleMissingRules(request, messageId, issuerConnector);
            }

            final var targetRuleMap = PolicyUtils.getTargetRuleMap(rules);
            if (targetRuleMap.containsKey(null)) {
                // Return rejection message if the rules are missing targets.
                return exceptionService.handleMissingTargetInRules(request, messageId,
                        issuerConnector);
            }

            final var targetList = new ArrayList<URI>();
            // Retrieve matching contract offers to compare the content.
            for (final var target : targetRuleMap.keySet()) {
                final List<Contract> contracts;
                try {
                    contracts = dependencyResolver.getContractOffersByArtifactId(target);
                } catch (ResourceNotFoundException exception) {
                    return exceptionService.handleResourceNotFoundException(exception, target,
                            issuerConnector, messageId);
                }

                // Abort negotiation if no contract offer could be found.
                if (contracts.isEmpty()) {
                    return exceptionService.handleMissingContractOffers(request, messageId,
                            issuerConnector);
                }

                // Abort negotiation if no contract offer for the issuer connector could be found.
                final var validContracts =
                        PolicyUtils.removeContractsWithInvalidConsumer(contracts, issuerConnector);
                if (validContracts.isEmpty()) {
                    return exceptionService.handleMissingContractOffers(request, messageId,
                            issuerConnector);
                }

                final var valid = areRulesValid(validContracts, targetRuleMap, target);
                if (!valid) {
                    return rejectContract(issuerConnector, messageId);
                }

                targetList.add(target);
            }

            return acceptContract(request, issuerConnector, messageId, targetList);
        } catch (IllegalArgumentException exception) {
            return exceptionService.handleIllegalArgumentException(exception, payload,
                    issuerConnector, messageId);
        } catch (Exception exception) {
            // NOTE: Should not be reached. TODO Add further exception handling if necessary.
            return exceptionService.handleMessageProcessingFailed(exception, payload,
                    issuerConnector, messageId);
        }
    }

    /**
     * Compare content of rule offer and request with each other.
     *
     * @param contractOffers The contract offer.
     * @param map            The target contract map.
     * @param target         The target value.
     * @return True if everything is fine, false in case of mismatch.
     */
    private boolean areRulesValid(final List<Contract> contractOffers,
                                  final Map<URI, List<Rule>> map,
                                  final URI target) {
        boolean valid = false;
        for (final var contract : contractOffers) {
            // Get rule list from contract offer.
            final var ruleList = dependencyResolver.getRulesByContractOffer(contract);
            // Get rule list from contract request.
            final var values = map.get(target);

            // Compare rules
            if (managementService.compareRulesOfOfferToRequest(ruleList, values)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    /**
     * Accept contract by building a contract agreement and sending it as payload within a
     * contract agreement message.
     *
     * @param request            The contract request object from the data consumer.
     * @param issuerConnector    The issuer connector id.
     * @param correlationMessage The correlation message id.
     * @param targetList         List of requested targets.
     * @return The message response to the requesting connector.
     */
    private MessageResponse acceptContract(final ContractRequest request,
                                           final URI issuerConnector,
                                           final URI correlationMessage,
                                           final List<URI> targetList) {
        ContractAgreement agreement = null;
        URI agreementId;
        try {
            // Turn the accepted contract request into a contract agreement.
            agreement = managementService.buildContractAgreement(request);
            // Save agreement to database.
            agreementId = managementService.saveContractAgreement(agreement, false, targetList);
        } catch (ConstraintViolationException | PersistenceException exception) {
            return exceptionService.handleAgreementPersistenceException(exception, agreement,
                    issuerConnector, correlationMessage);
        }

        try {
            // Build ids response message.
            final var desc = new ContractAgreementMessageDesc(issuerConnector, correlationMessage);
            final var header = agreementService.buildMessage(desc);
            LOGGER.info("Contract request accepted. Saved agreement: " + agreementId);

            // Send ids response message.
            return BodyResponse.create(header, agreement.toRdf());
        } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException
                | RdfBuilderException exception) {
            return exceptionService.handleResponseMessageBuilderException(exception,
                    issuerConnector, correlationMessage);
        }
    }

    /**
     * Builds a contract rejection message with a rejection reason.
     *
     * @param issuerConnector    The issuer connector.
     * @param correlationMessage The correlation message id.
     * @return A contract rejection message.
     */
    private MessageResponse rejectContract(final URI issuerConnector,
                                           final URI correlationMessage) {
        try {
            // Build ids response message.
            final var desc = new ContractRejectionMessageDesc(issuerConnector, correlationMessage);
            final var header = (RejectionMessage) rejectionService.buildMessage(desc);
            final var payload = "Contract rejected.";

            // Send ids response message.
            return ErrorResponse.create(header, payload);
        } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException
                | RdfBuilderException exception) {
            return exceptionService.handleResponseMessageBuilderException(exception,
                    issuerConnector, correlationMessage);
        }
    }
}
