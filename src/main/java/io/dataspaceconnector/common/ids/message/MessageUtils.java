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
package io.dataspaceconnector.common.ids.message;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import de.fraunhofer.ids.messaging.util.InfomodelMessageBuilder;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.MessageEmptyException;
import io.dataspaceconnector.common.exception.MessageRequestException;
import io.dataspaceconnector.common.util.Utils;
import lombok.extern.log4j.Log4j2;
import okhttp3.MultipartBody;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Class providing common methods for message utility.
 */
@Log4j2
public final class MessageUtils {

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
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractRequestedElement(final DescriptionRequestMessage message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getRequestedElement();
    }

    /**
     * Extract requested artifact from ids artifact request message.
     *
     * @param message The ids message.
     * @return The id of the requested artifact.
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractRequestedArtifact(final ArtifactRequestMessage message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getRequestedArtifact();
    }

    /**
     * Extract transfer contract from ids artifact request message.
     *
     * @param message The ids message.
     * @return The id of the transfer contract.
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractTransferContract(final ArtifactRequestMessage message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getTransferContract();
    }

    /**
     * Extract affected resource from ids resource update message.
     *
     * @param message The ids message.
     * @return The id of the affected resource.
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractAffectedResource(final ResourceUpdateMessage message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getAffectedResource();
    }

    /**
     * Extract issuer connector from incoming ids message.
     *
     * @param message The ids message.
     * @return The issuer connector of an ids message.
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractIssuerConnector(final Message message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getIssuerConnector();
    }

    /**
     * Extract the ids of an ids message.
     *
     * @param message The ids message.
     * @return The ids of an ids message.
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractMessageId(final Message message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getId();
    }

    /**
     * Extract the id of an targeted element.
     *
     * @param message The ids message.
     * @return The id of the target element.
     * @throws IllegalArgumentException If the message is null.
     */
    public static URI extractTargetId(final Message message) {
        final var start = 5;
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        Utils.requireNonNull(message.getProperties(), ErrorMessage.MESSAGE_HANDLING_FAILED);
        final var target =
                message.getProperties().get("https://w3id.org/idsa/core/target").toString();
        return URI.create(target.substring(start, target.length() - 1));
    }

    /**
     * Extract the ids of an ids message.
     *
     * @param message The ids message.
     * @return The ids of an ids message.
     * @throws IllegalArgumentException If the message is null.
     */
    public static String extractModelVersion(final Message message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.getModelVersion();
    }

    /**
     * Extract the rejection reason from an ids rejection message.
     *
     * @param message The ids message.
     * @return The rejection reason.
     * @throws IllegalArgumentException If the message is null.
     */
    public static RejectionReason extractRejectionReason(final RejectionMessage message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
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
     */
    public static void checkForVersionSupport(final String versionString,
                                              final List<? extends String> inboundVersions) {
        boolean versionSupported = false;
        for (final var version : inboundVersions) {
            if (version.equals(versionString)) {
                versionSupported = true;
                break;
            }
        }

        if (!versionSupported) {
            if (log.isDebugEnabled()) {
                log.debug("InfoModel version may be incompatible. [inbound=({}), request=({})]",
                        inboundVersions, versionString);
            }
            // TODO Add as soon as IM version compatibility table exists.
            // throw new VersionNotSupportedException("Information Model version not supported.");
        }
    }

    /**
     * Build http multipart message with a payload and an ids message as header.
     *
     * @param header  The ids message.
     * @param payload The message's payload.
     * @return A multipart body or error.
     * @throws SerializeException if the multipart message could not be built.
     */
    public static MultipartBody buildIdsMultipartMessage(final Message header, final Object payload)
            throws SerializeException {
        return InfomodelMessageBuilder.messageWithString(header, String.valueOf(payload));
    }

    /**
     * Extract the header part from the ids messaging services response.
     *
     * @param message The ids response message as map.
     * @return The ids header.
     * @throws IllegalArgumentException If the message is null.
     */
    public static String extractHeaderFromMultipartMessage(final Map<String, String> message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.get("header");
    }

    /**
     * Extract the payload part from the ids messaging services response.
     *
     * @param message The ids response message as map.
     * @return The ids payload.
     * @throws IllegalArgumentException If the message is null.
     */
    public static String extractPayloadFromMultipartMessage(final Map<String, String> message) {
        Utils.requireNonNull(message, ErrorMessage.MESSAGE_NULL);
        return message.get("payload");
    }

    /**
     * Read string from stream. Does not handle null payloads.
     *
     * @param payload The message payload as stream.
     * @return The stream's content.
     * @throws IllegalArgumentException if the payload is null.
     * @throws IOException              If the stream could not be read.
     */
    public static String getStreamAsString(final MessagePayload payload) throws IOException {
        Utils.requireNonNull(payload, ErrorMessage.MISSING_PAYLOAD);
        Utils.requireNonNull(payload.getUnderlyingInputStream(), ErrorMessage.MISSING_PAYLOAD);
        return new String(payload.getUnderlyingInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
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
            throw new MessageRequestException(ErrorMessage.MISSING_PAYLOAD);
        }

        String content;
        try {
            content = MessageUtils.getStreamAsString(payload);
        } catch (IOException e) {
            throw new MessageRequestException(ErrorMessage.MALFORMED_PAYLOAD, e);
        }

        // If request is empty, return rejection message.
        if (content.isEmpty()) {
            throw new MessageRequestException(ErrorMessage.MISSING_PAYLOAD);
        }

        return content;
    }
}
