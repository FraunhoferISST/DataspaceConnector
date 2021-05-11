package io.dataspaceconnector.model;

/**
 * Enum describing app endpoint types.
 */
public enum AppEndpointType {

    /**
     * Endpoint is used for runtime-related app configurations and app parameters.
     */
    CONFIG_ENDPOINT("Configuration Endpoint"),
    /**
     * Endpoint is used for data input.
     */
    INPUT_ENDPOINT("Input Endpoint"),
    /**
     * Endpoint is used for data output.
     */
    OUTPUT_ENDPOINT("Output Endpoint"),
    /**
     * Endpoint is used to start (or control) app processing.
     */
    PROCESS_ENDPOINT("Process Endpoint"),
    /**
     * Pre-defined endpoint used to return the corresponding self-description of the data app.
     */
    SELF_DESCRIPTION_ENDPOINT("Self description Endpoint"),
    /**
     * Endpoint is used to get app health / lifecycle status.
     */
    STATUS_ENDPOINT("Status Endpoint"),
    /**
     * Endpoint is used for usage policy / usage control related scenarios.
     */
    USAGE_POLICY_ENDPOINT("Usage Policy Endpoint");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    AppEndpointType(String value) {
        this.value = value;
    }

    /**
     * @return The value.
     */
    @Override
    public String toString() {
        return value;
    }

}
