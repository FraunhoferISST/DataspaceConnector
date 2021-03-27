package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageRequestException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.ids.framework.communication.http.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import okhttp3.MultipartBody;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
     * Extract the ids of an ids message.
     *
     * @param message The ids message.
     * @return The ids of an ids message.
     */
    public static String extractModelVersionFromMessage(final Message message) {
        return message.getModelVersion();
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
     * Check if the outbound model version of the requesting connector is listed in the inbound
     * model versions.
     *
     * @param versionString   The outbound model version of the requesting connector.
     * @param inboundVersions The inbound model version of the current connector.
     * @throws VersionNotSupportedException If the Infomodel version is not supported.
     */
    public static void checkForVersionSupport(final String versionString,
                                              final List<? extends String> inboundVersions)
            throws VersionNotSupportedException {
        boolean versionSupported = false;
        for (final var version : inboundVersions) {
            if (version.equals(versionString)) {
                versionSupported = true;
                break;
            }
        }

        if (!versionSupported) {
            throw new VersionNotSupportedException("Infomodel version not supported.");
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
            throw new MessageBuilderException("Message could not be built.", exception);
        }
    }

    /**
     * Convert response string to map.
     *
     * @param response The http response.
     * @return The multipart map.
     * @throws FileUploadException If string could not be parsed as multipart map.
     */
    public static Map<String, String> responseToMap(final String response) throws FileUploadException {
        return MultipartStringParser.stringToMultipart(response);
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
            throw new MessageResponseException(ErrorMessages.MALFORMED_HEADER.toString(),
                    exception);
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
            throw new MessageResponseException(ErrorMessages.MALFORMED_PAYLOAD.toString(),
                    exception);
        }
    }

    /**
     * Read string from stream.
     *
     * @param payload The message payload as stream.
     * @return The stream's content.
     * @throws IOException If the stream could not be read.
     */
    public static String getStreamAsString(final MessagePayload payload) throws IOException {
        return IOUtils.toString(payload.getUnderlyingInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * Get the payload as string.
     *
     * @param payload The message's payload.
     * @return The payload as string.
     * @throws MessageRequestException If the payload could not be processed.
     */
    public static String getPayloadAsString(final MessagePayload payload)
            throws MessageRequestException {
        if (payload == null) {
            throw new MessageRequestException(ErrorMessages.MISSING_PAYLOAD.toString());
        }

        String content;
        try {
            content = MessageUtils.getStreamAsString(payload);
        } catch (IOException e) {
            throw new MessageRequestException(ErrorMessages.MALFORMED_PAYLOAD.toString(), e);
        }

        // If request is empty, return rejection message.
        if (content.equals("")) {
            throw new MessageRequestException(ErrorMessages.MISSING_PAYLOAD.toString());
        }

        return content;
    }
}
