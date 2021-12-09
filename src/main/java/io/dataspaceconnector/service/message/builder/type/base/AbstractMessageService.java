/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.message.builder.type.base;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.ids.messaging.common.DeserializeException;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.protocol.http.IdsHttpService;
import de.fraunhofer.ids.messaging.protocol.http.ShaclValidatorException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.MessageEmptyException;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.model.message.MessageDesc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for building, sending, and processing ids messages.
 *
 * @param <D> The type of the message description.
 */
@Log4j2
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public abstract class AbstractMessageService<D extends MessageDesc> {

    /**
     * Service for ids communication.
     */
    @Autowired
    private IdsHttpService idsHttpService;

    /**
     * Service for the current connector configuration.
     */

    @Autowired
    private ConnectorService connectorService;

    /**
     * Service for ids deserialization.
     */
    @Autowired
    private DeserializationService deserializer;

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
     * @throws MessageException         if message building, sending, or processing failed.
     * @throws MessageResponseException if response processing failed.
     */
    public Map<String, String> send(final D desc, final Object payload) throws MessageException,
            MessageResponseException {
        try {
            final var recipient = desc.getRecipient();
            final var header = buildMessage(desc);

            final var body = MessageUtils.buildIdsMultipartMessage(header, payload);
            if (log.isDebugEnabled()) {
                log.debug("Built request message. [body=({})]", body);
            }

            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (SerializeException | ConstraintViolationException | IllegalArgumentException e) {
            final var msg = ErrorMessage.MESSAGE_BUILDING_FAILED;
            if (log.isWarnEnabled()) {
                log.warn(msg + " [exception=({})]", e.getMessage());
            }
            throw new MessageException(msg, e);
        } catch (MultipartParseException | DeserializeException | ShaclValidatorException e) {
            final var msg = ErrorMessage.INVALID_MESSAGE;
            if (log.isDebugEnabled()) {
                log.debug(msg + " [exception=({})]", e.getMessage(), e);
            }
            throw new MessageResponseException(msg, e);
        } catch (SocketTimeoutException e) {
            final var msg = ErrorMessage.GATEWAY_TIMEOUT;
            if (log.isDebugEnabled()) {
                log.debug(msg + " [exception=({})]", e.getMessage(), e);
            }
            throw new MessageException(msg, e);
        } catch (ClaimsException e) {
            final var msg = ErrorMessage.INVALID_DAT;
            if (log.isDebugEnabled()) {
                log.debug(msg + " [exception=({})]", e.getMessage(), e);
            }
            throw new MessageException(msg, e);
        } catch (SSLHandshakeException e) {
            final var msg = ErrorMessage.CERTIFICATE_NOT_TRUSTED;
            if (log.isWarnEnabled()) {
                log.warn(msg + " [exception=({})]", e.getMessage());
            }
            throw new MessageException(msg, e);
        } catch (IOException e) {
            final var msg = ErrorMessage.MESSAGE_HANDLING_FAILED;
            if (log.isWarnEnabled()) {
                log.warn(msg + " [exception=({})]", e.getMessage(), e);
            }
            throw new MessageException(msg, e);
        }
    }

    /**
     * Checks if the response message is of the right type.
     *
     * @param message The received message response.
     * @return True if the response type is as expected.
     * @throws MessageResponseException if the ids response could not be read.
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
        } catch (Exception e) {
            final var msg = ErrorMessage.INVALID_MESSAGE;
            if (log.isDebugEnabled()) {
                log.debug(msg + " [exception=({})]", e.getMessage(), e);
            }
            throw new MessageResponseException(msg, e);
        }
    }

    /**
     * If the response message is not of the expected type, message type, rejection reason, and the
     * payload are returned as an object.
     *
     * @param message The ids multipart message as map.
     * @return The object.
     * @throws MessageResponseException if message processing fails.
     */
    public Map<String, Object> getResponseContent(final Map<String, String> message)
            throws MessageResponseException {
        try {
            final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
            final var payload = MessageUtils.extractPayloadFromMultipartMessage(message);

            final var idsMessage = deserializer.getMessage(header);

            return getResponseContent(idsMessage, payload);
        } catch (Exception e) {
            throw new MessageResponseException(e);
        }
    }

    /**
     * If the response message is not of the expected type, message type, rejection reason, and the
     * payload are returned as an object.
     *
     * @param idsMessage The ids message.
     * @param payload    The message's payload.
     * @return The object.
     * @throws MessageResponseException if message processing fails.
     */
    public Map<String, Object> getResponseContent(final Message idsMessage, final Object payload) {
        final var map = new HashMap<String, Object>();
        map.put("type", idsMessage.getClass());

        // If the message is of type exception, add the reason to the response object.
        if (idsMessage instanceof RejectionMessage) {
            final var rejectionMessage = (RejectionMessage) idsMessage;
            map.put("reason", MessageUtils.extractRejectionReason(rejectionMessage));
        }

        map.put("payload", payload);
        return map;
    }

    /**
     * The ids message.
     *
     * @param message The message that should be validated.
     * @throws MessageEmptyException        if the message is empty.
     */
    public void validateIncomingMessage(final Message message) throws MessageEmptyException {
        MessageUtils.checkForEmptyMessage(message);

        final var modelVersion = MessageUtils.extractModelVersion(message);
        final var inboundVersions = connectorService.getInboundModelVersion();
        MessageUtils.checkForVersionSupport(modelVersion, inboundVersions);
    }
}
