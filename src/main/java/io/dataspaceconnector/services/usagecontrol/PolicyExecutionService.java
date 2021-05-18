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
package io.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.exceptions.PolicyExecutionException;
import io.dataspaceconnector.exceptions.RdfBuilderException;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.messages.types.LogMessageService;
import io.dataspaceconnector.services.messages.types.NotificationService;
import io.dataspaceconnector.utils.IdsUtils;
import io.dataspaceconnector.utils.RuleUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes policy conditions. Refers to the ids policy enforcement point (PEP).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PolicyExecutionService {

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for ids notification messages.
     */
    private final @NonNull NotificationService notificationService;

    /**
     * Service for ids log messages.
     */
    private final @NonNull LogMessageService logMessageService;

    /**
     * Send contract agreement to clearing house.
     *
     * @param agreement The ids contract agreement.
     */
    public void sendAgreement(final ContractAgreement agreement) {
        try {
            final var rdf = IdsUtils.toRdf(agreement);
            final var recipient = connectorConfig.getClearingHouse();
            if (!recipient.equals(URI.create(""))) {
                logMessageService.sendMessage(recipient, rdf);
            }
        } catch (PolicyExecutionException | RdfBuilderException exception) {
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
     * @throws PolicyExecutionException if the access could not be successfully logged.
     */
    public void logDataAccess(final URI target) throws PolicyExecutionException {
        final var recipient = connectorConfig.getClearingHouse();
        final var logItem = buildLog(target).toString();

        if (!recipient.equals(URI.create(""))) {
            logMessageService.sendMessage(recipient, logItem);
        }
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param rule    The ids rule.
     * @param element The accessed element.
     * @throws PolicyExecutionException If the notification has not been successful.
     */
    public void reportDataAccess(final Rule rule, final URI element)
            throws PolicyExecutionException {
        final var postDuty = ((Permission) rule).getPostDuty().get(0);
        final var recipient = RuleUtils.getEndpoint(postDuty);

        final var logItem = buildLog(element).toString();

        notificationService.sendMessage(URI.create(recipient), logItem);
    }

    /**
     * Build a log information object.
     *
     * @param target The accessed element.
     * @return The log line.
     */
    private Map<String, Object> buildLog(final URI target) {
        final var id = connectorService.getConnectorId();

        return new HashMap<>() {{
            put("target", target);
            put("issuerConnector", id);
            put("accessed", new Date());
        }};
    }
}
