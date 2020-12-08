package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class provides policy pattern recognition and calls the {@link
 * de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyVerifier} on data request or
 * access.
 */
@Component
public class PolicyHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(PolicyHandler.class);

    public static Contract contract;
    private PolicyVerifier policyVerifier;
    private SerializerProvider serializerProvider;

    @Autowired
    /**
     * Constructor for PolicyHandler.
     */
    public PolicyHandler(PolicyVerifier policyVerifier, SerializerProvider serializerProvider) {
        this.policyVerifier = policyVerifier;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Reads the properties of an odrl policy to automatically recognize the policy pattern.
     *
     * @param policy The parsed policy object.
     * @return The recognized policy pattern.
     * @throws java.io.IOException if any.
     */
    public Pattern getPattern(String policy) throws IOException {
        try {
            contract = serializerProvider.getSerializer().deserialize(policy, Contract.class);
        } catch (IOException e) {
            throw new IOException("The policy could not be read. Please check the policy syntax.");
        }

        if (contract.getProhibition() != null && contract.getProhibition().get(0) != null) {
            return Pattern.PROHIBIT_ACCESS;
        }

        if (contract.getPermission() != null && contract.getPermission().get(0) != null) {
            Permission permission = contract.getPermission().get(0);
            ArrayList<? extends Constraint> constraints = permission.getConstraint();
            ArrayList<? extends Duty> postDuties = permission.getPostDuty();

            if (constraints != null && constraints.get(0) != null) {
                if (constraints.size() > 1) {
                    if (postDuties != null && postDuties.get(0) != null) {
                        return Pattern.USAGE_UNTIL_DELETION;
                    } else {
                        return Pattern.USAGE_DURING_INTERVAL;
                    }
                } else {
                    LeftOperand leftOperand = constraints.get(0).getLeftOperand();
                    if (leftOperand == LeftOperand.COUNT) {
                        return Pattern.N_TIMES_USAGE;
                    } else if (leftOperand == LeftOperand.ELAPSED_TIME) {
                        return Pattern.DURATION_USAGE;
                    } else {
                        throw new IOException(
                            "The recognized policy pattern is not supported by this connector.");
                    }
                }
            } else {
                if (postDuties != null && postDuties.get(0) != null) {
                    Action action = postDuties.get(0).getAction().get(0);
                    if (action == Action.NOTIFY) {
                        return Pattern.USAGE_NOTIFICATION;
                    } else if (action == Action.LOG) {
                        return Pattern.USAGE_LOGGING;
                    } else {
                        throw new IOException(
                            "The recognized policy pattern is not supported by this connector.");
                    }
                } else {
                    return Pattern.PROVIDE_ACCESS;
                }
            }
        } else {
            throw new IOException(
                "The recognized policy pattern is not supported by this connector.");
        }
    }

    /**
     * Implements the policy restrictions depending on the policy pattern type (on artifact request
     * as provider).
     *
     * @param policy The resource's usage policy.
     * @return Whether the data can be accessed.
     * @throws java.io.IOException if any.
     */
    public boolean onDataProvision(String policy) throws IOException {
        switch (getPattern(policy)) {
            case PROVIDE_ACCESS:
                return policyVerifier.allowAccess();
            case PROHIBIT_ACCESS:
                return policyVerifier.inhibitAccess();
            case USAGE_DURING_INTERVAL:
            case USAGE_UNTIL_DELETION:
                return policyVerifier.checkInterval(contract);
            default:
                return true;
        }
    }

    /**
     * Implements the policy restrictions depending on the policy pattern type (on data access as
     * consumer).
     *
     * @param dataResource The accessed resource.
     * @return Whether the data can be accessed.
     * @throws java.io.IOException if any.
     */
    public boolean onDataAccess(RequestedResource dataResource) throws IOException {
        String policy = dataResource.getResourceMetadata().getPolicy();

        switch (getPattern(policy)) {
            case USAGE_DURING_INTERVAL:
            case USAGE_UNTIL_DELETION:
                return policyVerifier.checkInterval(contract);
            case DURATION_USAGE:
                return policyVerifier.checkDuration(dataResource.getCreated(), contract);
            case USAGE_LOGGING:
                return policyVerifier.logAccess();
            case N_TIMES_USAGE:
                return policyVerifier.checkFrequency(contract, dataResource.getUuid());
            case USAGE_NOTIFICATION:
                return policyVerifier.sendNotification(contract);
            default:
                return true;
        }
    }

    public enum Pattern {
        /**
         * Standard pattern to allow unrestricted access.
         */
        PROVIDE_ACCESS("PROVIDE_ACCESS"),
        /**
         * Default pattern if no other is detected. v2.0: NO_POLICY("no-policy")
         */
        PROHIBIT_ACCESS("PROHIBIT_ACCESS"),
        /**
         * Type: NotMoreThanN v2.0: COUNT_ACCESS("count-access") https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/NTimesUsageTemplates/N_TIMES_USAGE_OFFER_TEMPLATE.jsonld
         */
        N_TIMES_USAGE("N_TIMES_USAGE"),
        /**
         * Type: DurationOffer https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/TimeRestrictedUsageTemplates/DURATION_USAGE_OFFER_TEMPLATE.jsonld
         */
        DURATION_USAGE("DURATION_USAGE"),
        /**
         * Type: IntervalUsage v2.0: TIME_INTERVAL("time-interval") https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/TimeRestrictedUsageTemplates/USAGE_DURING_INTERVAL_OFFER_TEMPLATE.jsonld
         */
        USAGE_DURING_INTERVAL("USAGE_DURING_INTERVAL"),
        /**
         * Type: DeleteAfterInterval v2.0: DELETE_AFTER("delete-after")
         * https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/TimeRestrictedUsageTemplates/USAGE_UNTIL_DELETION_OFFER_TEMPLATE.jsonld
         */
        USAGE_UNTIL_DELETION("USAGE_UNTIL_DELETION"),
        /**
         * Type: Logging v2.0: LOG_ACCESS("log-access") https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/UsageLoggingTemplates/USAGE_LOGGING_OFFER_TEMPLATE.jsonld
         */
        USAGE_LOGGING("USAGE_LOGGING"),
        /**
         * Type: Notification https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/UsageNotificationTemplates/USAGE_NOTIFICATION_OFFER_TEMPLATE.jsonld
         */
        USAGE_NOTIFICATION("USAGE_NOTIFICATION");

        private final String pattern;

        Pattern(String string) {
            pattern = string;
        }

        @Override
        public String toString() {
            return pattern;
        }
    }
}
