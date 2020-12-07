package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.NotificationMessageHandler} handles
 * all incoming messages that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} JsonTypeName annotation. In
 * this example, the received payload is not defined and will be returned immediately. Usually, the
 * payload would be well defined as well, such that it can be deserialized into a proper
 * Java-Object.
 *
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(NotificationMessageImpl.class)
public class NotificationMessageHandler implements MessageHandler<NotificationMessageImpl> {

    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageHandler.class);

    private final TokenProvider tokenProvider;
    private final ConfigurationContainer configurationContainer;

    @Autowired
    public NotificationMessageHandler(@NotNull ConfigurationContainer configurationContainer,
        @NotNull TokenProvider tokenProvider) throws IllegalArgumentException {
        if (tokenProvider == null) {
            throw new IllegalArgumentException("The TokenProvider cannot be null.");
        }

        if (configurationContainer == null) {
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");
        }

        this.tokenProvider = tokenProvider;
        this.configurationContainer = configurationContainer;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message        The received notification message.
     * @param messagePayload The message notification messages content.
     * @return The response message.
     * @throws ConnectorConfigurationException - if no connector is configurated.
     * @throws RuntimeException                - if the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(NotificationMessageImpl message,
        MessagePayload messagePayload) throws RuntimeException {
        try {
            final var connector = getConnector();

            final var responseMsgHeader = new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(tokenProvider.getTokenJWS())
                ._correlationMessage_(message.getId())
                ._issued_(getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                .build();

            LOGGER.info(String.format("Received notification from %s with message: %s",
                message.getId(), messagePayload));

            return BodyResponse.create(responseMsgHeader, "Message received.");

        } catch (ConnectorConfigurationException exception) {
            // The connector must be set.
            throw exception;
        } catch (ConstraintViolationException exception) {
            // The response could not be constructed.
            throw new RuntimeException("Failed to construct the response message.", exception);
        }
    }

    private Connector getConnector() throws ConnectorConfigurationException {
        Assert.notNull(configurationContainer, "The config cannot be null.");

        final var connector = configurationContainer.getConnector();
        if (connector == null) {
            // The connector is needed for every answer and cannot be null
            throw new ConnectorConfigurationException("No connector configurated.");
        }

        return connector;
    }
}
