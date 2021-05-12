package io.dataspaceconnector.model;

/**
 * Enumeration for connector status.
 */
public enum ConnectorStatus {

    /**
     * Connector is badly configured.
     */
    CONNECTOR_BADLY_CONFIGURED("Connector badly configured"),
    /**
     * Connector is offline.
     */
    CONNECTOR_OFFLINE("Connector offline"),
    /**
     * Connector is online.
     */
    CONNECTOR_ONLINE("Connector online");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    ConnectorStatus(String value) {
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
