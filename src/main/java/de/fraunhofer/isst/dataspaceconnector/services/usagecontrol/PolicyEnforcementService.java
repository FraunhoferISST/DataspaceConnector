package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.config.UsageControlFramework;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
@Log4j2
public class PolicyEnforcementService {

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * The policy execution point.
     */
    private final @NonNull PolicyDecisionService decisionService;

    /**
     * The delay of the scheduler.
     */
    private static final int FIXED_DELAY = 60_000;


    /**
     * Periodically (every minute) calls {@link PolicyDecisionService#scanAgreements()}.
     */
    @Scheduled(fixedDelay = FIXED_DELAY)
    public void schedule() {
        try {
            if (connectorConfig.getUcFramework() == UsageControlFramework.INTERNAL) {
                if (log.isInfoEnabled()) {
                    log.info("Scanning agreements for data deletion...");
                }
                decisionService.scanAgreements();
            }
        } catch (IllegalArgumentException | ParseException | ResourceNotFoundException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to check policy. [exception=({})]", e.getMessage());
            }
        }
    }

    /**
     * Policy check on data provision on provider side.
     *
     * @param target          The requested element.
     * @param issuerConnector The issuer connector.
     * @param agreement       The ids contract agreement.
     * @throws PolicyRestrictionException If a policy restriction has been detected.
     */
    public void checkPolicyOnDataProvision(final URI target,
                                           final URI issuerConnector,
                                           final ContractAgreement agreement)
            throws PolicyRestrictionException {
        switch (connectorConfig.getUcFramework()) {
            case MY_DATA: // Empty on purpose. TODO Behaviour to be defined and implemented.
            case INTERNAL:
            default:
                final var patternsToCheck = Arrays.asList(
                        PolicyPattern.PROVIDE_ACCESS,
                        PolicyPattern.PROHIBIT_ACCESS,
                        PolicyPattern.USAGE_DURING_INTERVAL,
                        PolicyPattern.USAGE_UNTIL_DELETION,
                        PolicyPattern.CONNECTOR_RESTRICTED_USAGE);
                decisionService.checkForDataAccess(patternsToCheck, target, issuerConnector, agreement);
                break;
        }
    }

    /**
     * Policy check on data access on consumer side. Ignore if unknown patterns are allowed.
     *
     * @param target The requested element.
     * @throws PolicyRestrictionException If a policy restriction has been detected.
     */
    public void checkPolicyOnDataAccess(final URI target) throws PolicyRestrictionException {
        if (!connectorConfig.isAllowUnsupported()) {
            switch (connectorConfig.getUcFramework()) {
                case MY_DATA: // Empty on purpose. TODO Behaviour to be defined and implemented.
                case INTERNAL:
                default:
                    final var patternsToCheck = Arrays.asList(
                            PolicyPattern.PROVIDE_ACCESS,
                            PolicyPattern.USAGE_DURING_INTERVAL,
                            PolicyPattern.USAGE_UNTIL_DELETION,
                            PolicyPattern.DURATION_USAGE,
                            PolicyPattern.USAGE_LOGGING,
                            PolicyPattern.N_TIMES_USAGE,
                            PolicyPattern.USAGE_NOTIFICATION);
                    decisionService.checkForDataAccess(patternsToCheck, target);
                    break;
            }
        }
    }
}
