package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for building, sending, and processing ids messages.
 *
 * @param <D> The type of the message description.
 */
@Log4j2
public abstract class AbstractMessageService<D extends MessageDesc> {

    /**
     * Service for ids communication.
     */
    @Autowired
    private IDSHttpService idsHttpService;

    /**
     * Service for the current connector configuration.
     */
    @Autowired
    private ConnectorService connectorService;

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
    public Map<String, String> send(final D desc, final Object payload)
            throws MessageException {
        try {
            final var recipient = desc.getRecipient();
            final var header = buildMessage(desc);

            final var body = MessageUtils.buildIdsMultipartMessage(header, payload);
            if (log.isDebugEnabled()) {
                log.debug("Built request message:" + body); // TODO Add logging house class
            }

            // Send message and return response.
            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (MessageBuilderException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to build ids request message. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new MessageException(ErrorMessages.MESSAGE_BUILD_FAILED.toString(), e);
        } catch (MessageResponseException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to read ids response message. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new MessageException(ErrorMessages.INVALID_RESPONSE.toString(), e);
        } catch (ConstraintViolationException e) {
            if (log.isWarnEnabled()) {
                log.warn("Ids message could not be built. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new MessageException(ErrorMessages.HEADER_BUILD_FAILED.toString(), e);
        } catch (ClaimsException e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid DAT in incoming message. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new MessageException(ErrorMessages.INVALID_RESPONSE_DAT.toString(), e);
        } catch (FileUploadException | IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Message could not be sent. [exception=({})]", e.getMessage(), e);
            }
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
    public boolean isValidResponseType(final Map<String, String> message)
            throws MessageResponseException {
        try {
            // MessageResponseException is handled at a higher level.
            final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
            final var idsMessage = getDeserializer().getMessage(header);

            final var messageType = idsMessage.getClass();
            final var allowedType = getResponseMessageType();
            return messageType.equals(allowedType);
        } catch (MessageResponseException | IllegalArgumentException e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to read response header. [exception=({})]", e.getMessage(), e);
            }
            throw new MessageResponseException(ErrorMessages.MALFORMED_HEADER.toString(), e);
        } catch (Exception e) {
            // NOTE: Should not be reached.
            if (log.isWarnEnabled()) {
                log.warn("Something else went wrong. [exception=({})]", e.getMessage());
            }
            throw new MessageResponseException(ErrorMessages.INVALID_RESPONSE.toString(), e);
        }
    }

    /**
     * The ids message.
     *
     * @param message The message that should be validated.
     * @throws MessageEmptyException        if the message is empty.
     * @throws VersionNotSupportedException if the message version is not supported.
     */
    public void validateIncomingMessage(final Message message) throws MessageEmptyException,
            VersionNotSupportedException {
        MessageUtils.checkForEmptyMessage(message);

        final var modelVersion = MessageUtils.extractModelVersion(message);
        final var inboundVersions = connectorService.getInboundModelVersion();
        MessageUtils.checkForVersionSupport(modelVersion, inboundVersions);
    }

    /**
     * If the response message is not of the expected type, message type, rejection reason, and the
     * payload are returned as an object.
     *
     * @param message The ids multipart message as map.
     * @return The object.
     * @throws MessageResponseException Of the response could not be read or deserialized.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Map<String, Object> getResponseContent(final Map<String, String> message)
            throws MessageResponseException, IllegalArgumentException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(message);

        final var idsMessage = deserializationService.getResponseMessage(header);
        var responseMap = new HashMap<String, Object>() {{
            put("type", idsMessage.getClass());
        }};

        // If the message is of type exception, add the reason to the response object.
        if (idsMessage instanceof RejectionMessage) {
            final var rejectionMessage = (RejectionMessage) idsMessage;
            final var reason = MessageUtils.extractRejectionReason(rejectionMessage);
            responseMap.put("reason", reason);
        }

        responseMap.put("payload", payload);
        return responseMap;
    }

    /**
     * Getter for ids connector service.
     *
     * @return The service class.
     */
    public ConnectorService getConnectorService() {
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
