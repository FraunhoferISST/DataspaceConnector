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
package io.dataspaceconnector.service.usagecontrol;

import io.dataspaceconnector.common.ids.policy.ContractUtils;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.ids.policy.UsageControlFramework;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * This class implements automated policy check.
 */
@EnableScheduling
@Log4j2
@RequiredArgsConstructor
@Service
public class ScheduledDataRemoval {

    /**
     * The delay of the scheduler.
     */
    private static final int FIXED_DELAY = 60_000;

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for updating artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Periodically checks agreements for data deletion.
     */
    @Scheduled(fixedDelay = FIXED_DELAY)
    public void schedule() {
        try {
            if (connectorConfig.getUcFramework() == UsageControlFramework.INTERNAL) {
                if (log.isInfoEnabled()) {
                    log.info("Scanning agreements...");
                }
                scanAgreements();
            }
        } catch (IllegalArgumentException | DateTimeParseException | ResourceNotFoundException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to check policy. [exception=({})]", e.getMessage());
            }
        }
    }

    /**
     * Checks all known agreements for artifacts that have to be deleted.
     *
     * @throws DateTimeParseException    If a date from a policy cannot be parsed.
     * @throws IllegalArgumentException  If the rule could not be deserialized.
     * @throws ResourceNotFoundException If the data could not be deleted.
     */
    private void scanAgreements() throws DateTimeParseException, IllegalArgumentException,
            ResourceNotFoundException {
        for (final var agreement : agreementService.getAll(Pageable.unpaged())) {
            final var value = agreement.getValue();
            final var idsAgreement = deserializationService.getContractAgreement(value);
            for (final var rule : ContractUtils.extractRulesFromContract(idsAgreement)) {
                if (RuleUtils.checkRuleForPostDuties(rule)) {
                    final var artifactId = artifactService.identifyByRemoteId(rule.getTarget());
                    if (artifactId.isPresent() && !isDataDeleted(artifactId.get())) {
                        removeDataFromArtifact(artifactId.get());
                    }
                }
            }
        }
    }

    /**
     * Check if an artifact already has the status deleted.
     *
     * @param artifactId The artifact uuid.
     * @return True if the artifact data is deleted, false otherwise.
     */
    private boolean isDataDeleted(final UUID artifactId) {
        return artifactService.isDataDeleted(artifactId);
    }

    /**
     * Delete data by artifact id.
     *
     * @param artifactId The artifact uuid.
     */
    private void removeDataFromArtifact(final UUID artifactId) {
        try {
            artifactService.setData(artifactId, InputStream.nullInputStream());
            if (log.isDebugEnabled()) {
                log.debug("Removed data from artifact. [id=({})]", artifactId);
            }
        } catch (IOException | ResourceNotFoundException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to remove data from artifact. [id=({}), exception=({})]",
                        artifactId, e.getMessage());
            }
        }
    }
}
