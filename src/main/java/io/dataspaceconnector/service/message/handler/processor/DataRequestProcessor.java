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
package io.dataspaceconnector.service.message.handler.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.exception.InvalidInputException;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.model.message.ArtifactResponseMessageDesc;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.builder.type.ArtifactResponseService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.processor.base.IdsProcessor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

/**
 * Fetches the data of an artifact as the response to an ArtifactRequestMessage.
 */
@Log4j2
@Component("DataRequestProcessor")
@RequiredArgsConstructor
class DataRequestProcessor extends IdsProcessor<
        RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling artifact response messages.
     */
    private final @NonNull ArtifactResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Fetches the data of the requested artifact as the response payload and creates an
     * ArtifactResponseMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with an ArtifactResponseMessage as header and the data as payload.
     * @throws Exception if the {@link QueryInput} given in the request's payload is invalid or
     *                   there is an error fetching the data or an error occurs building the
     *                   response.
     */
    @Override
    protected Response processInternal(final RouteMsg<ArtifactRequestMessageImpl,
            MessagePayload> msg) throws Exception {
        final var artifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var transferContract = MessageUtils.extractTransferContract(msg.getHeader());

        final var queryInput = getQueryInputFromPayload(msg.getBody());
        final var data = entityResolver.getDataByArtifactId(artifact, queryInput);

        final var desc = new ArtifactResponseMessageDesc(issuer, messageId, transferContract);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, Base64Utils.encodeToString(data.readAllBytes()));
    }

    /**
     * Read query parameters from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the query input.
     * @throws InvalidInputException If the query input is not empty but invalid.
     */
    private QueryInput getQueryInputFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("") || payload.equals("null")) {
                // Query input is optional, so no rejection message will be sent. Query input will
                // be checked for null value in HttpService.class.
                return null;
            } else {
                return new ObjectMapper().readValue(payload, QueryInput.class);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid query input. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid query input.", e);
        }
    }

}
