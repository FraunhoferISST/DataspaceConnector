package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RequestFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;

/**
 * This class provides policy pattern recognition and calls the {@link
 * de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyVerifier} on data request or
 * access.
 */
@Component
public class PolicyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyHandler.class);

    private static Contract contract;
    private final PolicyVerifier policyVerifier;
    private final SerializerProvider serializerProvider;
    private final PolicyConfiguration policyConfiguration;

    /**
     * Constructor for PolicyHandler.
     *
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public PolicyHandler(PolicyVerifier policyVerifier,
                         SerializerProvider serializerProvider,
                         PolicyConfiguration policyConfiguration)
        throws IllegalArgumentException {
        if (policyVerifier == null)
            throw new IllegalArgumentException("The PolicyVerifier cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (policyConfiguration == null)
            throw new IllegalArgumentException("The PolicyConfiguration cannot be null.");

        this.policyVerifier = policyVerifier;
        this.serializerProvider = serializerProvider;
        this.policyConfiguration = policyConfiguration;
    }

    /**
     * Deserializes a contract object from a string.
     *
     * @param contract the contract as a string.
     * @return the contract.
     * @throws RequestFormatException if the string is not a valid contract and could thus not be deserialized.
     */
    public Contract validateContract(String contract) throws RequestFormatException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, Contract.class);
        } catch (Exception e) {
            LOGGER.debug("Policy pattern is not supported.");
            throw new RequestFormatException("Contract could not be deserialized. ", e);
        }
    }

    /**
     * Reads the properties of an ODRL policy to automatically recognize the policy pattern.
     *
     * @param policy the policy as a string.
     * @return the recognized policy pattern.
     * @throws UnsupportedPatternException if no pattern could be recognized.
     * @throws RequestFormatException if the string could not be deserialized.
     */
    public Pattern getPattern(String policy) throws UnsupportedPatternException,
        RequestFormatException {
        contract = validateContract(policy);

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
                    } else if ((leftOperand == LeftOperand.SYSTEM )
                            && (constraints.get(0).getOperator() == BinaryOperator.SAME_AS))  {
                        return Pattern.CONNECTOR_RESTRICTED_USAGE;
                    } else {
                        throw new UnsupportedPatternException(
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
                        throw new UnsupportedPatternException(
                            "The recognized policy pattern is not supported by this connector.");
                    }
                } else {
                    return Pattern.PROVIDE_ACCESS;
                }
            }
        } else {
            throw new UnsupportedPatternException(
                "The recognized policy pattern is not supported by this connector.");
        }
    }

    /**
     * Implements the policy restrictions depending on the policy pattern type on data provision (as provider).
     *
     * @param policy the resource's usage policy.
     * @return whether the data can be provided.
     * @throws UnsupportedPatternException if no pattern could be recognized.
     * @throws RequestFormatException if the string could not be deserialized.
     */
    public boolean onDataProvision(String policy, URI issuerConnector) throws UnsupportedPatternException,
        RequestFormatException {
        switch (policyConfiguration.getUsageControlFramework()) {
            case INTERNAL:
                switch (getPattern(policy)) {
                    case PROVIDE_ACCESS:
                        return policyVerifier.allowAccess();
                    case PROHIBIT_ACCESS:
                        return policyVerifier.inhibitAccess();
                    case USAGE_DURING_INTERVAL:
                    case USAGE_UNTIL_DELETION:
                        return policyVerifier.checkInterval(contract);
                    case CONNECTOR_RESTRICTED_USAGE:
                        return policyVerifier.checkIssuerConnector(contract, issuerConnector);
                    default:
                        return true;
                }
            case MYDATA: // TODO
            case MYDATA_INTERCEPTOR: // TODO
            default:
                return true;
        }
    }

    /**
     * Implements the policy restrictions depending on the policy pattern type on data access (as consumer).
     *
     * @param dataResource the accessed resource.
     * @return whether the data can be accessed.
     * @throws UnsupportedPatternException if no pattern could be recognized.
     * @throws RequestFormatException if the string could not be deserialized.
     */
    public boolean onDataAccess(RequestedResource dataResource) throws UnsupportedPatternException,
        RequestFormatException{
        switch (policyConfiguration.getUsageControlFramework()) {
            case INTERNAL:
                break;
            case MYDATA: // TODO
            case MYDATA_INTERCEPTOR: // TODO
            default:
                return true;
        }

        final var policy = dataResource.getResourceMetadata().getPolicy();
        final var ignoreUnsupportedPatterns = policyConfiguration.isUnsupportedPatterns();

        Pattern pattern;
        try {
            pattern = getPattern(policy);
        } catch (UnsupportedPatternException exception) {
            if (!ignoreUnsupportedPatterns)
                throw new UnsupportedPatternException(exception.getMessage());
            else
                pattern = Pattern.PROVIDE_ACCESS;
        }

        switch (pattern) {
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
        USAGE_NOTIFICATION("USAGE_NOTIFICATION"),
        /**
         * Type: Connector-restricted access https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/ConnectorbasedAgreementTemplates/CONNECTORBASED_OFFER_TEMPLATE.jsonld
         */
        CONNECTOR_RESTRICTED_USAGE("CONNECTOR_RESTRICTED_USAGE");

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
