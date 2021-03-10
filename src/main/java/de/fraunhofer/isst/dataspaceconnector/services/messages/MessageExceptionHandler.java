package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResponseMessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageResponseBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.PolicyRestrictionOnDataProvisionException;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class handles exceptions of type {@link MessageEmptyException}.
 */
@RequiredArgsConstructor
public class MessageExceptionHandler {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageExceptionHandler.class);

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * Handles thrown {@link MessageEmptyException}.
     *
     * @param exception Exception that was thrown when checking if the message is null.
     */
    @ExceptionHandler(MessageEmptyException.class)
    public void handleMessageEmptyException(final MessageEmptyException exception) {
        LOGGER.warn("Cannot respond when there is no request. [exception=({})]",
                exception.getMessage());
        throw new IllegalArgumentException(exception.getMessage());
    }

    /**
     * Handles thrown {@link InfoModelVersionNotSupportedException}.
     *
     * @param exception Exception that was thrown when checking the Infomodel version.
     * @return A message response.
     */
    @ExceptionHandler(InfoModelVersionNotSupportedException.class)
    public MessageResponse handleInfoModelNotSupportedException(final InfoModelVersionNotSupportedException exception) {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        LOGGER.debug("Information Model version of requesting connector is not supported. "
                + "[exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED,
                exception.getMessage(), connector.getId(), connector.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link MessageResponseBuilderException}.
     *
     * @param exception Exception that was thrown when building a response in a message handler.
     * @return A message response.
     */
    @ExceptionHandler(MessageResponseBuilderException.class)
    public MessageResponse handleMessageResponseBuilderException(final MessageResponseBuilderException exception) {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        LOGGER.debug("IDS response message could not be constructed. [exception=({})]",
                exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.", connector.getId(),
                connector.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link PolicyRestrictionOnDataProvisionException}.
     *
     * @param exception Exception that was thrown when checking for data access.
     * @return A message response.
     */
    @ExceptionHandler(PolicyRestrictionOnDataProvisionException.class)
    public MessageResponse handlePolicyRestrictionOnDataProvisionException(final PolicyRestrictionOnDataProvisionException exception) {
        // Get a local copy of the current connector.
        final var connector = configurationContainer.getConnector();

        LOGGER.debug("Policy restriction detected. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                "Policy restriction detected." + exception.getMessage(),
                connector.getId(), connector.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link ConstraintViolationException}.
     *
     * @param exception Exception that was thrown when converting an ids object to a rdf string.
     * @return A message response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public MessageResponse handleConstraintViolationException(final ConstraintViolationException exception) {
        // Get a local copy of the current connector.
        final var connector = configurationContainer.getConnector();

        LOGGER.warn("Failed to convert ids object to string. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link ResponseMessageBuilderException}.
     *
     * @param exception Exception that was thrown when building the response message.
     * @return A message response.
     */
    @ExceptionHandler(ResponseMessageBuilderException.class)
    public MessageResponse handleConstraintViolationException(final ResponseMessageBuilderException exception) {
        // Get a local copy of the current connector.
        final var connector = configurationContainer.getConnector();

        LOGGER.warn("Failed to convert ids object to string. [exception=({})]", exception.getMessage());
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
    }
}
