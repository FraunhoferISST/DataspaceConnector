package io.dataspaceconnector.model;

/**
 * Enumeration for deploy method.
 */
public enum DeployMethod {

    /**
     * Deploy method is none.
     */
    NONE("None"),
    /**
     * Deploy method is camel.
     */
    CAMEL("Camel"),
    /**
     * Deploy method is kafka.
     */
    KAFKA("Kafka"),
    /**
     * Deploy method is argo.
     */
    ARGO("Argo"),
    /**
     * Deploy method is airflow.
     */
    AIRFLOW("Airflow");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    DeployMethod(String value) {
        this.value = value;
    }

    /**
     * @return Enum value.
     */
    @Override
    public String toString() {
        return value;
    }
}
