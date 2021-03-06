package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnsupportedPatternException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;

/**
 * This class provides access permission information for the {@link PolicyDecisionService}
 * depending on the policy content. Refers to the ids policy information point (PEP).
 */
@Service
@RequiredArgsConstructor
public class PolicyInformationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyInformationService.class);

    /**
     * Read the properties of an ids rule to automatically recognize the policy pattern.
     * TODO check if the rules are of the right type
     *
     * @param rule The ids rule.
     * @return The recognized policy pattern.
     */
    public PolicyPattern getPatternByRule(final Rule rule) {
        PolicyPattern detectedPattern = null;

        if (rule instanceof Prohibition) {
            detectedPattern = PolicyPattern.PROHIBIT_ACCESS;
        } else if (rule instanceof Permission) {
            final var constraints = rule.getConstraint();
            final var postDuties = ((Permission) rule).getPostDuty();

            if (constraints != null && constraints.get(0) != null) {
                if (constraints.size() > 1) {
                    if (postDuties != null && postDuties.get(0) != null) {
                        detectedPattern = PolicyPattern.USAGE_UNTIL_DELETION;
                    } else {
                        detectedPattern = PolicyPattern.USAGE_DURING_INTERVAL;
                    }
                } else {
                    final var leftOperand = constraints.get(0).getLeftOperand();
                    if (leftOperand == LeftOperand.COUNT) {
                        detectedPattern = PolicyPattern.N_TIMES_USAGE;
                    } else if (leftOperand == LeftOperand.ELAPSED_TIME) {
                        detectedPattern = PolicyPattern.DURATION_USAGE;
                    } else {
                        detectedPattern = null;
                    }
                }
            } else {
                if (postDuties != null && postDuties.get(0) != null) {
                    final var action = postDuties.get(0).getAction().get(0);
                    if (action == Action.NOTIFY) {
                        detectedPattern = PolicyPattern.USAGE_NOTIFICATION;
                    } else if (action == Action.LOG) {
                        detectedPattern = PolicyPattern.USAGE_LOGGING;
                    } else {
                        detectedPattern = null;
                    }
                } else {
                    detectedPattern = PolicyPattern.PROVIDE_ACCESS;
                }
            }
        }

        if (detectedPattern == null) {
            LOGGER.debug("No supported pattern could be recognized.");
            throw new UnsupportedPatternException("No supported pattern could be recognized.");
        }

        return detectedPattern;
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public Date getCreationDate(final URI element) {
        // find element by id
        // return its creation date TODO Do all entities have a creation date?
        return null;
    }

    public Integer getAccessNumber(final URI element) {
        // find element by id
        // return its number of accesses TODO Do all entities have a creation date?
        return null;
    }
}
