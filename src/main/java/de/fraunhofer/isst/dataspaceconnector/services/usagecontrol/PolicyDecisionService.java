package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.DataAccessInvalidTimeIntervalException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.DataAccessNumberReachedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.TimeInterval;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.List;

/**
 * This class provides policy pattern recognition and calls the {@link
 * de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyInformationService} on data
 * request or access. Refers to the ids policy decision point (PDP).
 */
@Service
@RequiredArgsConstructor
public class PolicyDecisionService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDecisionService.class);

    /**
     * Service for handling requested resources.
     */
    private final @NonNull ResourceService<RequestedResource, ?> resourceService;

    /**
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService pep;

    /**
     * Policy management point.
     */
    private final @NonNull PolicyManagementService pmp;

    /**
     * Policy information point.
     */
    private final @NonNull PolicyInformationService pip;

    private final @NonNull DeserializationService deserializationService;

    /**
     * Checks all known resources and their policies to delete them if necessary.
     *
     * @throws java.text.ParseException If a date from a policy cannot be parsed.
     * @throws java.io.IOException      If an error occurs while deserializing a contract.
     */
    public void scanResources() throws ParseException, IOException {
        for (final var resource : resourceService.getAll(Pageable.unpaged())) {
            final var contracts = resourceService.get(resource.getId()).getContracts();
            for (final var contract : contracts) {
                final var rules = contract.getRules();
                for (final var rule : rules) {
                    final var idsRule = deserializationService.deserializeRule(rule.getValue());
                    if (PolicyUtils.checkRuleForPostDuties(idsRule)) {
                        final var resourceId = resource.getId();
                        pep.deleteResource(resourceId);
                    }
                }
            }
        }
    }

    /**
     * Checks the contract content for data access.
     *
     * @param allowedPatterns List of patterns that should be checked.
     * @param element         The requested element.
     * @throws UnsupportedPatternException If no suitable pattern could be found.
     */
    public void checkForDataAccess(final List<PolicyPattern> allowedPatterns, final URI element) {
        // Get the contract agreement's rules for the target.
        final var agreement = pmp.getContractAgreementForRequestedElement(element);
        final var rules = pmp.getRulesForRequestedElement(agreement, element);

        // Check the policy of each rule.
        for (Rule rule : rules) {
            final var pattern = pip.getPatternByRule(rule);
            if (allowedPatterns.contains(pattern)) {
                validatePolicy(pattern, rule, element);
            }
        }
    }

    /**
     * Validates the data access for a given rule.
     *
     * @param pattern The recognized policy pattern.
     * @param rule    The ids rule.
     * @param element The requested/accessed element.
     * @throws PolicyRestrictionException If a policy restriction was detected.
     */
    public void validatePolicy(final PolicyPattern pattern, final Rule rule, final URI element) throws PolicyRestrictionException {
        switch (pattern) {
            case PROVIDE_ACCESS:
                break;
            case USAGE_DURING_INTERVAL:
            case USAGE_UNTIL_DELETION:
                validateInterval(rule);
                break;
            case DURATION_USAGE:
                validateDuration(rule, element);
                break;
            case USAGE_LOGGING:
                pep.logDataAccess(element);
                break;
            case N_TIMES_USAGE:
                validateAccessNumber(rule, element);
                break;
            case USAGE_NOTIFICATION:
                pep.reportDataAccess(rule, element);
                break;
            case PROHIBIT_ACCESS:
            default:
                throw new PolicyRestrictionException("Policy restriction detected.");
        }
    }

    /**
     * Checks if the requested data access is in the allowed time interval.
     *
     * @param rule The ids rule.
     */
    public void validateInterval(final Rule rule) {
        TimeInterval timeInterval;
        try {
            timeInterval = PolicyUtils.getTimeInterval(rule);
        } catch (ParseException exception) {
            LOGGER.warn("Could not read time interval. [exception=({})]", exception.getMessage());
            throw new DataAccessInvalidTimeIntervalException();
        }

        final var current = pip.getCurrentDate();
        if (!current.after(timeInterval.getStart()) || !current.before(timeInterval.getEnd())) {
            throw new DataAccessInvalidTimeIntervalException();
        }
    }

    /**
     * Adds a duration to a given date and checks if the duration has already been exceeded.
     *
     * @param rule    The ids rule.
     * @param element The accessed element.
     */
    public void validateDuration(final Rule rule, final URI element) {
        final var created = pip.getCreationDate(element);

        final Duration duration;
        try {
            duration = PolicyUtils.getDuration(rule);
        } catch (DatatypeConfigurationException exception) {
            LOGGER.warn("Could not read duration. [exception=({})]", exception.getMessage());
            throw new DataAccessInvalidTimeIntervalException();
        }

        if (duration == null) {
            throw new DataAccessInvalidTimeIntervalException();
        }

        final var maxTime = PolicyUtils.getCalculatedDate(created, duration);
        final var validDate = PolicyUtils.checkDate(pip.getCurrentDate(), maxTime);

        if (!validDate) {
            throw new DataAccessInvalidTimeIntervalException();
        }
    }

    /**
     * Checks whether the maximum number of accesses has already been reached.
     *
     * @param rule    The ids rule.
     * @param element The accessed element.
     */
    public void validateAccessNumber(final Rule rule, final URI element) {
        final var max = PolicyUtils.getMaxAccess(rule);
        // final var endpoint = PolicyUtils.getPipEndpoint(rule);
        // NOTE: might be used later

        final var accessed = pip.getAccessNumber(element);
        if (accessed >= max) {
            throw new DataAccessNumberReachedException();
        }
    }
}
