package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Abstract class for building, sending, and processing ids messages.
 */
public abstract class AbstractMessageService<D extends MessageDesc> {
    /**
     * The logging service.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageService.class);

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
     * Service for ids deserialization.
     */
    @Autowired
    private DeserializationService deserializationService;

    /**
     * Build ids message with params.
     *
     * @param desc Type-specific message parameter.
     * @return An ids message.
     * @throws ConstraintViolationException If the ids message could not be built.
     */
    public abstract Message buildMessage(D desc) throws ConstraintViolationException;

    /**
     * Return allowed response message type.
     *
     * @return The response message type class.
     */
    protected abstract Class<?> getResponseMessageType();

    /**
     * Build and sent a multipart message with header and payload.
     *
     * @param desc    Type-specific message parameter.
     * @param payload The message's payload.
     * @return The response as map.
     * @throws MessageException If message building, sending, or processing failed.
     */
    public Map<String, String> sendMessage(final D desc, final String payload) throws MessageException {
        try {
            final var recipient = desc.getRecipient();
            final var header = buildMessage(desc);

            // MessageBuilderException is handled at a higher level.
            final var body = MessageUtils.buildIdsMultipartMessage(header, payload);
            LOGGER.debug("Built request message:" + body); // TODO Add logging house class

            // Send message and return response.
            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (MessageResponseException e) {
            LOGGER.warn("Failed to read ids response message. [exception=({})]", e.getMessage());
            throw new MessageException(ErrorMessages.INVALID_RESPONSE.toString(), e);
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Ids message could not be built. [exception=({})]", e.getMessage());
            throw new MessageException(ErrorMessages.HEADER_BUILD_FAILED.toString(), e);
        } catch (ClaimsException e) {
            LOGGER.debug("Invalid DAT in incoming message. [exception=({})]", e.getMessage());
            throw new MessageException(ErrorMessages.INVALID_RESPONSE_DAT.toString(), e);
        } catch (FileUploadException | IOException e) {
            LOGGER.warn("Message could not be sent. [exception=({})]", e.getMessage());
            throw new MessageException(ErrorMessages.MESSAGE_NOT_SENT.toString(), e);
        }
    }

    /**
     * Checks if the response message is of the right type.
     *
     * @param message The received message response.
     * @return True if the response type is as expected.
     * @throws MessageResponseException If the response could not be read.
     */
    public boolean isValidResponseType(final Map<String, String> message) throws MessageResponseException {
        try {
            // MessageResponseException is handled at a higher level.
            final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
            final var idsMessage = getDeserializer().deserializeResponseMessage(header);

            final var messageType = idsMessage.getClass();
            final var allowedType = getResponseMessageType();
            return messageType.equals(allowedType);
        } catch (MessageResponseException | IllegalArgumentException e) {
            LOGGER.debug("Failed to read response header. [exception=({})]", e.getMessage());
            throw new MessageResponseException(ErrorMessages.MALFORMED_HEADER.toString(), e);
        } catch (Exception e) {
            // NOTE: Should not be reached.
            LOGGER.warn("Something else went wrong. [exception=({})]", e.getMessage());
            throw new MessageResponseException(ErrorMessages.INVALID_RESPONSE.toString(), e);
        }
    }

    /**
     * Getter for ids connector service.
     *
     * @return The service class.
     */
    public IdsConnectorService getConnectorService() {
        return connectorService;
    }

    /**
     * Getter for ids deserialization service.
     *
     * @return The service class.
     */
    public DeserializationService getDeserializer() {
        return deserializationService;
    }
}
