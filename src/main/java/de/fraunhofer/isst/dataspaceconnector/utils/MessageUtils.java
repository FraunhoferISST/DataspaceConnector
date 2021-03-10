package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.Message;

import java.net.URI;

public final class MessageUtils {

    public static URI extractRequestedElementFromMessage(final DescriptionRequestMessage message) {
        return message.getRequestedElement();
    }

    public static URI extractIssuerConnectorFromMessage(final Message message) {
        return message.getIssuerConnector();
    }

    public static URI extractMessageIdFromMessage(final Message message) {
        return message.getId();
    }
}
