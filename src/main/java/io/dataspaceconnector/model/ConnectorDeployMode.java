package io.dataspaceconnector.model;

/**
 * Enumeration for connector deploy modes.
 */
public enum ConnectorDeployMode {

    /**
     * Connector is in productive deployment.
     */
    PRODUCTIVE_DEPLOYMENT("Productive Deployment"),
    /**
     * Connector is in test deployment.
     */
    TEST_DEPLOYMENT("Test Deployment");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     *
     * @param value The value of the enum.
     */
    ConnectorDeployMode(String value) {
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
