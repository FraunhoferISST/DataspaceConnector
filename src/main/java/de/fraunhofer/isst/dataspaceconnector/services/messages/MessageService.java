package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.ResponseMessage;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageDeserializationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.communication.http.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Abstract class for building, sending, and process ids messages.
 */
@Service
public abstract class MessageService {
    /**
     * The logging service.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    /**
     * The ids http service.
     */
    @Autowired
    private IDSHttpService idsHttpService;

    /**
     * The serializer provider.
     */
    @Autowired
    private SerializerProvider serializer;

    /**
     * The configuration container.
     */
    @Autowired
    private ConfigurationContainer configContainer;

    /**
     * Sends an ids message with header and payload.
     *
     * @param header The ids message header.
     * @param payload   The message payload.
     * @param recipient The message's recipient.
     * @return The http response.
     * @throws MessageException If a header could not be built or the message could not be sent.
     */
    public Map<String, String> sendMessage(final Message header,
                                           final String payload,
                                           final URI recipient) throws MessageException {
        try {
            final var body = InfomodelMessageBuilder.messageWithString(header, payload);
            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (ClaimsException exception) {
            LOGGER.warn("Invalid DAT in incoming message. [exception=({})]",
                    exception.getMessage());
            throw new MessageResponseException("Invalid DAT in incoming message.", exception);
        } catch (FileUploadException | IOException exception) {
            LOGGER.warn("Message could not be sent. [exception=({})]", exception.getMessage());
            throw new MessageNotSentException("Message could not be sent.", exception);
        }
    }

    /**
     * Returns the ids header of a http multipart response.
     *
     * @param header The ids header.
     * @return The response message.
     */
    public ResponseMessage getIdsHeader(final String header) throws MessageDeserializationException {
        try {
            return serializer.getSerializer().deserialize(header, ResponseMessage.class);
        } catch (IOException exception) {
            LOGGER.debug("Message could not be parsed to response. [exception=({})]",
                    exception.getMessage());
            throw new MessageResponseException("Could not deserialize ids response message.");
        }
    }

    /**
     * Check if the outbound model version of the requesting connector is listed in the inbound
     * model versions.
     *
     * @param versionString The outbound model version of the requesting connector.
     * @throws InfoModelVersionNotSupportedException Handled in the {@link MessageExceptionHandler}.
     */
    public void checkForVersionSupport(final String versionString) throws InfoModelVersionNotSupportedException {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();
        boolean versionSupported = false;

        for (final var version : connector.getInboundModelVersion()) {
            if (version.equals(versionString)) {
                versionSupported = true;
                break;
            }
        }

        if (!versionSupported) {
            throw new InfoModelVersionNotSupportedException("InfoModel version not supported.");
        }
    }

    /**
     * Check if the received message is empty.
     *
     * @param message The message.
     */
    public void checkForEmptyMessage(final Message message) {
        if (message == null) {
            throw new MessageEmptyException("The incoming request message cannot be null.");
        }
    }
}
