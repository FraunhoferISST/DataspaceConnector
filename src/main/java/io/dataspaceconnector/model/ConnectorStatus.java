package io.dataspaceconnector.model;

public enum ConnectorStatus {

    CONNECTOR_BADLY_CONFIGURED("Connector badly configured"),
    CONNECTOR_OFFLINE("Connector offline"),
    CONNECTOR_ONLINE("Connector online");

    private final String value;

    ConnectorStatus(String value){this.value = value;}

    @Override
    public String toString() {
        return value;
    }
}
