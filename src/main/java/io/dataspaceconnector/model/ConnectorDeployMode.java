package io.dataspaceconnector.model;

public enum ConnectorDeployMode {

    PRODUCTIVE_DEPLOYMENT("Productive Deployment"),
    TEST_DEPLOYMENT("Test Deployment");

    private final String value;

    ConnectorDeployMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
