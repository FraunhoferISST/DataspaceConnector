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

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.InvalidAffectedResourceException;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates that the correct resource ID was used in a ResourceUpdateMessage.
 */
@Component("CorrectAffectedResourceValidator")
class CorrectAffectedResourceValidator extends IdsValidator<Request<ResourceUpdateMessageImpl,
        Resource, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the resource id given in a ResourceUpdateMessage matches the resource ID in
     * the message's payload.
     *
     * @param msg the incoming message.
     * @throws Exception if the IDs do not match.
     */
    @Override
    protected void processInternal(final Request<ResourceUpdateMessageImpl, Resource,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(msg.getHeader());
        if (!msg.getBody().getId().equals(affected)) {
            throw new InvalidAffectedResourceException("Resource in message payload does not "
                    + "match affected resource from message header.");
        }
    }

}
