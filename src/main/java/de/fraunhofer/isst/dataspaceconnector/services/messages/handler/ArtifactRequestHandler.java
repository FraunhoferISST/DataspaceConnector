package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactResponseMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ArtifactResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.ContractManager;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.DataProvisionVerifier;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.VerificationInput;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.VerificationResult;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.net.URI;

/**
 * This @{@link ArtifactRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@Log4j2
@SupportedMessageType(ArtifactRequestMessageImpl.class)
@RequiredArgsConstructor
public class ArtifactRequestHandler implements MessageHandler<ArtifactRequestMessageImpl> {

    /**
     * Service for building and sending message responses.
     */
    private final @NonNull MessageResponseService responseService;

    /**
     * Service for handling artifact response messages.
     */
    private final @NonNull ArtifactResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for connector usage control configurations.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * The verifier for the data access.
     */
    private final @NonNull DataProvisionVerifier accessVerifier;

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
            messageService.validateIncomingMessage(message);
        } catch (MessageEmptyException exception) {
            return responseService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return responseService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(message);
        final var transferContract = MessageUtils.extractTransferContract(message);
        final var issuer = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            // Without a requested artifact, the message processing will be aborted.
            return responseService.handleMissingRequestedArtifact(requestedArtifact,
                    transferContract, issuer, messageId);
        }

        // Check agreement only if contract negotiation is turned on.
        final var negotiation = connectorConfig.isPolicyNegotiation();
        if (negotiation) {
            if (transferContract == null || transferContract.toString().equals("")) {
                // Without a transfer contract, the message processing will be aborted.
                return responseService.handleMissingTransferContract(requestedArtifact,
                        transferContract, issuer, messageId);
            }

            try {
                final var agreement = contractManager.validateTransferContract(
                        transferContract, requestedArtifact);

                final var input = new VerificationInput(requestedArtifact, issuer, agreement);
                if (accessVerifier.verify(input) == VerificationResult.DENIED) {
                    throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
                }
            } catch (ResourceNotFoundException | IllegalArgumentException exception) {
                // Agreement could not be loaded or deserialized.
                return responseService.handleMessageProcessingFailed(exception,
                        requestedArtifact, transferContract, issuer, messageId);
            } catch (PolicyRestrictionException exception) {
                // Conditions not fulfilled.
                return responseService.handlePolicyRestrictionException(exception,
                        requestedArtifact, transferContract, issuer, messageId);
            } catch (ContractException exception) {
                // Invalid transfer contract.
                return responseService.handleInvalidTransferContract(exception, requestedArtifact,
                        transferContract, issuer, messageId);
            }
        }

        // Either without contract negotiation or if all conditions are fulfilled, data is returned.
        try {
            // Process query input.
            final var queryInput = getQueryInputFromPayload(payload);
            return returnData(requestedArtifact, transferContract, issuer, messageId, queryInput);
        } catch (InvalidInputException exception) {
            return responseService.handleInvalidQueryInput(exception, requestedArtifact,
                    transferContract, issuer, messageId);
        } catch (Exception exception) {
            // Failed to retrieve data.
            return responseService.handleFailedToRetrieveData(exception, requestedArtifact,
                    issuer, messageId);
        }
    }

    /**
     * Get data by requested artifact and return within an artifact response message.
     *
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The id of the transfer contract.
     * @param issuer            The issuer connector.
     * @param messageId         The message id.
     * @param queryInput        The query input.
     * @return A message response.
     */
    private MessageResponse returnData(final URI requestedArtifact, final URI transferContract,
                                       final URI issuer, final URI messageId,
                                       final QueryInput queryInput) {
        try {
            final var data = entityResolver.getDataByArtifactId(requestedArtifact, queryInput);

            // Build ids response message.
            final var desc = new ArtifactResponseMessageDesc(issuer, messageId, transferContract);
            final var header = messageService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, Base64Utils.encodeToString(data.readAllBytes()));
        } catch (MessageBuilderException | ConstraintViolationException | IOException exception) {
            return responseService.handleResponseMessageBuilderException(exception, issuer,
                    messageId);
        }
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
