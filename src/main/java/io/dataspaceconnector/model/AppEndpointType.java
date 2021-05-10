package io.dataspaceconnector.model;

public enum AppEndpointType {

    CONFIG_ENDPOINT("Configuration Endpoint"),
    INPUT_ENDPOINT("Input Endpoint"),
    OUTPUT_ENDPOINT("Output Endpoint"),
    PROCESS_ENDPOINT("Process Endpoint"),
    SELF_DESCRIPTION_ENDPOINT("Self description Endpoint"),
    STATUS_ENDPOINT("Status Endpoint"),
    USAGE_POLICY_ENDPOINT("Usage Policy Endpoint");

    private final String value;

    AppEndpointType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
