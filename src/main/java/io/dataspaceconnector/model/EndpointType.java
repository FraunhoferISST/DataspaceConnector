package io.dataspaceconnector.model;

/**
 * Enumeration for endpoint type.
 */
public enum EndpointType {

    /**
     * The generic endpoint.
     */
    GENERIC_ENDPOINT("Generic endpoint"),
    /**
     * The app endpoint.
     */
    APP_ENDPOINT("App endpoint"),
    /**
     * The ids endpoint.
     */
    IDS_ENDPOINT("IDS endpoint");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    EndpointType(String value) {
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
