package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;

@Component
@Log4j2
@RequiredArgsConstructor
public final class SimpleDataAccessVerifier implements PolicyVerifier<URI> {

    /**
     * The policy execution point.
     */
    private final @NonNull PolicyDecisionService decisionService;

    /**
     * Policy check on data access on consumer side. Ignore if unknown patterns are allowed.
     *
     * @param target The requested element.
     * @throws PolicyRestrictionException If a policy restriction has been detected.
     */
    public void checkPolicyOnDataAccess(final URI target) throws PolicyRestrictionException {
        final var patternsToCheck = Arrays.asList(
                PolicyPattern.PROVIDE_ACCESS,
                PolicyPattern.USAGE_DURING_INTERVAL,
                PolicyPattern.USAGE_UNTIL_DELETION,
                PolicyPattern.DURATION_USAGE,
                PolicyPattern.USAGE_LOGGING,
                PolicyPattern.N_TIMES_USAGE,
                PolicyPattern.USAGE_NOTIFICATION);

        decisionService.checkForDataAccess(patternsToCheck, target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerificationResult verify(final URI input) {
        try {
            this.checkPolicyOnDataAccess(input);
            return VerificationResult.ALLOWED;
        } catch (PolicyRestrictionException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Data access denied. [input=({})]", input, exception);
            }
            return VerificationResult.DENIED;
        }
    }
}
