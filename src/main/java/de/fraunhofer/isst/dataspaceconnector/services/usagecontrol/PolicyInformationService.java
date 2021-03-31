package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

    /**
     * Service for handling artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Read the properties of an ids rule to automatically recognize the policy pattern.
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
                    final var operator = constraints.get(0).getOperator();
                    if (leftOperand == LeftOperand.COUNT) {
                        detectedPattern = PolicyPattern.N_TIMES_USAGE;
                    } else if (leftOperand == LeftOperand.ELAPSED_TIME) {
                        detectedPattern = PolicyPattern.DURATION_USAGE;
                    } else if (leftOperand == LeftOperand.SYSTEM
                            && operator == BinaryOperator.SAME_AS) {
                        detectedPattern = PolicyPattern.CONNECTOR_RESTRICTED_USAGE;
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

        return detectedPattern;
    }

    /**
     * Get current system date.
     *
     * @return The date object.
     */
    public Date getCurrentDate() {
        return new Date();
    }

    /**
     * Get creation date of artifact.
     *
     * @param target The target id.
     * @return The artifact's creation date.
     */
    public Date getCreationDate(final URI target) {
        final var resourceId = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(resourceId);

        return artifact.getCreationDate();
    }

    /**
     * Get access number of artifact.
     *
     * @param target The target id.
     * @return The artifact's access number.
     */
    public long getAccessNumber(final URI target) {
        final var resourceId = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(resourceId);

        return artifact.getNumAccessed();
    }
}
