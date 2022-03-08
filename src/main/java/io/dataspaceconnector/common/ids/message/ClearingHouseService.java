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
package io.dataspaceconnector.common.ids.message;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.PolicyExecutionException;
import io.dataspaceconnector.common.exception.UUIDFormatException;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.model.message.ProcessCreationMessageDesc;
import io.dataspaceconnector.service.message.builder.type.LogMessageService;
import io.dataspaceconnector.service.message.builder.type.ProcessCreationRequestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
     * The clearing house API path for creating processes.
     */
    @Value("${clearing.house.path.process:}")
    private String processPath;

    /**
     * The clearing house API path for logging messages.
     */
    @Value("${clearing.house.path.log:}")
    private String logPath;

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Service for ids log messages.
     */
    private final @NonNull LogMessageService logMessageSvc;

    /**
     * Service for ids request messages.
     */
    private final @NonNull ProcessCreationRequestService requestService;

    /**
     * Object mapper for mapping to JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    /**
     * Creates a process at the Clearing House using a contract agreement's UUID, where both the
     * provider and the consumer of the agreement are registered as process owners. This is
     * necessary so that both parties are able to log messages under the same process.
     *
     * @param agreement the contract agreement.
     * @param providerFingerprint fingerprint of the provider connector.
     * @param consumerFingerprint fingerprint of the consumer connector.
     * @throws JsonProcessingException if parsing the process owners to JSON format fails.
     */
    public void createProcessAtClearingHouse(final ContractAgreement agreement,
                                             final String providerFingerprint,
                                             final String consumerFingerprint)
            throws JsonProcessingException {
        if (isClearingHouseEnabled()) {
            final var agreementId = UUIDUtils.uuidFromUri(agreement.getId());
            final var url = buildProcessCreationUrl(agreementId);

            final var payload = buildProcessCreationPayload(providerFingerprint,
                    consumerFingerprint);

            final var response = requestService.send(new ProcessCreationMessageDesc(url),
                    objectMapper.writeValueAsString(payload));

            if (!requestService.isValidResponseType(response)) {
                throw new MessageResponseException("Received unexpected response message type from"
                        + " the Clearing House.");
            }
        }
    }

    /**
     * Builds the URL for creating a new process at the Clearing House. The given process ID is
     * appended to the Clearing House URL and the path for process creation.
     *
     * @param id the process ID.
     * @return the URL.
     */
    private URI buildProcessCreationUrl(final UUID id) {
        final var clearingHouse = connectorConfig.getClearingHouse();
        final var uriBuilder = UriComponentsBuilder
                .fromHttpUrl(clearingHouse.toString());
        uriBuilder.pathSegment(processPath, id.toString());
        return uriBuilder.build().toUri();
    }

    /**
     * Builds the payload for creating a new process at the Clearing House. Both the provider and
     * the consumer of an agreement are added as the process owners, so that both may log using
     * the same ID.
     *
     * @param providerFingerprint the provider fingerprint.
     * @param consumerFingerprint the consumer fingerprint.
     * @return the payload.
     */
    private JSONObject buildProcessCreationPayload(final String providerFingerprint,
                                                   final String consumerFingerprint) {
        final var list = Arrays.asList(providerFingerprint, consumerFingerprint);
        final var payload = new JSONObject();
        payload.put("owners", list);
        return payload;
    }

    private boolean isClearingHouseEnabled() {
        return !connectorConfig.getClearingHouse().toString().isBlank();
    }

    private URI buildDestination(final URI agreementId) {
        final var clearingHouse = connectorConfig.getClearingHouse();
        final var uriBuilder = UriComponentsBuilder.fromHttpUrl(clearingHouse.toString());
        uriBuilder.pathSegment(logPath, UUIDUtils.uuidFromUri(agreementId).toString());
        return uriBuilder.build().toUri();
    }
}
