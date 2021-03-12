package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.Message;

import java.net.URI;

public final class MessageUtils {

    /**
     * Extract requested element from ids description request message.
     *
     * @param message The incoming ids message.
     * @return The id of the requested element.
     */
    public static URI extractRequestedElementFromMessage(final DescriptionRequestMessage message) {
        return message.getRequestedElement();
    }

    /**
     * Extract issuer connector from incoming ids message.
     *
     * @param message The incoming ids message.
     * @return The issuer connector of an ids message.
     */
    public static URI extractIssuerConnectorFromMessage(final Message message) {
        return message.getIssuerConnector();
    }

    /**
     * Extract the ids of an ids message.
     *
     * @param message The incoming ids message.
     * @return The ids of an ids message.
     */
    public static URI extractMessageIdFromMessage(final Message message) {
        return message.getId();
    }
}
