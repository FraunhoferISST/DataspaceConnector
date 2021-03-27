package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageRequestException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * This class handles message responses.
 */
@Service
@RequiredArgsConstructor
public class MessageExceptionService {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageExceptionService.class);

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull IdsConnectorService connectorService;

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
     * @param exception Exception that was thrown when checking the Infomodel version.
     * @param version   Infomodel version of incoming message.
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
     * Handles thrown {@link PolicyRestrictionException}.
     *
     * @param exception Exception that was thrown when checking for data access.
     * @return A message response.
     */
    public MessageResponse handlePolicyRestrictionException(final PolicyRestrictionException exception) {
        LOGGER.debug("Policy restriction detected. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                "Policy restriction detected." + exception.getMessage(),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown exceptions when building the response message.
     *
     * @param exception Exception that was thrown when building the response message.
     * @return A message response.
     */
    public MessageResponse handleResponseMessageBuilderException(final Exception exception) {
        LOGGER.warn("Failed to convert ids object to string. [exception=({})]",
                exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
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
     * Handles thrown {@link InvalidResourceException}.
     *
     * @param exception        Exception that was thrown when building the response message.
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidResourceException(final InvalidResourceException exception,
                                                          final URI requestedElement,
                                                          final URI issuerConnector,
                                                          final URI messageId) {
        LOGGER.debug("Element not found. [exception=({}), resourceId=({}), issuer=({}), "
                        + "messageId=({})]",
                exception.getMessage(), requestedElement, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                "The requested element %s could not be found.", requestedElement),
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link MessageRequestException}.
     *
     * @param exception       Exception that was thrown while reading a message's payload.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessagePayloadException(final MessageRequestException exception,
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
        LOGGER.warn("Could not process contract request. [exception=({}), payload=({}), issuer="
                        + "({}), messageId=({})]", exception.getMessage(), payload, issuerConnector,
                messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not process contract request.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }

    /**
     * Handle contract exception.
     *
     * @param agreement       The contract agreement.
     * @param storedAgreement The stored agreement.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleContractException(final ContractAgreement agreement,
                                                   final Agreement storedAgreement,
                                                   final URI issuerConnector,
                                                   final URI messageId) {
        LOGGER.warn("Invalid contract agreement request. [agreement=({}), storedAgreement=({}), "
                        + "issuer=({}), messageId=({})]", agreement, storedAgreement,
                issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Could not process contract request.",
                connectorService.getConnectorId(),
                connectorService.getOutboundModelVersion());
    }
}
