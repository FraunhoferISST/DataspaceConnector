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

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.SecurityProfile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.common.time.TimeInterval;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.EntityDependencyResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class provides policy pattern recognition and calls the {@link
 * PolicyInformationService} on data
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
     * @param profile         The security profile.
     * @param agreementId     The id of the transfer contract (agreement).
     * @throws PolicyRestrictionException If a policy restriction was detected.
     */
    public void validatePolicy(final PolicyPattern pattern, final Rule rule, final URI target,
                               final URI issuerConnector, final Optional<SecurityProfile> profile,
                               final URI agreementId) throws PolicyRestrictionException {
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
                executionService.logDataAccess(target, agreementId);
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
            case SECURITY_PROFILE_RESTRICTED_USAGE:
                validateSecurityProfile(rule, profile);
                break;
            case PROHIBIT_ACCESS:
                throw new PolicyRestrictionException(ErrorMessage.NOT_ALLOWED);
            default:
                if (log.isDebugEnabled()) {
                    log.debug("No pattern detected. [target=({})]", target);
                }
                throw new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
        }
    }

    /**
     * Compare content of rule offer and request with each other.
     *
     * @param contractOffers The contract offer.
     * @param map            The target contract map.
     * @param target         The target value.
     * @return An optional of the matching contract; an empty optional if none was found.
     */
    public Optional<Contract> findMatchingContractForRequest(final List<Contract> contractOffers,
                                                             final Map<URI, List<Rule>> map,
                                                             final URI target) {
        for (final var contract : contractOffers) {
            // Get rule list from contract offer.
            final var ruleList = dependencyResolver.getRulesByContractOffer(contract);
            // Get rule list from contract request.
            final var values = map.get(target);

            // Compare rules
            if (compareRulesOfOfferToRequest(ruleList, values)) {
                return Optional.of(contract);
            }
        }

        return Optional.empty();
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
            idsRuleList.add(deserializationService.getRule(rule.getValue()));
        }

        if (!RuleUtils.compareRules(idsRuleList, requestRules)) {
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
            timeInterval = RuleUtils.getTimeInterval(rule);
        } catch (DateTimeParseException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not read time interval. [exception=({})]", e.getMessage());
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL, e);
        }

        final var current = RuleUtils.getCurrentDate();
        if (!current.isAfter(timeInterval.getStart()) || !current.isBefore(timeInterval.getEnd())) {
            if (log.isWarnEnabled()) {
                log.warn("Invalid time interval. [timeInterval=({})]", timeInterval);
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL);
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
            duration = RuleUtils.getDuration(rule);
        } catch (DateTimeParseException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not read duration. [target=({}), exception=({})]",
                        target, e.getMessage(), e);
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL, e);
        }

        if (duration == null) {
            if (log.isWarnEnabled()) {
                log.warn("Duration is null. [target=({})]", target);
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL);
        }

        if (RuleUtils.isExpired(RuleUtils.getCalculatedDate(created, duration))) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid date time. [target=({})]", target);
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_INVALID_INTERVAL);
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
        final var max = RuleUtils.getMaxAccess(rule);
        final var accessed = informationService.getAccessNumber(target);
        if (accessed >= max) {
            if (log.isDebugEnabled()) {
                log.debug("Access number reached. [target=({})]", target);
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_NUMBER_REACHED);
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
        final var allowedConsumer = RuleUtils.getEndpoint(rule);
        final var allowedConsumerAsUri = URI.create(allowedConsumer);
        if (!allowedConsumerAsUri.equals(issuerConnector)) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid consumer connector. [issuer=({})]", issuerConnector);
            }
            throw new PolicyRestrictionException(ErrorMessage.DATA_ACCESS_INVALID_CONSUMER);
        }
    }

    /**
     * Checks whether the requesting connector has the right security level.
     *
     * @param rule    The ids rule.
     * @param profile The security profile.
     * @throws PolicyRestrictionException If the connector ids do no match.
     */
    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    private void validateSecurityProfile(final Rule rule, final Optional<SecurityProfile> profile)
            throws PolicyRestrictionException {
        if (profile.isEmpty()) {
            throw new PolicyRestrictionException(ErrorMessage.MISSING_SECURITY_PROFILE_CLAIM);
        }

        try {
            final var allowedProfile = RuleUtils.getSecurityProfile(rule);
            final var securityProfile = profile.get();
            if (!allowedProfile.equals(securityProfile.toString())) {
                throw new PolicyRestrictionException(
                        ErrorMessage.DATA_ACCESS_INVALID_SECURITY_PROFILE);
            }
        } catch (NullPointerException e) {
            throw new PolicyRestrictionException(
                    ErrorMessage.DATA_ACCESS_INVALID_SECURITY_PROFILE);
        }
    }
}
