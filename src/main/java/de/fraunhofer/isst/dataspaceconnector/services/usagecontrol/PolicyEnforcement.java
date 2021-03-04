package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * This class implements automated policy check and usage control enforcement.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
public class PolicyEnforcement {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyEnforcement.class);

    /**
     * Service for verifying policies.
     */
    private final @NonNull PolicyVerifier policyVerifier;

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull PolicyConfiguration policyConfig;

    /**
     * Service for contract handling.
     */
    private final @NonNull IdsContractService contractService;

    /**
     * Service for handling requested resources.
     */
    private final @NonNull ResourceService<RequestedResource, ?> resourceService;

    /**
     * The amount of delay for the scheduler.
     */
    private static final int FIXED_DELAY = 60000;


    /**
     * Periodically (every minute) calls {@link PolicyEnforcement#checkResources()}.
     */
    @Scheduled(fixedDelay = FIXED_DELAY)
    public void schedule() {
        try {
            if (policyConfig.getUcFramework()
                    == PolicyConfiguration.UsageControlFramework.INTERNAL) {
                checkResources();
            }
        } catch (ParseException | IOException exception) {
            LOGGER.warn("Failed to check policy. [exception=({})]", exception.getMessage());
        }
    }

    /**
     * Checks all known resources and their policies to delete them if necessary.
     *
     * @throws java.text.ParseException If a date from a policy cannot be parsed.
     * @throws java.io.IOException      If an error occurs while deserializing a contract.
     */
    public void checkResources() throws ParseException, IOException {
        LOGGER.info("Check contracts...");

        for (final var resource : resourceService.getAll(Pageable.unpaged())) {
            final var contracts = resourceService.get(resource.getId()).getContracts();
            for (final var contract : contracts) {
                final var rules = contract.getRules();
                for (final var rule : rules) {
                    if (checkRule(rule.getValue())) {
                        resourceService.delete(resource.getId());
                    }
                }
            }
        }
    }

    /**
     * Check rule for post duties.
     *
     * @param policy The rule string.
     * @return True if resource should be deleted, false if not.
     * @throws IOException    If the policy could not be deserialized.
     * @throws ParseException If the policy could not be checked.
     */
    public boolean checkRule(final String policy) throws IOException, ParseException {
        final var rule = contractService.deserializeRule(policy);

        final var postDuties = ((Permission) rule).getPostDuty();
        if (postDuties != null && !postDuties.isEmpty()) {
            return checkDuty(postDuties);
        }
        return false;
    }

    /**
     * Check duty for deletion.
     *
     * @param duties The post duty list.
     * @return True if resource should be deleted, false if not.
     * @throws ParseException If the policy could not be checked.
     */
    public boolean checkDuty(final ArrayList<? extends Duty> duties) throws ParseException {
        for (final var duty : duties) {
            for (final var action : duty.getAction()) {
                if (action == Action.DELETE) {
                    return policyVerifier.checkForDelete(duty);
                }
            }
        }
        return false;
    }
}
