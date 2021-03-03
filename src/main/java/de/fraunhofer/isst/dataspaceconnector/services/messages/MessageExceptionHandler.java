package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.MessageEmptyException;
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
     */
    @ExceptionHandler(MessageEmptyException.class)
    public void handleMessageEmptyException(final MessageEmptyException e) {
        LOGGER.warn("Cannot respond when there is no request.");
        throw new IllegalArgumentException(e.getMessage());
    }

    /**
     * Handles thrown {@link InfoModelVersionNotSupportedException}.
     *
     * @return A message response.
     */
    @ExceptionHandler(InfoModelVersionNotSupportedException.class)
    public MessageResponse handleInfoModelNotSupportedException(final InfoModelVersionNotSupportedException e) {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        LOGGER.debug("Information Model version of requesting connector is not supported.");
        return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED,
                e.getMessage(), connector.getId(), connector.getOutboundModelVersion());
    }
}
