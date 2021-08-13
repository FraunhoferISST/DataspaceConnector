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

import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.common.exception.PolicyExecutionException;
import io.dataspaceconnector.common.exception.UUIDFormatException;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.service.message.builder.type.LogMessageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Service for communication with the clearing house.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ClearingHouseService {

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Service for ids log messages.
     */
    private final @NonNull LogMessageService logMessageSvc;


    /**
     * Send contract agreement to clearing house.
     *
     * @param agreementId The agreement's id.
     * @param logItem   The item that should be logged.
     */
    public void sendToClearingHouse(final URI agreementId, final Object logItem) {
        if (isClearingHouseEnabled()) {
            final var url = buildDestination(agreementId);
            logMessageSvc.sendMessage(url, logItem);
        }
    }

    /**
     * Creates a LogMessage with the IDS message as payload, then sends to the Clearing House.
     *
     * @param idsMessage the message that should be logged.
     */
    public void logIdsMessage(final Message idsMessage) {
        if (isClearingHouseEnabled()) {
            try {
                final var transferContractId =
                                    UUIDUtils.uuidFromUri(idsMessage.getTransferContract());
                final var url = buildDestination(URI.create(transferContractId.toString()));
                logMessageSvc.sendMessage(url, idsMessage.toRdf());
            } catch (UUIDFormatException | PolicyExecutionException exception) {
                if (log.isWarnEnabled()) {
                    log.warn("Failed to log message to clearing house. [exception=({})]",
                            exception.getMessage());
                }
            }
        }
    }

    private boolean isClearingHouseEnabled() {
        return !connectorConfig.getClearingHouse().toString().isBlank();
    }

    private URI buildDestination(final URI agreementId) {
        final var clearingHouse = connectorConfig.getClearingHouse();
        final var uriBuilder = UriComponentsBuilder.fromHttpUrl(clearingHouse.toString());
        uriBuilder.pathSegment(UUIDUtils.uuidFromUri(agreementId).toString());
        return uriBuilder.build().toUri();
    }
}
