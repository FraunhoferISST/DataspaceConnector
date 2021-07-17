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
package io.dataspaceconnector.service.usagecontrol;

import java.net.URI;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.exception.PolicyExecutionException;
import io.dataspaceconnector.exception.RdfBuilderException;
import io.dataspaceconnector.service.message.type.LogMessageService;
import io.dataspaceconnector.util.IdsUtils;
import io.dataspaceconnector.util.UUIDUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;


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
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for ids log messages.
     */
    private final @NonNull LogMessageService logMessageService;

    /**
     * Used for building log messages.
     */
    private final @NonNull LogBuilder logBuilder;

    /**
     * Send contract agreement to clearing house.
     *
     * @param agreement The ids contract agreement.
     */
    public void sendAgreement(final ContractAgreement agreement) {
        try {
            if (isClearingHouseEnabled()) {
                final var clearingHouseAddress = buildClearingHouseDestination(agreement.getId());
                logMessageService.sendMessage(clearingHouseAddress, IdsUtils.toRdf(agreement));
            }
        } catch (PolicyExecutionException | RdfBuilderException | NullPointerException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to send contract agreement to clearing house. "
                         + "[exception=({})]", exception.getMessage());
            }
        }
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param target The target object.
     * @param agreementId The agreement ID.
     * @throws PolicyExecutionException if the access could not be successfully logged.
     */
    public void logDataAccess(final URI target,
                              final URI agreementId) throws PolicyExecutionException {
        try {
            if (isClearingHouseEnabled()) {
                final var clearingHouseAddress = buildClearingHouseDestination(agreementId);
                logMessageService.sendMessage(clearingHouseAddress, logBuilder.buildLog(target));
            }
        } catch (PolicyExecutionException | RdfBuilderException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to send log message to clearing house. [exception=({})]",
                         exception.getMessage());
            }
        }
    }

    /**
     *
     * Creates a LogMessage with the IDS message as payload, then sends to the Clearing House.
     *
     * @param idsMessageHeader the input.
     */
    public void logIdsMessage(final Message idsMessageHeader) {
        final var transferContractID =
            UUIDUtils.uuidFromUri(idsMessageHeader.getTransferContract());
        if (isClearingHouseEnabled()) {
            try {
                final var clearingHouseAddress =
                     buildClearingHouseDestination(URI.create(transferContractID.toString()));
                logMessageService.sendMessage(clearingHouseAddress, idsMessageHeader.toRdf());
            } catch (PolicyExecutionException exception) {
                if (log.isWarnEnabled()) {
                    log.warn("Failed to send log message to clearing house. [exception=({})]",
                             exception.getMessage());
                }
            }
        }
    }

    private boolean isClearingHouseEnabled() {
        return !connectorConfig.getClearingHouse().toString().isBlank();
    }

    private URI buildClearingHouseDestination(final URI agreementId) {
        final var clearingHouse = connectorConfig.getClearingHouse();
        final var uriBuilder = UriComponentsBuilder.fromHttpUrl(clearingHouse.toString());
        uriBuilder.pathSegment(UUIDUtils.uuidFromUri(agreementId).toString());
        return uriBuilder.build().toUri();
    }
}
