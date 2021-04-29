package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.model.TimeInterval;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;

/**
 * This class provides policy pattern recognition and calls the {@link
 * de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyInformationService} on data
 * request or access. Refers to the ids policy decision point (PDP).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PolicyDecisionService {

    /**
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService executionService;

    /**
     * Policy management point.
     */
    @Autowired
    private final PolicyManagementService managementService;

    /**
     * Policy information point.
     */
    private final @NonNull PolicyInformationService informationService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull AgreementService agreementService;

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
                    executionService.deleteDataFromArtifact(target);
                }
            }
        }
    }

    /**
     * Checks the contract content for data access (on consumer side).
     *
     * @param patterns List of patterns that should be enforced.
     * @param target   The requested element.
     * @throws UnsupportedPatternException If no suitable pattern could be found.
     */
    public void checkForDataAccess(final List<PolicyPattern> patterns, final URI target) {
        // Get the contract agreement's rules for the target.
        final var agreements = managementService.getContractAgreementsByTarget(target);
        for (final var agreement : agreements) {
            final var rules = PolicyUtils.getRulesForTargetId(agreement, target);

            // Check the policy of each rule.
            for (final var rule : rules) {
                final var pattern = informationService.getPatternByRule(rule);
                // Enforce only a set of patterns.
                if (patterns.contains(pattern)) {
                    validatePolicy(pattern, rule, target, null);
                }
            }
        }
    }

    /**
     * Checks the contract content for data access (on provider side).
     *
     * @param patterns        List of patterns that should be enforced.
     * @param target          The requested element.
     * @param issuerConnector The issuer connector.
     * @param agreement       The ids contract agreement.
     * @throws PolicyRestrictionException If a policy restriction has been detected.
     */
    public void checkForDataAccess(final List<PolicyPattern> patterns,
                                   final URI target, final URI issuerConnector,
                                   final ContractAgreement agreement)
            throws PolicyRestrictionException {
        final var rules = PolicyUtils.getRulesForTargetId(agreement, target);

        // Check the policy of each rule.
        for (final var rule : rules) {
            final var pattern = informationService.getPatternByRule(rule);
            // Enforce only a set of patterns.
            if (patterns.contains(pattern)) {
                validatePolicy(pattern, rule, target, issuerConnector);
            }
        }
    }

    /**
     * Validates the data access for a given rule.
     *
     * @param pattern         The recognized policy pattern.
     * @param rule            The ids rule.
     * @param target          The requested/accessed element.
     * @param issuerConnector The issuer connector.
     * @throws PolicyRestrictionException If a policy restriction was detected.
     */
    private void validatePolicy(final PolicyPattern pattern, final Rule rule, final URI target,
                                final URI issuerConnector) throws PolicyRestrictionException {
        switch (pattern) {
            case PROVIDE_ACCESS:
                break;
            case USAGE_DURING_INTERVAL:
            case USAGE_UNTIL_DELETION:
                validateInterval(rule);
                break;
            case DURATION_USAGE:
                validateDuration(rule, target);
                break;
            case USAGE_LOGGING:
                executionService.logDataAccess(target);
                break;
            case N_TIMES_USAGE:
                validateAccessNumber(rule, target);
                break;
            case USAGE_NOTIFICATION:
                executionService.reportDataAccess(rule, target);
                break;
            case CONNECTOR_RESTRICTED_USAGE:
                validateIssuerConnector(rule, issuerConnector);
                break;
            case PROHIBIT_ACCESS:
                throw new PolicyRestrictionException(ErrorMessages.NOT_ALLOWED);
            default:
                if (log.isDebugEnabled()) {
                    log.debug("No pattern detected. [target=({})]", target);
                }
                throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
        }
    }

    /**
     * Checks if the requested data access is in the allowed time interval.
     *
     * @param rule The ids rule.
     * @throws PolicyRestrictionException If the policy could not be read or a restriction is
     *                                    detected.
     */
    private void validateInterval(final Rule rule) throws PolicyRestrictionException {
        TimeInterval timeInterval;
        try {
            timeInterval = PolicyUtils.getTimeInterval(rule);
        } catch (ParseException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not read time interval. [exception=({})]", e.getMessage());
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_INVALID_INTERVAL, e);
        }

        final var current = informationService.getCurrentDate();
        if (!current.isAfter(timeInterval.getStart()) || !current.isBefore(timeInterval.getEnd())) {
            if (log.isWarnEnabled()) {
                log.warn("Invalid time interval. [timeInterval=({})]", timeInterval);
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_INVALID_INTERVAL);
        }
    }

    /**
     * Adds a duration to a given date and checks if the duration has already been exceeded.
     *
     * @param rule   The ids rule.
     * @param target The accessed element.
     * @throws PolicyRestrictionException If the policy could not be read or a restriction is
     *                                    detected.
     */
    private void validateDuration(final Rule rule, final URI target)
            throws PolicyRestrictionException {
        final var created = informationService.getCreationDate(target);

        final Duration duration;
        try {
            duration = PolicyUtils.getDuration(rule);
        } catch (DatatypeConfigurationException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not read duration. [target=({}), exception=({})]",
                        target, e.getMessage(), e);
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_INVALID_INTERVAL, e);
        }

        if (duration == null) {
            if (log.isWarnEnabled()) {
                log.warn("Duration is null. [target=({})]", target);
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_INVALID_INTERVAL);
        }

        final var maxTime = PolicyUtils.getCalculatedDate(created, duration);
        final var validDate = PolicyUtils.checkDate(informationService.getCurrentDate(), maxTime);

        if (!validDate) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid date time. [target=({})]", target);
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_INVALID_INTERVAL);
        }
    }

    /**
     * Checks whether the maximum number of accesses has already been reached.
     *
     * @param rule   The ids rule.
     * @param target The accessed element.
     * @throws PolicyRestrictionException If the access number has been reached.
     */
    private void validateAccessNumber(final Rule rule, final URI target)
            throws PolicyRestrictionException {
        final var max = PolicyUtils.getMaxAccess(rule);
        // final var endpoint = PolicyUtils.getPipEndpoint(rule);
        // NOTE: might be used later

        final var accessed = informationService.getAccessNumber(target);
        if (accessed >= max) {
            if (log.isDebugEnabled()) {
                log.debug("Access number reached. [target=({})]", target);
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_NUMBER_REACHED);
        }
    }

    /**
     * Checks whether the requesting connector corresponds to the allowed connector.
     *
     * @param rule            The ids rule.
     * @param issuerConnector The issuer connector.
     * @throws PolicyRestrictionException If the connector ids do no match.
     */
    private void validateIssuerConnector(final Rule rule, final URI issuerConnector)
            throws PolicyRestrictionException {
        final var allowedConsumer = PolicyUtils.getEndpoint(rule);
        final var allowedConsumerAsUri = URI.create(allowedConsumer);
        if (!allowedConsumerAsUri.equals(issuerConnector)) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid consumer connector. [issuer=({})]", issuerConnector);
            }
            throw new PolicyRestrictionException(ErrorMessages.DATA_ACCESS_INVALID_CONSUMER);
        }
    }
}
