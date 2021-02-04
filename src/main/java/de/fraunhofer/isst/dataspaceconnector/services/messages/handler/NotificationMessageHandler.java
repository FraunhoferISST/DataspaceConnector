package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This @{@link NotificationMessageHandler} handles
 * all incoming messages that have a {@link de.fraunhofer.iais.eis.NotificationMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link de.fraunhofer.iais.eis.NotificationMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(NotificationMessageImpl.class)
public class NotificationMessageHandler implements MessageHandler<NotificationMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageHandler.class);

    private final NotificationMessageService messageService;
    private final ConfigurationContainer configurationContainer;

    /**
     * Constructor for NotificationMessageHandler.
     *
     * @param configurationContainer The container with the configuration
     * @param notificationMessageService The service responsible for notifications
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public NotificationMessageHandler(ConfigurationContainer configurationContainer,
        NotificationMessageService notificationMessageService, DapsTokenProvider tokenProvider)
        throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (notificationMessageService == null)
            throw new IllegalArgumentException("The NotificationMessageService cannot be null.");

        this.configurationContainer = configurationContainer;
        this.messageService = notificationMessageService;
    }

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message        The received notification message.
     * @param messagePayload The message notification messages content.
     * @return The response message.
     * @throws RuntimeException                - if the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(NotificationMessageImpl message,
        MessagePayload messagePayload) throws RuntimeException {
        if (message == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Check if version is supported.
        if (!messageService.versionSupported(message.getModelVersion())) {
            LOGGER.debug("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(
                RejectionReason.VERSION_NOT_SUPPORTED,
                "Information model version not supported.",
                connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            // Build response header.
            messageService.setResponseParameters(message.getIssuerConnector(), message.getId());
            return BodyResponse.create(messageService.buildResponseHeader(), "Message received.");
        } catch (ConstraintViolationException | MessageException exception) {
            // The response could not be constructed.
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }
}
