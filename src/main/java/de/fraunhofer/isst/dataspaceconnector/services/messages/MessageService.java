package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Abstract class for building, sending, and processing ids messages.
 */
public abstract class MessageService<T extends MessageDesc> {
    /**
     * The logging service.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    /**
     * Service for ids communication.
     */
    @Autowired
    private IDSHttpService idsHttpService;

    /**
     * Service for the current connector configuration.
     */
    @Autowired
    private IdsConnectorService connectorService;

    /**
     * Build ids message with params.
     *
     * @param desc Type-specific message parameter.
     * @return An ids message.
     * @throws ConstraintViolationException If the ids message could not be built.
     */
    public abstract Message buildMessage(T desc) throws ConstraintViolationException;

    /**
     * Build and sent a multipart message with header and payload.
     *
     * @param recipient The message's recipient.
     * @param desc    Type-specific message parameter.
     * @param payload The message's payload.
     * @return The response as map.
     * @throws MessageException If message building, sending, or processing failed.
     */
    public Map<String, String> sendMessage(final URI recipient, final T desc, final String payload)
            throws MessageException {
        try {
            final var header = buildMessage(desc);
            final var body = MessageUtils.buildIdsMultipartMessage(header, payload);
            LOGGER.info(String.valueOf(body));

            // Send message and check response.
            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Ids message header could not be built. [exception=({})]", e.getMessage());
            throw new MessageBuilderException("Ids message header could not be built.");
        } catch (ClaimsException exception) {
            LOGGER.debug("Invalid DAT in incoming message. [exception=({})]",
                    exception.getMessage());
            throw new MessageResponseException("Invalid DAT in incoming message.");
        } catch (FileUploadException | IOException exception) {
            LOGGER.warn("Message could not be sent. [exception=({})]", exception.getMessage());
            throw new MessageNotSentException("Message could not be sent.");
        }
    }

    /**
     * Getter for ids connector service.
     *
     * @return The service class
     */
    public IdsConnectorService getConnectorService() {
        return connectorService;
    }
}
