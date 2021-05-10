package io.dataspaceconnector.model;

public enum DeployMethod {

    NONE("None"),
    CAMEL("Camel"),
    KAFKA("Kafka"),
    ARGO("Argo"),
    AIRFLOW("Airflow");

    private final String value;

    DeployMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
