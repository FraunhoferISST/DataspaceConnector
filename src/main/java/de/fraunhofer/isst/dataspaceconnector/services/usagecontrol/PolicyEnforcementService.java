package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.config.UsageControlFramework;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.PolicyRestrictionOnDataAccessException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.PolicyRestrictionOnDataProvisionException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;

/**
 * This class implements automated policy check and usage control enforcement. Refers to the ids
 * policy enforcement point (PEP).
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
public class PolicyEnforcementService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyEnforcementService.class);

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * The policy execution point.
     */
    private final @NonNull PolicyDecisionService pdp;

    /**
     * The delay of the scheduler.
     */
    private static final int FIXED_DELAY = 60_000;


    /**
     * Periodically (every minute) calls {@link PolicyDecisionService#scanResources()}.
     */
    @Scheduled(fixedDelay = FIXED_DELAY)
    public void schedule() {
        try {
            if (connectorConfig.getUcFramework() == UsageControlFramework.INTERNAL) {
                LOGGER.info("Scan resources...");
                pdp.scanResources();
            }
        } catch (ParseException | IOException exception) {
            LOGGER.warn("Failed to check policy. [exception=({})]", exception.getMessage());
        }
    }

    /**
     * Policy check on data provision on provider side.
     *
     * @param requestedElement The requested element.
     * @throws PolicyRestrictionOnDataProvisionException If a policy restriction has been detected.
     */
    public void checkPolicyOnDataProvision(final URI requestedElement) throws PolicyRestrictionOnDataProvisionException {
        final var ignorePatterns = connectorConfig.isAllowUnsupported();
        if (!ignorePatterns) {
            switch (connectorConfig.getUcFramework()) {
                case MY_DATA: // Empty on purpose. TODO Needs to be implemented.
                case INTERNAL:
                default:
                    final var allowedPatterns = Arrays.asList(
                            PolicyPattern.PROVIDE_ACCESS,
                            PolicyPattern.PROHIBIT_ACCESS,
                            PolicyPattern.USAGE_DURING_INTERVAL,
                            PolicyPattern.USAGE_UNTIL_DELETION);
                    pdp.checkForDataAccess(allowedPatterns, requestedElement);
                    break;
            }
        }
    }

    /**
     * Policy check on data access on consumer side.
     *
     * @param requestedElement The requested element.
     * @throws PolicyRestrictionOnDataAccessException If a policy restriction has been detected.
     */
    public void checkPolicyOnDataAccess(final URI requestedElement) throws PolicyRestrictionOnDataAccessException {
        final var ignorePatterns = connectorConfig.isAllowUnsupported();
        if (!ignorePatterns) {
            switch (connectorConfig.getUcFramework()) {
                case MY_DATA: // Empty on purpose. TODO Needs to be implemented.
                case INTERNAL:
                default:
                    final var allowedPatterns = Arrays.asList(
                            PolicyPattern.PROVIDE_ACCESS,
                            PolicyPattern.USAGE_DURING_INTERVAL,
                            PolicyPattern.USAGE_UNTIL_DELETION,
                            PolicyPattern.DURATION_USAGE,
                            PolicyPattern.USAGE_LOGGING,
                            PolicyPattern.N_TIMES_USAGE,
                            PolicyPattern.USAGE_NOTIFICATION);
                    pdp.checkForDataAccess(allowedPatterns, requestedElement);
                    break;
            }
        }
    }
}
