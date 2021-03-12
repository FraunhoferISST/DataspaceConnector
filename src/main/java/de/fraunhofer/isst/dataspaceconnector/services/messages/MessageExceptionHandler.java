package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.PolicyRestrictionOnDataProvisionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResponseMessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.ConfigurationService;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * This class handles exceptions of type {@link MessageEmptyException}.
 */
@Service
@RequiredArgsConstructor
public class MessageExceptionHandler {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageExceptionHandler.class);

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConfigurationService configService;

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
                exception.getMessage(), configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link InfoModelVersionNotSupportedException}.
     *
     * @param exception Exception that was thrown when checking the Infomodel version.
     * @param version Infomodel version of incoming message.
     * @return A message response.
     */
    public MessageResponse handleInfoModelNotSupportedException(final InfoModelVersionNotSupportedException exception, final String version) {
        LOGGER.debug("Information Model version of requesting connector is not supported. "
                + "[version=({}), exception=({})]", version, exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED,
                exception.getMessage(), configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link PolicyRestrictionOnDataProvisionException}.
     *
     * @param exception Exception that was thrown when checking for data access.
     * @return A message response.
     */
    public MessageResponse handlePolicyRestrictionOnDataProvisionException(final PolicyRestrictionOnDataProvisionException exception) {
        LOGGER.debug("Policy restriction detected. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                "Policy restriction detected." + exception.getMessage(),
                configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link ConstraintViolationException}.
     *
     * @param exception Exception that was thrown when converting an ids object to a rdf string.
     * @return A message response.
     */
    public MessageResponse handleConstraintViolationException(final ConstraintViolationException exception) {
        LOGGER.debug("IDS response message could not be constructed. [exception=({})]",
                exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link ResponseMessageBuilderException}.
     *
     * @param exception Exception that was thrown when building the response message.
     * @return A message response.
     */
    public MessageResponse handleResponseMessageBuilderException(final ResponseMessageBuilderException exception) {
        LOGGER.warn("Failed to convert ids object to string. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link IllegalStateException}.
     *
     * @param exception Exception that was thrown when trying to send the message.
     * @return A message response.
     */
    public MessageResponse handleIllegalStateException(final IllegalStateException exception) {
        LOGGER.debug("Wrong ids message type as response. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Internal server error.", configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link ResourceNotFoundException}.
     *
     * @param exception Exception that was thrown when trying to send the message.
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The message id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleResourceNotFoundException(final ResourceNotFoundException exception,
                                                           final URI requestedElement,
                                                           final URI issuerConnector,
                                                           final URI messageId) {
        LOGGER.debug("Element could not be found. [exception=({}), resourceId=({}), issuer=({}), messageId=({})]",
                exception.getMessage(), requestedElement, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                "The requested element %s could not be found.", requestedElement),
                configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Build a message response telling the requester that the element could not be found.
     *
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The message id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleResourceNotFoundException(final URI requestedElement,
                                                           final URI issuerConnector,
                                                           final URI messageId) {
        LOGGER.debug("Element not found. [resourceId=({}), issuer=({}), messageId=({})]",
                requestedElement, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                "The requested element %s could not be found.", requestedElement),
                configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }

    /**
     * Handles thrown {@link InvalidResourceException}.
     *
     * @param exception Exception that was thrown when building the response message.
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The message id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidResourceException(final InvalidResourceException exception,
                                                          final URI requestedElement,
                                                          final URI issuerConnector,
                                                          final URI messageId) {
        LOGGER.debug("Element not found. [exception=({}), resourceId=({}), issuer=({}), messageId=({})]",
                exception.getMessage(), requestedElement, issuerConnector, messageId);
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                "The requested element %s could not be found.", requestedElement),
                configService.extractIdFromConnector(),
                configService.extractOutboundModelVersionFromConnector());
    }
}
