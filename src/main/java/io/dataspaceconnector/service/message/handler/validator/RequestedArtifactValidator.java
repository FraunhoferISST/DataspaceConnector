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
package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.NoRequestedArtifactException;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates the requested artifact given in an ArtifactRequestMessage.
 */
@Component("RequestedArtifactValidator")
class RequestedArtifactValidator extends IdsValidator<Request<ArtifactRequestMessageImpl,
        MessagePayload, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the requested artifact given in an ArtifactRequestMessage is null or empty.
     *
     * @param msg the incoming message.
     * @throws Exception if the requested artifact is null or empty.
     */
    @Override
    protected void processInternal(final Request<ArtifactRequestMessageImpl, MessagePayload,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            throw new NoRequestedArtifactException("Requested artifact is missing.");
        }
    }

}
