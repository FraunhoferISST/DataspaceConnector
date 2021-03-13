package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.services.ConfigurationService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for building, sending, and process ids messages.
 */
@Service
@RequiredArgsConstructor
public abstract class MessageService {
    /**
     * The logging service.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    /**
     * The ids http service.
     */
    private final @NonNull IDSHttpService idsHttpService;

    /**
     * The configuration container.
     */
    private final @NonNull ConfigurationContainer configContainer;

    /**
     * The configuration container.
     */
    private final @NonNull ConfigurationService configService;

    /**
     * Build ids message with one param.
     *
     * @param recipient The message's recipient.
     * @param params Method parameters.
     * @return An ids message.
     * @throws ConstraintViolationException If the ids message could not be built.
     */
    public abstract Message buildMessage(URI recipient, List<URI> params) throws ConstraintViolationException;

    /**
     * Send ids multipart message.
     *
     * @param recipient The message's recipient.
     * @param params Parameters needed for building the ids message.
     * @param payload The message's payload.
     * @return The response as map.
     * @throws MessageException If message building, sending, or processing failed.
     */
    public Map<String, String> send(final URI recipient,
                                    final List<URI> params,
                                    final String payload) throws MessageException {
        try {
            final var header = buildMessage(recipient, params);
            // MessageException is caught one level higher.
            return sendMessage(header, payload, recipient);
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Ids message header could not be built. [exception=({})]", e.getMessage());
            throw new MessageBuilderException("Ids message header could not be built.", e);
        }
    }

    /**
     * Build and sent a multipart message with header and payload.
     *
     * @param header The ids message header.
     * @param payload   The message payload.
     * @param recipient The message's recipient.
     * @return The http response.
     * @throws MessageException If the message could not be built or sent.
     */
    private Map<String, String> sendMessage(final Message header,
                                     final String payload,
                                     final URI recipient) throws MessageException {
        try {
            // MessageBuilderException is caught one level higher.
            final var body = MessageUtils.buildIdsMultipartMessage(header, payload);
            LOGGER.info(String.valueOf(body));

            // Send message and check response.
            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (ClaimsException exception) {
            LOGGER.debug("Invalid DAT in incoming message. [exception=({})]", exception.getMessage());
            throw new MessageResponseException("Invalid DAT in incoming message.");
        } catch (FileUploadException | IOException exception) {
            LOGGER.warn("Message could not be sent. [exception=({})]", exception.getMessage());
            throw new MessageNotSentException("Message could not be sent.");
        }
    }



    /**
     * Check if the outbound model version of the requesting connector is listed in the inbound
     * model versions.
     *
     * @param versionString The outbound model version of the requesting connector.
     * @throws InfoModelVersionNotSupportedException Handled in the {@link MessageExceptionService}.
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
     * @throws MessageEmptyException If the message is empty.
     */
    public void checkForEmptyMessage(final Message message) throws MessageEmptyException {
        if (message == null) {
            throw new MessageEmptyException("The incoming request message cannot be null.");
        }
    }

    public ConfigurationService getConfigService() {
        return configService;
    }
}
