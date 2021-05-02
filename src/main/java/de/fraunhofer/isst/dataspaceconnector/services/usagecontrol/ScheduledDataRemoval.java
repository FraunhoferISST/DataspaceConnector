package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.config.UsageControlFramework;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;

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
    private final @NonNull ConnectorConfiguration connectorConfig;

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
        } catch (IllegalArgumentException | ParseException | ResourceNotFoundException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to check policy. [exception=({})]", e.getMessage());
            }
        }
    }

    /**
     * Checks all known agreements for artifacts that have to be deleted.
     *
     * @throws ParseException            If a date from a policy cannot be parsed.
     * @throws IllegalArgumentException  If the rule could not be deserialized.
     * @throws ResourceNotFoundException If the data could not be deleted.
     */
    public void scanAgreements() throws ParseException, IllegalArgumentException,
            ResourceNotFoundException {
        for (final var agreement : agreementService.getAll(Pageable.unpaged())) {
            final var value = agreement.getValue();
            final var idsAgreement = deserializationService.getContractAgreement(value);
            final var rules = PolicyUtils.extractRulesFromContract(idsAgreement);

            for (final var rule : rules) {
                final var delete = PolicyUtils.checkRuleForPostDuties(rule);
                if (delete) {
                    final var target = rule.getTarget();
                    removeDataFromArtifact(target);
                }
            }
        }
    }
    /**
     * Delete data by artifact id.
     *
     * @param target The artifact id.
     * @throws ResourceNotFoundException If the artifact update fails.
     */
    public void removeDataFromArtifact(final URI target) throws ResourceNotFoundException {
        final var entityId = EndpointUtils.getUUIDFromPath(target);

        // Update data for artifact.
        artifactService.setData(entityId, InputStream.nullInputStream());
        if (log.isDebugEnabled()) {
            log.debug("Deleted data from artifact. [target=({})]", target);
        }
    }
}
