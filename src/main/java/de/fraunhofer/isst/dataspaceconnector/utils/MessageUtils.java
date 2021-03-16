package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.ids.framework.communication.http.InfomodelMessageBuilder;
import okhttp3.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Class providing util methods for message utility.
 */
public final class MessageUtils {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);

    /**
     * Class constructor without params.
     */
    private MessageUtils() {
        // not used
    }

    /**
     * Extract requested element from ids description request message.
     *
     * @param message The ids message.
     * @return The id of the requested element.
     */
    public static URI extractRequestedElementFromMessage(final DescriptionRequestMessage message) {
        return message.getRequestedElement();
    }

    /**
     * Extract issuer connector from incoming ids message.
     *
     * @param message The ids message.
     * @return The issuer connector of an ids message.
     */
    public static URI extractIssuerConnectorFromMessage(final Message message) {
        return message.getIssuerConnector();
    }

    /**
     * Extract the ids of an ids message.
     *
     * @param message The ids message.
     * @return The ids of an ids message.
     */
    public static URI extractMessageIdFromMessage(final Message message) {
        return message.getId();
    }

    /**
     * Extract the rejection reason from an ids rejection message.
     *
     * @param message The ids message.
     * @return The rejection reason.
     */
    public static RejectionReason extractRejectionReasonFromMessage(final RejectionMessage message) {
        return message.getRejectionReason();
    }

    /**
     * Check if the received message is empty.
     *
     * @param message The message.
     * @throws MessageEmptyException If the message is empty.
     */
    public static void checkForEmptyMessage(final Message message) throws MessageEmptyException {
        if (message == null) {
            throw new MessageEmptyException("The incoming request message cannot be null.");
        }
    }

    /**
     * Build http multipart message with a payload and an ids message as header.
     *
     * @param header  The ids message.
     * @param payload The message's payload.
     * @return A multipart body or error.
     * @throws MessageBuilderException If the message could not be built.
     */
    public static MultipartBody buildIdsMultipartMessage(final Message header, final String payload)
            throws MessageBuilderException {
        try {
            return InfomodelMessageBuilder.messageWithString(header, payload);
        } catch (IOException exception) {
            LOGGER.warn("Message could not be built. [exception=({})]", exception.getMessage());
            throw new MessageBuilderException("Message could not be built.");
        }
    }

    /**
     * Extract the header part from the ids framework response.
     *
     * @param message The ids response message as map.
     * @return The ids header.
     * @throws MessageResponseException If the map contains no header property.
     */
    public static String extractHeaderFromMultipartMessage(final Map<String, String> message)
            throws MessageResponseException {
        try {
            return message.get("header");
        } catch (Exception exception) {
            throw new MessageResponseException("Cannot read header.");
        }
    }

    /**
     * Extract the payload part from the ids framework response.
     *
     * @param message The ids response message as map.
     * @return The ids payload.
     * @throws MessageResponseException If the map contains no payload property.
     */
    public static String extractPayloadFromMultipartMessage(final Map<String, String> message)
            throws MessageResponseException {
        try {
            return message.get("payload");
        } catch (Exception exception) {
            throw new MessageResponseException("Cannot read header.");
        }
    }
}
