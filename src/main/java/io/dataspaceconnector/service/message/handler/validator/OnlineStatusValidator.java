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
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.model.configuration.ConnectorStatus;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.ConnectorOfflineException;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Before validating an incoming message, the online status of the connector is checked. If the
 * configuration has connectorStatus == OFFLINE, the connector responds with a rejection message.
 */
@RequiredArgsConstructor
@Component("OnlineStatusValidator")
class OnlineStatusValidator extends IdsValidator<Request<? extends Message, ?,
        Optional<Jws<Claims>>>> {

    /**
     * Service for connector configurations.
     */
    private final @NonNull ConnectorService connectorSvc;

    /**
     * Checks whether the connector is online or offline.
     *
     * @param msg the incoming message.
     * @throws Exception if the status was set to offline.
     */
    @Override
    protected void processInternal(final Request<? extends Message, ?, Optional<Jws<Claims>>> msg)
            throws Exception {
        if (connectorSvc.getConnectorStatus() != ConnectorStatus.ONLINE) {
            throw new ConnectorOfflineException();
        }
    }
}
