package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.TimeInterval;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class provides policy pattern recognition and calls the {@link
 * de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyInformationService} on data
 * request or access. Refers to the ids policy decision point (PDP).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class RuleValidator {

    /**
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService executionService;

    /**
     * Policy information point.
     */
    private final @NonNull PolicyInformationService informationService;

    /**
     * Service for resolving elements and its parents/children.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Validates the data access for a given rule.
     *
     * @param pattern         The recognized policy pattern.
     * @param rule            The ids rule.
     * @param target          The requested/accessed element.
     * @param issuerConnector The issuer connector.
     * @throws PolicyRestrictionException If a policy restriction was detected.
     */
    void validatePolicy(final PolicyPattern pattern, final Rule rule, final URI target,
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
     * Compare content of rule offer and request with each other.
     *
     * @param contractOffers The contract offer.
     * @param map            The target contract map.
     * @param target         The target value.
     * @return True if everything is fine, false in case of mismatch.
     */
    public boolean validateRulesOfRequest(final List<Contract> contractOffers,
                                          final Map<URI, List<Rule>> map,
                                          final URI target) {
        boolean valid = false;
        for (final var contract : contractOffers) {
            // Get rule list from contract offer.
            final var ruleList = dependencyResolver.getRulesByContractOffer(contract);
            // Get rule list from contract request.
            final var values = map.get(target);

            // Compare rules
            if (compareRulesOfOfferToRequest(ruleList, values)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    /**
     * Compare rule list of a contract offer to the rule list of a contract request.
     *
     * @param offerRules   List of ids rules.
     * @param requestRules List of ids rules.
     * @return True if the lists are equal, false if not.
     */
    private boolean compareRulesOfOfferToRequest(final List<ContractRule> offerRules,
                                                final List<Rule> requestRules) {
        final var idsRuleList = new ArrayList<Rule>();
        for (final var rule : offerRules) {
            final var value = rule.getValue();
            final var idsRule = deserializationService.getRule(value);
            idsRuleList.add(idsRule);
        }

        if (!PolicyUtils.compareRules(idsRuleList, (ArrayList<Rule>) requestRules)) {
            if (log.isDebugEnabled()) {
                log.debug("Rules do not match. [offer=({}), request=({})]", idsRuleList,
                        requestRules);
            }
            return false;
        }

        return true;
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

        final var current = PolicyUtils.getCurrentDate();
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
        final var validDate = PolicyUtils.checkDate(PolicyUtils.getCurrentDate(), maxTime);

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
