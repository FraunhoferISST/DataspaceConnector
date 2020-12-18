package de.fraunhofer.isst.dataspaceconnector.message;

import static de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
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

/**
 * This @{@link ContractMessageHandler} handles
 * all incoming messages that have a {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} JsonTypeName annotation. In
 * this example, the received payload is not defined and will be returned immediately. Usually, the
 * payload would be well defined as well, such that it can be deserialized into a proper
 * Java-Object.
 */
@Component
@SupportedMessageType(ContractRequestMessageImpl.class)
public class ContractMessageHandler implements MessageHandler<ContractRequestMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractRequestMessageImpl.class);

    private final TokenProvider tokenProvider;
    private final IdsUtils idsUtils;

    /**
     * Constructor for NotificationMessageHandler.
     *
     * @throws IllegalArgumentException - if one of the parameters is null.
     */
    @Autowired
    public ContractMessageHandler(IdsUtils idsUtils,
        TokenProvider tokenProvider) throws IllegalArgumentException {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        this.tokenProvider = tokenProvider;
        this.idsUtils = idsUtils;
    }

    /**
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
    public MessageResponse handleMessage(ContractRequestMessageImpl message,
        MessagePayload messagePayload) throws RuntimeException {
        try {
            final var connector = idsUtils.getConnector();

            final var responseMsgHeader = new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(tokenProvider.getTokenJWS())
                ._correlationMessage_(message.getId())
                ._issued_(getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                .build();

            LOGGER.debug("Received notification. [id=({}), payload=({})]",
                message.getId(), messagePayload);

            return BodyResponse.create(responseMsgHeader, "Message received.");

        } catch (ConnectorConfigurationException exception) {
            // The connector must be set.
            throw exception;
        } catch (ConstraintViolationException exception) {
            // The response could not be constructed.
            throw new RuntimeException("Failed to construct the response message.", exception);
        }
    }
}
