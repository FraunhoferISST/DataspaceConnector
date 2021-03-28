package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidInputException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactResponseMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageExceptionService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ArtifactResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyEnforcementService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * This @{@link ArtifactRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ArtifactRequestMessageImpl.class)
@RequiredArgsConstructor
public class ArtifactRequestHandler implements MessageHandler<ArtifactRequestMessageImpl> {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionRequestHandler.class);

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageExceptionService exceptionService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for resolving elements and its parents/children.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for connector usage control configurations.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for policy enforcement.
     */
    private final @NonNull PolicyEnforcementService enforcementService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull ArtifactResponseService artifactService;

    /**
     * This message implements the logic that is needed to handle the message. As it returns the
     * input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The request message.
     * @param payload The message payload.
     * @return The response message.
     * @throws RuntimeException If the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(final ArtifactRequestMessageImpl message,
                                         final MessagePayload payload) throws RuntimeException {
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
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(message);
        final var transferContract = MessageUtils.extractTransferContract(message);
        final var issuerConnector = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            // Without a requested artifact, the message processing will be aborted.
            return exceptionService.handleMissingRequestedArtifact(requestedArtifact,
                    transferContract, issuerConnector, messageId);
        }

        // Check agreement only if contract negotiation is turned on.
        final var negotiation = connectorConfig.isPolicyNegotiation();
        if (negotiation) {
            if (transferContract == null || transferContract.toString().equals("")) {
                // Without a transfer contract, the message processing will be aborted.
                return exceptionService.handleMissingTransferContract(requestedArtifact,
                        transferContract, issuerConnector, messageId);
            }

            try {
                checkContractConditions(transferContract, requestedArtifact);
            } catch (ResourceNotFoundException | IllegalArgumentException exception) {
                // Agreement could not be loaded or deserialized.
                return exceptionService.handleMessageProcessingFailed(exception,
                        requestedArtifact, transferContract, issuerConnector, messageId);
            } catch (PolicyRestrictionException exception) {
                // Conditions not fulfilled.
                return exceptionService.handlePolicyRestrictionException(exception,
                        requestedArtifact, transferContract, issuerConnector, messageId);
            } catch (ContractException exception) {
                // Invalid transfer contract.
                return exceptionService.handleInvalidTransferContract(exception, requestedArtifact,
                        transferContract, issuerConnector, messageId);
            }
        }

        // Either without contract negotiation or if all conditions are fulfilled, data is returned.
        try {
            // Process query input.
            final var queryInput = messageService.getQueryInputFromPayload(payload);
            return returnData(requestedArtifact, issuerConnector, messageId, queryInput);
        } catch (InvalidInputException exception) {
            return exceptionService.handleInvalidQueryInput(exception, requestedArtifact,
                    transferContract, issuerConnector, messageId);
        } catch (Exception exception) {
            // Failed to retrieve data. TODO Add further exception handling if necessary.
            return exceptionService.handleFailedToRetrieveData(exception, requestedArtifact,
                    issuerConnector, messageId);
        }
    }

    /**
     * Check if the transfer contract is valid and the conditions are fulfilled.
     *
     * @param transferContract  The id of the contract.
     * @param requestedArtifact The id of the artifact.
     */
    private void checkContractConditions(final URI transferContract, final URI requestedArtifact)
            throws IllegalArgumentException, ResourceNotFoundException,
            PolicyRestrictionException, ContractException {
        // ResourceNotFoundException is handled at a higher level.
        final var agreement = entityResolver.getAgreementByRemoteId(transferContract);
        final var artifacts = dependencyResolver.getArtifactsByAgreement(agreement);

        final var valid = isValidTransferContract(artifacts, requestedArtifact);
        if (!valid) {
            // If the requested artifact does not match the agreement, send rejection message.
            throw new ContractException("Transfer contract does not match the requested artifact.");
        }

        final var value = agreement.getValue();
        final var idsAgreement = deserializationService.getContractAgreement(value);
        enforcementService.checkPolicyOnDataProvision(requestedArtifact, idsAgreement);
    }

    /**
     * Check if the transfer contract matches the requested artifact.
     *
     * @param artifacts         List of artifacts.
     * @param requestedArtifact Id of the requested artifact.
     * @return True if the requested artifact matches the transfer contract's artifacts.
     * @throws ResourceNotFoundException If a resource could not be found.
     */
    private boolean isValidTransferContract(final List<Artifact> artifacts,
                                            final URI requestedArtifact)
            throws ResourceNotFoundException {
        for (final var artifact : artifacts) {
            final var endpoint = EndpointUtils.getSelfLink(artifact);
            if (endpoint.equals(requestedArtifact)) {
                return true;
            }
        }

        // If the requested artifact could not be found in the transfer contract (agreement).
        return false;
    }

    /**
     * Get data by requested artifact and return within an artifact response message.
     *
     * @param requestedArtifact The requested artifact.
     * @param issuerConnector   The issuer connector.
     * @param messageId         The message id.
     * @param queryInput        The query input.
     * @return A message response.
     */
    private MessageResponse returnData(final URI requestedArtifact, final URI issuerConnector,
                                       final URI messageId, final QueryInput queryInput) {
        try {
            final var data = entityResolver.getDataByArtifactId(requestedArtifact, queryInput);

            // Build ids response message.
            final var desc = new ArtifactResponseMessageDesc(messageId, issuerConnector);
            desc.setRecipient(issuerConnector);
            final var header = artifactService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, data);
        } catch (MessageBuilderException | ConstraintViolationException exception) {
            return exceptionService.handleResponseMessageBuilderException(exception);
        }
    }
}
