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
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.exception.PolicyExecutionException;
import io.dataspaceconnector.service.message.type.NotificationService;
import io.dataspaceconnector.util.RuleUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Executes policy conditions. Refers to the ids policy enforcement point (PEP).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PolicyExecutionService {

    /**
     * Service for ids notification messages.
     */
    private final @NonNull NotificationService notificationService;

    /**
     * Service for sending messages to the clearing house.
     */
    private final @NonNull ClearingHouseService clearingHouseService;

    /**
     * Service for building log messages.
     */
    private final @NonNull LogBuilder logBuilder;

    /**
     * Send contract agreement to clearing house.
     *
     * @param agreement The ids contract agreement.
     */
    public void sendAgreement(final ContractAgreement agreement) {
        clearingHouseService.sendAgreement(agreement);
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
        clearingHouseService.logDataAccess(target, agreementId);
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
        if (rule instanceof Permission) {
            final var postDuty = ((Permission) rule).getPostDuty().get(0);
            final var recipient = RuleUtils.getEndpoint(postDuty);

            notificationService.sendMessage(URI.create(recipient), logBuilder.buildLog(element));
        } else if (log.isWarnEnabled()) {
            log.warn("Reporting data access is only supported for permissions.");
        }
    }
}
