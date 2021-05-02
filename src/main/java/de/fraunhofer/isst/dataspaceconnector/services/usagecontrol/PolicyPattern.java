package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

/**
 * Enum describing policy patterns supported by this ids connector.
 */
public enum PolicyPattern {

    /**
     * Standard pattern to allow unrestricted access.
     */
    PROVIDE_ACCESS,

    /**
     * Default pattern if no other is detected.
     */
    PROHIBIT_ACCESS,

    /**
     * Use the data not more than N times.
     */
    N_TIMES_USAGE,

    /**
     * Restrict the data usage to a specific time duration.
     */
    DURATION_USAGE,

    /**
     * Restrict the data usage to a specific time interval.
     */
    USAGE_DURING_INTERVAL,

    /**
     * Use data and delete it at a specific date time.
     */
    USAGE_UNTIL_DELETION,

    /**
     * Log the data usage information.
     */
    USAGE_LOGGING,

    /**
     * Notify a party or a specific group of users when the data is used.
     */
    USAGE_NOTIFICATION,

    /**
     * Restrict the data usage to specific connectors.
     */
    CONNECTOR_RESTRICTED_USAGE
}
