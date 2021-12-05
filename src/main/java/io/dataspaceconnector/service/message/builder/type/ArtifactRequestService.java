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
package io.dataspaceconnector.service.message.builder.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ArtifactResponseMessageImpl;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.message.ClearingHouseService;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.message.ArtifactRequestMessageDesc;
import io.dataspaceconnector.service.message.builder.type.base.AbstractMessageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

/**
 * Message service for ids artifact request messages.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public final class ArtifactRequestService
        extends AbstractMessageService<ArtifactRequestMessageDesc> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializer;

    /**
     * Clearing House logging utility.
     */
    private final @NonNull ClearingHouseService clearingHouseService;

    /**
     * @throws IllegalArgumentException     if desc is null.
     * @throws ConstraintViolationException if security tokes is null or another error appears
     *                                      when building the message.
     */
    @Override
    public Message buildMessage(final ArtifactRequestMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        Utils.requireNonNull(token, ErrorMessage.DAT_NULL);

        final var recipient = desc.getRecipient();
        final var artifactId = desc.getRequestedArtifact();
        final var contractId = desc.getTransferContract();

        final var message = new ArtifactRequestMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedArtifact_(artifactId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._transferContract_(contractId)
                .build();

        // Log outgoing ArtifactRequestMessages in ClearingHouse
        // Note: Message might not have been sent.
        clearingHouseService.logIdsMessage(message);

        return message;
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return ArtifactResponseMessageImpl.class;
    }

    /**
     * Build and send an artifact request message.
     *
     * @param recipient   The recipient.
     * @param elementId   The requested artifact.
     * @param agreementId The transfer contract.
     * @return The response map.
     * @throws MessageException            if message handling failed.
     * @throws MessageResponseException    if the response could not be processed.
     * @throws UnexpectedResponseException if the response is not as expected.
     */
    public Map<String, String> sendMessage(final URI recipient, final URI elementId,
                                           final URI agreementId)
            throws MessageException, UnexpectedResponseException {
        return sendMessage(recipient, elementId, agreementId, null);
    }

    /**
     * Send artifact request message and then validate the response.
     *
     * @param recipient   The recipient.
     * @param elementId   The requested artifact.
     * @param agreementId The transfer contract.
     * @param queryInput  The query input.
     * @return The response map.
     * @throws MessageException            if message handling failed.
     * @throws MessageResponseException    if the response could not be processed.
     * @throws UnexpectedResponseException if the response is not as expected.
     */
    public Map<String, String> sendMessage(final URI recipient, final URI elementId,
                                           final URI agreementId, final QueryInput queryInput)
            throws MessageException, MessageResponseException, UnexpectedResponseException {
        String payload = "";
        if (queryInput != null) {
            try {
                payload = new ObjectMapper().writeValueAsString(queryInput);
            } catch (JsonProcessingException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Failed to parse query. Loading everything. "
                            + "[exception=({})]", e.getMessage(), e);
                }
            }
        }

        final var desc = new ArtifactRequestMessageDesc(recipient, elementId, agreementId);
        final var response = send(desc, payload);

        try {
            if (!validateResponse(response)) {
                final var content = getResponseContent(response);
                if (log.isDebugEnabled()) {
                    log.debug("Data could not be loaded. [content=({})]", content);
                }
                throw new UnexpectedResponseException(content);
            }
        } catch (MessageResponseException e) {
            final var content = getResponseContent(response);
            if (log.isDebugEnabled()) {
                log.debug("Data could not be loaded. [content=({})]", content);
            }
            throw new UnexpectedResponseException(content);
        }

        // Log response header in the Clearing House
        final var header = MessageUtils.extractHeaderFromMultipartMessage(response);
        final var idsMessage = deserializer.getMessage(header);
        clearingHouseService.logIdsMessage(idsMessage);

        return response;
    }

    /**
     * Check if the response message is of type artifact response.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException if the response could not be read.
     */
    public boolean validateResponse(final Map<String, String> response)
            throws MessageResponseException {
        return isValidResponseType(response);
    }
}
