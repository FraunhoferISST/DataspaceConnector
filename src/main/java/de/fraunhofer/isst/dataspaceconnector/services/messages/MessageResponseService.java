package de.fraunhofer.isst.dataspaceconnector.services.messages;

import java.net.URI;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidInputException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class handles message responses.
 */
@Service
@RequiredArgsConstructor
public class MessageResponseService {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageResponseService.class);

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Handles thrown {@link MessageEmptyException}.
     *
     * @param exception Exception that was thrown when checking if the message is null.
     * @return A message response.
     */
    public MessageResponse handleMessageEmptyException(final MessageEmptyException exception) {
        LOGGER.debug("Cannot respond when there is no request. [exception=({})]",
                exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                exception.getMessage(), connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link VersionNotSupportedException}.
     *
     * @param exception Exception that was thrown when checking the Information Model version.
     * @param version   Information Model version of incoming message.
     * @return A message response.
     */
    public MessageResponse handleInfoModelNotSupportedException(
            final VersionNotSupportedException exception, final String version) {
        LOGGER.debug("Information Model version of requesting connector is not supported. "
                + "[version=({}), exception=({})]", version, exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED,
                exception.getMessage(), connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown exceptions when building the response message.
     *
     * @param exception       Exception that was thrown when building the response message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleResponseMessageBuilderException(final Exception exception,
                                                                 final URI issuerConnector,
                                                                 final URI messageId) {
        LOGGER.warn("Failed to convert ids object to string. [exception=({}), issuer=({}), "
                + "messageId=({})]", exception.getMessage(), issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link PolicyRestrictionException}.
     *
     * @param exception         Exception that was thrown when checking for data access.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handlePolicyRestrictionException(final PolicyRestrictionException exception,
                                                            final URI requestedArtifact,
                                                            final URI transferContract,
                                                            final URI issuerConnector,
                                                            final URI messageId) {
        LOGGER.debug("Policy restriction detected. [exception=({}), artifact=({}), contract=({}), "
                        + "issuer=({}), messageId=({})]", exception.getMessage(), requestedArtifact,
                transferContract, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                "Policy restriction detected." + exception.getMessage(),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link IllegalArgumentException}.
     *
     * @param exception       Exception that was thrown when deserializing a message's payload.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleIllegalArgumentException(final IllegalArgumentException exception,
                                                          final String payload,
                                                          final URI issuerConnector,
                                                          final URI messageId) {
        LOGGER.debug("Could not parse message payload. [exception=({}), payload=({}), issuer=({}), "
                + "messageId=({})]", exception.getMessage(), payload, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Internal server error.", connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown exception while finding the requested element.
     *
     * @param exception        Exception that was thrown when trying to sendMessage the message.
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleResourceNotFoundException(final Exception exception,
                                                           final URI requestedElement,
                                                           final URI issuerConnector,
                                                           final URI messageId) {
        LOGGER.debug("Element not found. [exception=({}), resourceId=({}), issuer=({}), "
                        + "messageId=({})]", exception.getMessage(), requestedElement,
                issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                "The requested element %s could not be found.", requestedElement),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown exceptions in processing a message's payload.
     *
     * @param exception       Exception that was thrown while reading a message's payload.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessagePayloadException(final Exception exception,
                                                         final URI messageId,
                                                         final URI issuerConnector) {
        LOGGER.debug("Failed to read payload. [exception=({}), messageId=({}), issuer=({})]",
                exception.getMessage(), messageId, issuerConnector);
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                exception.getMessage(),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing rules in contract request message.
     *
     * @param request         The contract request.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingRules(final ContractRequest request,
                                              final URI messageId,
                                              final URI issuerConnector) {
        LOGGER.debug("No rules found. [request=({}), messageId=({}), issuer=({})]",
                request, messageId, issuerConnector);
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing rules in contract request.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing target in rules of a contract request.
     *
     * @param request         The contract request.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingTargetInRules(final ContractRequest request,
                                                      final URI messageId,
                                                      final URI issuerConnector) {
        LOGGER.debug("No targets found. [request=({}), messageId=({}), issuer=({})]",
                request, messageId, issuerConnector);
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing targets in rules of contract request.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing contract offers matching the contract request targets.
     *
     * @param request         The contract request.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingContractOffers(final ContractRequest request,
                                                       final URI messageId,
                                                       final URI issuerConnector) {
        LOGGER.debug("No contract offers found. [request=({}), messageId=({}), issuer=({})]",
                request, messageId, issuerConnector);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                "Could not find any matching contract offers for your request.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle global message processing failed.
     *
     * @param exception       Exception that was thrown while processing a request message.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessageProcessingFailed(final Exception exception,
                                                         final String payload,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        LOGGER.warn("Could not process request message. [exception=({}), payload=({}), issuer="
                        + "({}), messageId=({})]", exception.getMessage(), payload, issuerConnector,
                messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not process request message. " + exception.getMessage(),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle global message processing failed.
     *
     * @param exception         Exception that was thrown while processing a request message.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessageProcessingFailed(final Exception exception,
                                                         final URI requestedArtifact,
                                                         final URI transferContract,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        LOGGER.warn("Could not process request message. [exception=({}), artifact=({}), "
                        + "contract=({}), issuer=({}), messageId=({})]", exception.getMessage(),
                requestedArtifact, transferContract, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not process request message. " + exception.getMessage(),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle contract exception.
     *
     * @param exception       The exception that was thrown when validating the contracts.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleContractException(final ContractException exception,
                                                   final String payload,
                                                   final URI issuerConnector,
                                                   final URI messageId) {
        LOGGER.debug("Invalid contract agreement request. [exception=({}), payload=({}), "
                        + "issuer=({}), messageId=({})]", exception, payload, issuerConnector,
                messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "This agreement does not match the one handled out before.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle exceptions when saving a contract agreement.
     *
     * @param exception       Exception that was thrown while storing a contract agreement.
     * @param agreement       The contract agreement.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleAgreementPersistenceException(final Exception exception,
                                                               final ContractAgreement agreement,
                                                               final URI issuerConnector,
                                                               final URI messageId) {
        LOGGER.warn("Could not store contract agreement. [exception=({}), agreement=({}), issuer="
                        + "({}), messageId=({})]", exception.getMessage(), agreement,
                issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not store contract agreement.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing transfer contract in request message.
     *
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingTransferContract(final URI requestedArtifact,
                                                         final URI transferContract,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        LOGGER.debug("Missing transfer contract. [artifact=({}), contract=({}), issuer=({}), "
                        + "messageId=({})]", requestedArtifact, transferContract, issuerConnector,
                messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Missing transfer contract.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle {@link ContractException} because of invalid transfer contract for requested artifact.
     *
     * @param exception         Exception that was thrown while checking the transfer contract.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidTransferContract(final ContractException exception,
                                                         final URI requestedArtifact,
                                                         final URI transferContract,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        LOGGER.debug("Invalid transfer contract. [exception=({}), artifact=({}), contract=({}), "
                        + "issuer=({}), messageId=({})]", exception.getMessage(), requestedArtifact,
                transferContract, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Invalid transfer contract for requested artifact.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing requested artifact in request message.
     *
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingRequestedArtifact(final URI requestedArtifact,
                                                          final URI transferContract,
                                                          final URI issuerConnector,
                                                          final URI messageId) {
        LOGGER.debug("Missing requested artifact. [artifact=({}), contract=({}), issuer=({}), "
                        + "messageId=({})]", requestedArtifact, transferContract, issuerConnector,
                messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Missing requested artifact.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle {@link InvalidInputException} because of an invalid query input in message payload.
     *
     * @param exception         Exception that was thrown while reading the query input.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidQueryInput(final InvalidInputException exception,
                                                   final URI requestedArtifact,
                                                   final URI transferContract,
                                                   final URI issuerConnector,
                                                   final URI messageId) {
        LOGGER.debug("Invalid query input. [exception=({}), artifact=({}), contract=({}), "
                        + "issuer=({}), messageId=({})]", exception.getMessage(), requestedArtifact,
                transferContract, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Invalid query input.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle exceptions when retrieving the data.
     *
     * @param exception         Exception that was thrown while getting the data.
     * @param requestedArtifact The requested artifact.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleFailedToRetrieveData(final Exception exception,
                                                      final URI requestedArtifact,
                                                      final URI issuerConnector,
                                                      final URI messageId) {
        LOGGER.warn("Failed to load data. [exception=({}), artifact=({}), issuer=({}), "
                        + "messageId=({})]", exception.getMessage(), requestedArtifact,
                issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not retrieve data.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing affected resource in request message.
     *
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingAffectedResource(final URI affectedResource,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        LOGGER.debug("Missing affected resource. [resource=({}), issuer=({}), messageId=({})]",
                affectedResource, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Missing affected resource.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle missing payload content in request message.
     *
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingPayload(final URI affectedResource,
                                                final URI issuerConnector,
                                                final URI messageId) {
        LOGGER.debug("Missing resource in payload. [resource=({}), issuer=({}), messageId=({})]",
                affectedResource, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Missing resource in payload.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle mismatch in affected resource and resource id of the incoming payload.
     *
     * @param resourceId       The id of the resource in the payload.
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidAffectedResource(final URI resourceId,
                                                         final URI affectedResource,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        LOGGER.debug("Affected resource does not match the resource id. [resource=({}), "
                        + "affectedResource=({}), issuer=({}), messageId=({})]", resourceId,
                affectedResource, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Affected resource does not match the resource id.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }


    public MessageResponse handleMalformedRules(final IllegalArgumentException exception,
                                                final String payload,
                                                final URI issuerConnector,
                                                final URI messageId) {
        LOGGER.debug("Could not parse message payload. [exception=({}), payload=({}), issuer=({}), "
                     + "messageId=({})]", exception.getMessage(), payload, issuerConnector,
                     messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.MALFORMED_MESSAGE,
                                               "Invalid rules in message payload.",
                                               connectorService.getConnectorId(),
                                               connectorService.getOutboundModelVersion());
    }
}
