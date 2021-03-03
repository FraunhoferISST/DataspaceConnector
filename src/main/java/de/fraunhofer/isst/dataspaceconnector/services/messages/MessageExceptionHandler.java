package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.MessageResponseBuilderException;
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
}
