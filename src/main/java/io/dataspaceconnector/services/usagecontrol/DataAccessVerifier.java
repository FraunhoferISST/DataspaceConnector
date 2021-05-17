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
package io.dataspaceconnector.services.usagecontrol;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.exceptions.PolicyExecutionException;
import io.dataspaceconnector.exceptions.PolicyRestrictionException;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.RuleUtils;
import io.dataspaceconnector.utils.SelfLinkHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * A {@link PolicyVerifier} implementation that checks whether data access should be allowed.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public final class DataAccessVerifier implements PolicyVerifier<Artifact> {

    /**
     * The policy execution point.
     */
    private final @NonNull RuleValidator ruleValidator;

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Policy check on data access on consumer side. Ignore if unknown patterns are allowed.
     *
     * @param target The requested element.
     * @throws PolicyRestrictionException If a policy restriction has been detected.
     */
    public void checkPolicy(final Artifact target) throws PolicyRestrictionException {
        final var patternsToCheck = Arrays.asList(
                PolicyPattern.PROVIDE_ACCESS,
                PolicyPattern.USAGE_DURING_INTERVAL,
                PolicyPattern.USAGE_UNTIL_DELETION,
                PolicyPattern.DURATION_USAGE,
                PolicyPattern.USAGE_LOGGING,
                PolicyPattern.N_TIMES_USAGE,
                PolicyPattern.USAGE_NOTIFICATION);

        try {
            final var artifactId = SelfLinkHelper.getSelfLink(target);
            checkForAccess(patternsToCheck, artifactId, target.getRemoteId());
        } catch (PolicyRestrictionException exception) {
            // Unknown patterns cause an exception. Ignore if unsupported patterns are allowed.
            if (!connectorConfig.isAllowUnsupported()) {
                throw exception;
            }
        }
    }

    /**
     * Checks the contract content for data access (on consumer side).
     *
     * @param patterns   List of patterns that should be enforced.
     * @param artifactId The requested artifact.
     * @param remoteId   The remote id of the requested artifact.
     * @throws io.dataspaceconnector.exceptions.UnsupportedPatternException
     *         If no suitable pattern could be found.
     */
    public void checkForAccess(final List<PolicyPattern> patterns, final URI artifactId,
                               final URI remoteId) {
        // Get the contract agreement's rules for the target.
        final var agreements = entityResolver.getContractAgreementsByTarget(artifactId);
        for (final var agreement : agreements) {
            final var rules = ContractUtils.getRulesForTargetId(agreement, remoteId);

            // Check the policy of each rule.
            for (final var rule : rules) {
                final var pattern = RuleUtils.getPatternByRule(rule);
                // Enforce only a set of patterns.
                if (patterns.contains(pattern)) {
                    ruleValidator.validatePolicy(pattern, rule, artifactId, null);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerificationResult verify(final Artifact input) {
        try {
            this.checkPolicy(input);
            return VerificationResult.ALLOWED;
        } catch (PolicyRestrictionException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Data access denied. [input=({})]", input, exception);
            }
            return VerificationResult.DENIED;
        } catch (PolicyExecutionException e) {
            // If message could not be sent, ignore and provide access anyway.
            if (log.isDebugEnabled()) {
                log.debug("[exception=({}), input=({})]", e.getMessage(), input, e);
            }
            return VerificationResult.ALLOWED;
        }
    }
}
