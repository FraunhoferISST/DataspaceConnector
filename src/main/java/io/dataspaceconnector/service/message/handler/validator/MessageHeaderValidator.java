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

import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.service.message.builder.type.DescriptionResponseService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates any incoming message by checking whether the message is empty and whether it references
 * an Infomodel version supported by this connector.
 */
@RequiredArgsConstructor
@Component("MessageHeaderValidator")
class MessageHeaderValidator extends IdsValidator<Request<? extends Message, ?,
        Optional<Jws<Claims>>>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Checks whether the message is empty and whether it references an Infomodel version supported
     * by this connector.
     *
     * @param msg the incoming message.
     * @throws Exception if the message is empty or references an unsupported Infomodel version.
     */
    @Override
    protected void processInternal(final Request<? extends Message, ?, Optional<Jws<Claims>>> msg)
            throws Exception {
        messageService.validateIncomingMessage(msg.getHeader());
    }
}
