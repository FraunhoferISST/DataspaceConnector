package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

/**
 * Enum describing policy patterns supported by this ids connector.
 */
public enum PolicyPattern {

    /**
     * Standard pattern to allow unrestricted access.
     */
    PROVIDE_ACCESS("PROVIDE_ACCESS"),

    /**
     * Default pattern if no other is detected.
     */
    PROHIBIT_ACCESS("PROHIBIT_ACCESS"),

    /**
     * See <a href="https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/NTimesUsageTemplates/N_TIMES_USAGE_OFFER_TEMPLATE.jsonld">this example.</a>
     */
    N_TIMES_USAGE("N_TIMES_USAGE"),

    /**
     * See <a href="https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/TimeRestrictedUsageTemplates/DURATION_USAGE_OFFER_TEMPLATE.jsonld">this example.</a>
     */
    DURATION_USAGE("DURATION_USAGE"),

    /**
     * See <a href="https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/TimeRestrictedUsageTemplates/USAGE_DURING_INTERVAL_OFFER_TEMPLATE.jsonld">this example.</a>
     */
    USAGE_DURING_INTERVAL("USAGE_DURING_INTERVAL"),

    /**
     * See <a href="https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/TimeRestrictedUsageTemplates/USAGE_UNTIL_DELETION_OFFER_TEMPLATE.jsonld">this example.</a>
     */
    USAGE_UNTIL_DELETION("USAGE_UNTIL_DELETION"),

    /**
     * See <a href="https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/UsageLoggingTemplates/USAGE_LOGGING_OFFER_TEMPLATE.jsonld">this example.</a>
     */
    USAGE_LOGGING("USAGE_LOGGING"),

    /**
     * See <a href="https://github.com/International-Data-Spaces-Association/InformationModel/blob/master/examples/contracts-and-usage-policy/templates/UsageNotificationTemplates/USAGE_NOTIFICATION_OFFER_TEMPLATE.jsonld">this example.</a>
     */
    USAGE_NOTIFICATION("USAGE_NOTIFICATION");

    /**
     * The policy pattern name.
     */
    private final String pattern;

    PolicyPattern(final String string) {
        pattern = string;
    }

    @Override
    public String toString() {
        return pattern;
    }
}
