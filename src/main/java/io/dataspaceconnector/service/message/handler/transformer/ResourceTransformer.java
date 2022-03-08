/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.message.handler.transformer;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.exception.DeserializationException;
import io.dataspaceconnector.service.message.handler.exception.MissingPayloadException;
import io.dataspaceconnector.service.message.handler.transformer.base.IdsTransformer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Transforms the payload of a ResourceUpdateMessage from a {@link MessagePayload} to a
 * {@link Resource}.
 */
@Component("ResourceDeserializer")
@RequiredArgsConstructor
class ResourceTransformer extends IdsTransformer<
        Request<ResourceUpdateMessageImpl, MessagePayload, Optional<Jws<Claims>>>,
        RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull
    DeserializationService deserializationService;

    /**
     * Deserializes the payload of a ResourceUpdateMessage to a Resource.
     *
     * @param msg the incoming message.
     * @return a RouteMsg object with the initial header and the Resource as payload.
     * @throws Exception if the payload cannot be read or deserialized.
     */
    @Override
    protected RouteMsg<ResourceUpdateMessageImpl, Resource> processInternal(
            final Request<ResourceUpdateMessageImpl, MessagePayload, Optional<Jws<Claims>>> msg)
            throws Exception {

        final String payloadString;
        try {
            payloadString = MessageUtils.getStreamAsString(msg.getBody());
        } catch (IllegalArgumentException e) {
            throw new MissingPayloadException("Payload is missing from ResourceUpdateMessage.", e);
        }

        if (payloadString.isBlank()) {
            throw new MissingPayloadException("Payload is missing from ResourceUpdateMessage.");
        }

        final Resource resource;
        try {
            resource = deserializationService.getResource(payloadString);
        } catch (IllegalArgumentException e) {
            throw new DeserializationException("Deserialization failed.", e);
        }

        return new Request<>(msg.getHeader(), resource, msg.getClaims());
    }

}
