package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.NotificationMessageHandler} handles all incoming messages
 * that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as part one in the multipart message. This
 * header must have the correct '@type' reference as defined in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl}
 * JsonTypeName annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(NotificationMessageImpl.class)
public class NotificationMessageHandler implements MessageHandler<NotificationMessageImpl> {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageHandler.class);

    /**
     * {@inheritDoc}
     *
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    public MessageResponse handleMessage(NotificationMessageImpl requestMessage, MessagePayload messagePayload) {
        LOGGER.info("USAGE LOGGED: " + messagePayload);
        return null;
    }
}
